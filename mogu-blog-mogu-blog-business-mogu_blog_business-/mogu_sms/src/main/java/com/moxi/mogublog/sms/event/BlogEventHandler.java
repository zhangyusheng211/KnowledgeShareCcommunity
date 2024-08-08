package com.moxi.mogublog.sms.event;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.commons.vo.UserSubscribeVO;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.commons.vo.SubjectItemVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 博客事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.BLOG})
public class BlogEventHandler extends AbstractEventHandler {

    @Value("${data.webSite.url}")
    private String webSiteUrl;

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Blog blog = objectMapper.convertValue(domainEvent.getEntity(), Blog.class);
        switch (domainEvent.getEventAction()) {
            case BLOG_ADD: {
                this.add(blog, domainEvent.isAdminOperate());
            }
            break;
            case BLOG_EDIT: {
                this.edit(blog, domainEvent.isAdminOperate());
            }
            break;
            case BLOG_DELETE: {
                this.delete(blog, domainEvent.isAdminOperate());
            }
            break;
            case BLOG_AUDIT: {
                this.audit(blog, domainEvent.isAdminOperate());
            }
            break;
            case BLOG_PUBLISH: {
                this.publish(blog, domainEvent.isAdminOperate());
            }
            break;
            case BLOG_VISIT: {
                this.visit(blog, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 博客新增事件
     *
     * @param blog
     * @param isAdminOperate
     */
    public void add(Blog blog, boolean isAdminOperate) {
        /**
         * 新增情况下 审核通过  说明是免审
         * 一般都不会走这一层
         * 博主属于特殊角色
         * 在新增博客选择发布时需要给与积分
         */
        if (EAuditStatus.AGREE.equals(blog.getAuditStatus()) && EPublish.PUBLISH.equals(blog.getIsPublish())) {
            // 如果是发布的仅专栏可见的文章，不发送站内信
            if (blog.getIsOnlySubjectShow() != 1) {
                // 站内信通知
                this.sendNotice(blog);
            }

            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
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

        // 发布后台系统消息通知
        if (!EAuditStatus.AGREE.equals(blog.getAuditStatus()) || !EPublish.PUBLISH.equals(blog.getIsPublish())) {
            this.sendBlackNotice(blog);
        }

        /**
         * 更新专栏，目前只开放运营添加专栏
         */
        List<String> insertSubjectUidList = new ArrayList<>();
        if (isAdminOperate) {
            insertSubjectUidList = this.upsertSubject(blog, EventAction.BLOG_ADD);
        }
        // 发送专栏新增消息
        this.sendUserSubscribeMessage(blog, insertSubjectUidList);

        // 更新ES索引
        this.syncAggEsDoc(blog, EOperate.ADD);

        // 执行文章发布任务
        userTaskService.action(Action.BLOG, null, null);
    }

    /**
     * 博客修改事件
     *
     * @param blog
     * @param isAdminOperate
     */
    private void edit(Blog blog, boolean isAdminOperate) {
        if (EPublish.PUBLISH.equals(blog.getIsPublish()) && EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
        }

        /**
         * 更新专栏，目前只开放运营添加专栏
         */
        List<String> insertSubjectUidList = new ArrayList<>();
        if (isAdminOperate) {
            insertSubjectUidList = this.upsertSubject(blog, EventAction.BLOG_EDIT);
        }

        // 发送专栏新增消息
        this.sendUserSubscribeMessage(blog, insertSubjectUidList);

        // 清空用户对该文章的访问信息
        this.deleteUserVisitAuthCache(blog.getUid());

        // 更新索引
        this.syncAggEsDoc(blog, EOperate.EDIT);
    }

    /**
     * 博客审核事件
     *
     * @param blog
     * @param isAdminOperate
     */
    private void audit(Blog blog, boolean isAdminOperate) {
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
            List<String> insertSubjectUidList = new ArrayList<>();
            if (isAdminOperate) {
                insertSubjectUidList = this.upsertSubject(blog, EventAction.BLOG_AUDIT);
            }

            // 发送专栏新增消息
            this.sendUserSubscribeMessage(blog, insertSubjectUidList);
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
     * @param blog
     * @param isAdminOperate
     */
    private void publish(Blog blog, boolean isAdminOperate) {
        /**
         * 站内信
         */
        this.sendNotice(blog);
        /**
         * 管理员发布
         */
        if (isAdminOperate) {
            /**
             * 管理员下架的话
             */
            if (EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
                String adminUid = ThreadLocalUtil.getAdminUid();
                Admin admin = adminService.getById(adminUid);
                if (admin != null) {
                    blog.setAuditName(admin.getNickName());
                    blog.setAuditStatus(EAuditStatus.REJECT);
                    blog.setRejectReason("管理员下架文章");
                    blog.setAuditTime(new Date());
                }

                User user = userService.getOne(new LambdaQueryWrapper<User>()
                        .eq(User::getUid, blog.getUserUid())
                );
                if (user != null) {
                    StringBuilder text = new StringBuilder();
                    text.append(String.format("<p>博主，你投稿的文章: %s 被管理员下架，如有疑问请联系管理员.</p>", blog.getTitle()));
                    rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
                    // 发送站内信，文章被下架
                    sendAuditNotice(blog, text.toString());
                }
            }
            /**
             * 管理员上架
             */
            if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
                userService.addUserCredits(ECreditType.Blog.getAction(), null, blog.getUid(), blog.getUserUid());
                /**
                 * 发送专栏新增消息
                 */
                if (blog.getSubjectList() != null && blog.getSubjectList().size() > 0) {
                    List<String> insertSubjectUidList = blog.getSubjectList().stream().map(Subject::getUid).collect(Collectors.toList());
                    this.sendUserSubscribeMessage(blog, insertSubjectUidList);
                }
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

                /**
                 * 发送专栏新增消息
                 */
                List<String> insertSubjectUidList = blog.getSubjectList().stream().map(Subject::getUid).collect(Collectors.toList());
                this.sendUserSubscribeMessage(blog, insertSubjectUidList);
            }
        }

        // 发布文章时，都需要更新专栏
        this.upsertSubject(blog, EventAction.BLOG_PUBLISH);

        /**
         * 更新索引
         */
        this.syncAggEsDoc(blog, EOperate.EDIT);
    }

    /**
     * 博客删除事件
     *
     * @param blog
     * @param isAdminOperate
     */
    private void delete(Blog blog, boolean isAdminOperate) {
        /**
         * 判断是否是用户发表的文章，删除对应的积分
         */
        if (StringUtils.isNotEmpty(blog.getUid())) {
            userService.addUserCredits(ECreditType.Delete_Blog.getAction(), null, blog.getUid(), blog.getUserUid());
        }

        // 移除所有包含该博客的专题元素
        List<String> blogUidList = new ArrayList<>(Constants.NUM_ONE);
        blogUidList.add(blog.getUid());
        subjectItemService.deleteBatchSubjectItemByBlogUid(blogUidList);

        // 移除该文章下所有评论
        commentService.batchDeleteCommentByBlogUid(blogUidList);

        // 清空用户对该文章的访问信息
        this.deleteUserVisitAuthCache(blog.getUid());
        /**
         * 更新索引
         */
        this.syncAggEsDoc(blog, EOperate.DELETE);
    }

    private void visit(Blog blog, boolean isAdminOperate) {

        // 执行文章任务
        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.TO_USER_UID, blog.getUserUid());
        map.put(SysConf.BLOG_UID, blog.getUid());
        // 执行博客被访问任务
        userTaskService.action(Action.TO_BLOG_VISIT, null, map, blog.getUserUid());
        // 执行博客访问任务
        if (StringUtils.isNotEmpty(ThreadLocalUtil.getUserUid())) {
            userTaskService.action(Action.BLOG_VISIT, null, new HashMap<>());
        }
    }

    /**
     * 发送通知
     *
     * @param blog
     */
    private void sendNotice(Blog blog) {
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
    private void sendBlackNotice(Blog blog) {
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
    private void sendEmailToManager(Blog blog) {
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
    private List<String> upsertSubject(Blog blog, EventAction blogEventAction) {
        // 查询状态有效的专栏
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.BLOG_UID, blog.getUid());
        List<SubjectItem> subjectItemList = subjectItemService.list(queryWrapper);

        // 数据库已存在的专栏
        List<String> sub1 = new ArrayList<>();
        // 本次变更的专栏
        List<String> sub2 = new ArrayList<>();
        Map<String, SubjectItem> subjectItemMap = new HashMap<>();
        for (SubjectItem subjectItem : subjectItemList) {
            sub1.add(subjectItem.getSubjectUid());
            if (StringUtils.isNotEmpty(subjectItem.getSubjectUid()) && StringUtils.isNotEmpty(subjectItem.getBlogUid())) {
                subjectItemMap.put(String.format("%s_%s", subjectItem.getSubjectUid(), subjectItem.getBlogUid()), subjectItem);
            }
        }

        // 如果是编辑操作，会从前端携带变更后的专栏
        List<Subject> subjectList = blog.getSubjectList();
        if (subjectList != null) {
            for (Subject subject : subjectList) {
                sub2.add(subject.getUid());
            }
        }

        // 如果发布操作，不会变更专栏的内容
        if (blogEventAction.equals(EventAction.BLOG_PUBLISH) || blogEventAction.equals(EventAction.BLOG_AUDIT)) {
            // 只需要遍历一遍文章，把下架或者删除的文章清空掉索引
            for (SubjectItem subjectItem : subjectItemList) {
                // 必须是上线，并且成功发布的
                if (blog.getIsPublish().equals(EPublish.PUBLISH) && EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
                    subjectItem.setIsPublish(EPublish.PUBLISH);
                } else {
                    subjectItem.setIsPublish(EPublish.NO_PUBLISH);
                }
                 subjectItem.updateById();
            }
            return null;
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
        log.info("[upsertSubject] 更新专栏信息完毕, blogUid: {}", blog.getUid());
        // 本次新增的
        return insertList;
    }

    /**
     * 发送用户订阅消息
     * @param blog
     */
    private void sendUserSubscribeMessage(Blog blog, List<String> insertSubjectUidList) {
        // 必须是通过审核并且上架的，才会推送消息
        if (!(EAuditStatus.AGREE.equals(blog.getAuditStatus()) && EPublish.PUBLISH.equals(blog.getIsPublish()))){
            return;
        }
        if (insertSubjectUidList.size() == 0) {
            return;
        }
        // 加载本篇文章的专栏
        List<Subject> subjectList = subjectService.listByIds(insertSubjectUidList);
        // 判断这两篇专栏有多少用户订阅了
        if (subjectList.size() == 0) return;
        List<String> subjectUidList = subjectList.stream().map(Subject::getUid).collect(Collectors.toList());
        Map<String, Subject> subjectMap = subjectList.stream().collect(Collectors.toMap(Subject::getUid, obj -> obj));
        if (subjectUidList.size() == 0) return;

        // 查找出所有的订阅者
        UserSubscribeVO userSubscribeVO = new UserSubscribeVO();
        userSubscribeVO.setResourceUidList(subjectUidList);
        userSubscribeVO.setPageSize(10000L);
        userSubscribeVO.setCurrentPage(1L);
        IPage<UserSubscribe> iPage = userSubscribeService.getPageList(userSubscribeVO);
        List<UserSubscribe> userSubscribeList = iPage.getRecords();
        // 给订阅者发送文章订阅消息
        // 按用户和专栏进行去重
        Map<String, Boolean> filterMap = new HashMap<>();
        for (UserSubscribe userSubscribe : userSubscribeList) {
            // 您订阅的专栏: xxx，新增了一篇新的文章:
            Subject subject = subjectMap.get(userSubscribe.getResourceUid());
            if (subject == null) continue;
            String filterKey = String.format("%s_%s", subject.getUid(), userSubscribe.getUserUid());
            if (filterMap.get(filterKey) != null) {
                continue;
            }
            filterMap.put(filterKey, true);

            // 向被关注的用户推送消息通知
            Notice notice = new Notice();
            notice.setUserUid(userSubscribe.getUserUid());
            notice.setNoticeType(ENoticeType.SYSTEM.getCode());
            notice.setBusinessType(EBusinessType.SUBJECT_SUBSCRIBE.getCode());
            notice.setBusinessUid(subject.getUid());
            String content = String.format("你订阅的专栏&nbsp;<a href='%s' href='_blank' style='color: rgb(100, 103, 163)'>%s</a>&nbsp; 新增了一篇新的文章&nbsp; <a href='%s' href='_blank' style='color: rgb(100, 103, 163)'>%s</a>", webSiteUrl + "subject/" + subject.getUid(), subject.getSubjectName(), webSiteUrl + "info/" + blog.getUid(), blog.getTitle());
            notice.setContent(content);
            notice.insert();

            // 同时向被关注的Redis发送通知
            String redisKey = RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + userSubscribe.getUserUid();
            String watchCount = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(watchCount)) {
                redisUtil.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
        }
    }


    /**
     * 差集(基于java8新特性)优化解法2 适用于大数据量
     * * 求List1中有的但是List2中没有的元素
     *
     * @param list1
     * @param list2
     * @return
     */
    private List<String> subList(List<String> list1, List<String> list2) {
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
        log.info("[sendAuditNotice] 发送博客审核通知");
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUserUid(blog.getUserUid());
        noticeVO.setContent(text);
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeVO.setBusinessType(EBusinessType.BLOG_AUDIT.getCode());
        noticeVO.setBusinessUid(blog.getUid());
        noticeVO.setIsBlack(Constants.NUM_ZERO);
        asyncService.executeAsyncBusinessNotice(noticeVO);
    }


    /**
     * 清空用户缓存，例如：密码访问记录
     * @param resourceUid
     */
    private void deleteUserVisitAuthCache(String resourceUid) {
        // 清空用户缓存
        Set<String> keys = redisUtil.keys(RedisConf.VISIT_AUTH + Constants.SYMBOL_COLON + resourceUid + "*");
        redisUtil.delete(keys);
    }

}
