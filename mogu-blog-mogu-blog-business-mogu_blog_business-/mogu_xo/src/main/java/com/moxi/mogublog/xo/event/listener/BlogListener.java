package com.moxi.mogublog.xo.event.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.commons.vo.SubjectItemVO;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.event.blogEvent.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 遇见
 */
@Component
@Slf4j
public class BlogListener {
    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 管理员服务
     */
    @Resource
    private AdminService adminService;

    /**
     * 简易的mq邮件封装工具类
     */
    @Resource
    private RabbitMqUtil rabbitMqUtil;

    /**
     * 异步执行服务
     */
    @Resource
    private AsyncService asyncService;

    /**
     * 系统配置；服务
     */
    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 博客服务
     */
    @Resource
    private BlogService blogService;

    @Resource
    private SubjectItemService subjectItemService;


    /**
     * todo 2021/12/14  通知服务调用
     */

    /**
     * 博客新增事件
     *
     * @param event
     */
    @EventListener(value = BlogAddEvent.class)
    public void add(BlogEvent event) {
        Blog blog = (Blog) event.getSource();
        /**
         * 新增情况下 审核通过  说明是免审
         * 一般都不会走这一层
         * 博主属于特殊角色
         * 在新增博客选择发布时需要给与积分
         */
        if (EAuditStatus.AGREE.equals(blog.getAuditStatus()) && EPublish.PUBLISH.equals(blog.getIsPublish())) {
            /**
             * 站内信通知
             */
            this.sendNotice(blog);

            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());

            /**
             * 更新专栏，目前只开放运营添加专栏
             */
            if (event.isAdmin()) {
                this.upsertSubject(blog);
            }
        }

        /**
         * 新增时候选择发布的博客都给与网站管理员发送邮件提示
         */
        if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
            /**
             * 通知运营
             */
            this.sendEmailToManager(blog);
        }

        //发布后台系统消息通知
        if (!EAuditStatus.AGREE.equals(blog.getAuditStatus()) || !EPublish.PUBLISH.equals(blog.getIsPublish())) {
            this.sendBlackNotice(blog);
        }

        /**
         * 更新专栏，目前只开放运营添加专栏
         */
        if (event.isAdmin()) {
            this.upsertSubject(blog);
        }

        // 更新索引
        this.syncAggEsDoc(blog, EOperate.ADD);
    }

    /**
     * 博客修改事件
     *
     * @param event
     */
    @EventListener(value = BlogEditEvent.class)
    public void edit(BlogEvent event) {
        Blog blog = (Blog) event.getSource();

        if (EPublish.PUBLISH.equals(blog.getIsPublish()) && EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
        }

        /**
         * 更新专栏，目前只开放运营添加专栏
         */
        if (event.isAdmin()) {
            this.upsertSubject(blog);
        }

        // 更新索引
        this.syncAggEsDoc(blog, EOperate.EDIT);
    }

    /**
     * 博客审核事件
     *
     * @param event
     */
    @EventListener(value = BlogAuditEvent.class)
    public void audit(BlogEvent event) {
        Blog blog = (Blog) event.getSource();
        if (EPublish.PUBLISH.equals(blog.getIsPublish()) && EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
            /**
             * 站内信
             */
            this.sendNotice(blog);
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());

            /**
             * 更新专栏，目前只开放运营添加专栏
             */
            if (event.isAdmin()) {
                this.upsertSubject(blog);
            }
        }

        /**
         * 对作者发送邮件和站内信
         */
        if (blog.getUserUid() != null) {
            User user = userService.getById(blog.getUserUid());
            StringBuilder text = new StringBuilder();
            text.append(String.format("您的稿件 %s ", blog.getTitle()));
            if (EAuditStatus.REJECT.equals(blog.getAuditStatus())) {
                text.append("审核未通过，未通过原因：" + blog.getRejectReason());
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else if (EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
                text.append("审核已通过");
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else {
                log.error("审核状态异常,稿件为{}", blog.getUid());
            }
            // 发送文章审核通过
            sendAuditNotice(blog, text.toString());
        }
        // 更新索引
        this.syncAggEsDoc(blog, EOperate.EDIT);
    }

    /**
     * 博客发布事件
     *
     * @param event
     */
    @EventListener(value = BlogPublishEvent.class)
    public void publish(BlogEvent event) {
        Blog blog = (Blog) event.getSource();

        /**
         * 站内信
         */
        this.sendNotice(blog);
        /**
         * 管理员发布
         */
        if (event.isAdmin()) {
            /**
             * 管理员下架的话
             */
            if (EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
                HttpServletRequest request = RequestHolder.getRequest();
                Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());
                blog.setAuditName(admin.getNickName());
                blog.setAuditStatus(EAuditStatus.REJECT);
                blog.setRejectReason("管理员下架文章");
                blog.setAuditTime(new Date());

                User user = userService.getOne(new LambdaQueryWrapper<User>()
                        .eq(User::getUid, blog.getUserUid())
                );
                StringBuilder text = new StringBuilder();
                text.append(String.format("<p>亲爱的的博主，您的博客稿件 %s 被管理员下架，如有疑问请联系管理员</p>", blog.getTitle()));
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());

                // 发送站内信，文章被下架
                sendAuditNotice(blog, text.toString());

                return;
            }
            /**
             * 管理员上架
             */
            if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
                userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
            }
        } else {
            /**
             * 用户发布
             */
            if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
                /**
                 * 通知运营
                 */
                sendEmailToManager(blog);
                /**
                 * 增加积分
                 */
                userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
            }
        }

        /**
         * 更新索引
         */
        this.syncAggEsDoc(blog, EOperate.EDIT);
    }

    /**
     * 博客删除事件
     *
     * @param event
     */
    @EventListener(value = BlogDeleteEvent.class)
    public void delete(BlogEvent event) {
        Blog blog = (Blog) event.getSource();
        /**
         * 判断是否是用户发表的文章，删除对应的积分
         */
        if (StringUtils.isNotEmpty(blog.getUid())) {
            userService.addUserCredits(ECreditType.Delete_Blog.getAction(), null, blog.getUid(), blog.getUserUid());
        }
        /**
         * 更新索引
         */
        this.syncAggEsDoc(blog, EOperate.DELETE);
    }

    /**
     * 发送通知
     *
     * @param blog
     */
    public void sendNotice(Blog blog) {
        /**
         * 上架操作
         */
        if (EAuditStatus.AGREE.equals(blog.getAuditStatus()) && EPublish.PUBLISH.equals(blog.getIsPublish())) {
            /**
             * 站内信通知
             */
            if (blog.getUserUid() != null) {
                asyncService.executeAsyncBusinessNotice(false, blog.getUserUid(), blog.getUid(), EBusinessType.BLOG.getCode());
            } else {
                asyncService.executeAsyncBusinessNotice(true, blog.getAdminUid(), blog.getUid(), EBusinessType.BLOG.getCode());
            }
        }
    }

    /**
     * 发送后台通知
     *
     * @param blog
     */
    public void sendBlackNotice(Blog blog) {
        /**
         * 进入审批流程的博客 会给后台发送站内信通知
         */
        if (!EAuditStatus.AGREE.equals(blog.getAuditStatus()) || !EPublish.PUBLISH.equals(blog.getIsPublish())) {
            asyncService.executeAsyncBusinessBlackNotice(false, blog.getUserUid(), blog.getUid(), EBusinessType.BLOG.getCode(), blog.getTitle());
        }

    }

    /**
     * 发送邮件通知
     *
     * @param blog
     */
    public void sendEmailToManager(Blog blog) {
        /**
         * 发送邮件用纸
         */
        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
            if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                log.info("发送邮件通知");
                String emailContent = "收到用户的投稿文章：" + blog.getTitle();
                rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), emailContent);
            } else {
                log.error("网站没有配置通知接收的邮箱地址！");
            }
        }
    }

    /**
     * 同步AggEsDoc
     *
     * @param blog
     * @return
     */
    private void syncAggEsDoc(Blog blog, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertBlog(blog);
        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
            log.info("syncAggEsDoc: aggEsDoc oid is nil");
            Blog blogTemp = blogService.getById(blog.getUid());
            if (blogTemp != null) {
                aggEsDoc.setOid(blogTemp.getOid());
            }
            /**
             * 更新索引
             */
        }
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }

    /**
     * 批量更新专栏【可能删除，或者新增】
     */
    private void upsertSubject(Blog blog) {
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.BLOG_UID, blog.getUid());
        List<SubjectItem> subjectItemList = subjectItemService.list(queryWrapper);
        List<Subject> itemList = blog.getSubjectList();
        List<String> sub1 = new ArrayList<>();
        List<String> sub2 = new ArrayList<>();
        Map<String, SubjectItem> subjectItemMap = new HashMap<>();
        for (SubjectItem subjectItem : subjectItemList) {
            sub1.add(subjectItem.getSubjectUid());
            if (StringUtils.isNotEmpty(subjectItem.getSubjectUid()) && StringUtils.isNotEmpty(subjectItem.getBlogUid())) {
                subjectItemMap.put(String.format("%s_%s", subjectItem.getSubjectUid(), subjectItem.getBlogUid()), subjectItem);
            }

        }
        if (itemList != null) {
            for (Subject subjectItem : itemList) {
                sub2.add(subjectItem.getUid());
            }
        }

        // 需要新增的list
        List<String> insertList = this.subList(sub2, sub1);
        // 需要删除的list
        List<String> deleteList = this.subList(sub1, sub2);

        // 构造插入专栏的list
        List<SubjectItemVO> insertSubjectItemVOList = new ArrayList<>();
        for (String subjectUid : insertList) {
            SubjectItemVO subjectItemVO = new SubjectItemVO();
            subjectItemVO.setBlogUid(blog.getUid());
            subjectItemVO.setSubjectUid(subjectUid);
            insertSubjectItemVOList.add(subjectItemVO);
        }
        if (insertSubjectItemVOList.size() > 0) {
            subjectItemService.addSubjectItemList(insertSubjectItemVOList);
        }
        // 构建删除
        List<SubjectItemVO> deleteSubjectItemVOList = new ArrayList<>();
        for (String subjectUid : deleteList) {
            SubjectItemVO subjectItemVO = new SubjectItemVO();
            SubjectItem subjectItem = subjectItemMap.get(String.format("%s_%s", subjectUid, blog.getUid()));
            if (subjectItem != null) {
                subjectItemVO.setUid(subjectItem.getUid());
            }
            deleteSubjectItemVOList.add(subjectItemVO);
        }
        if (deleteSubjectItemVOList.size() > 0) {
            subjectItemService.deleteBatchSubjectItem(deleteSubjectItemVOList);
        }
        log.info("更新专栏信息完毕");
    }


    /**
     * 差集(基于java8新特性)优化解法2 适用于大数据量
     * * 求List1中有的但是List2中没有的元素
     *
     * @param list1
     * @param list2
     * @return
     */
    public List<String> subList(List<String> list1, List<String> list2) {
        Map<String, String> tempMap = list2.parallelStream().collect(Collectors.toMap(Function.identity(), Function.identity(), (oldData, newData) -> newData));
        return list1.parallelStream().filter(str -> {
            return !tempMap.containsKey(str);
        }).collect(Collectors.toList());
    }

    /**
     * 发送审核通知
     *
     * @param blog
     * @param text
     */
    private void sendAuditNotice(Blog blog, String text) {
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUserUid(blog.getUserUid());
        noticeVO.setContent(text);
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeVO.setBusinessType(EBusinessType.BLOG_AUDIT.getCode());
        noticeVO.setBusinessUid(blog.getUid());
        noticeVO.setIsBlack(Constants.NUM_ZERO);
        asyncService.executeAsyncBusinessNotice(noticeVO);
    }

}
