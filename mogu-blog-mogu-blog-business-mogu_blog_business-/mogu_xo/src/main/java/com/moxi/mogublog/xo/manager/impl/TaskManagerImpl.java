package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PayFeignClient;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.manager.TaskManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mougblog.base.enums.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务调度处理类
 *
 * @author 遇见
 * @date 2023年4月1日18:18:27
 */
@Service
public class TaskManagerImpl implements TaskManager {

    /**
     * 博客模块
     */
    @Resource
    BlogService blogService;

    @Resource
    ProblemService problemService;
    /**
     * 问题模块
     */
    @Resource
    QuestionService questionService;
    /**
     * 评论模块
     */
    @Resource
    CommentService commentService;

    /**
     * 资源模块
     */
    @Resource
    ResourceService resourceService;

    /**
     * 用户点赞模块
     */
    @Resource
    UserPraiseRecordService userPraiseRecordService;

    @Resource
    CollectService collectService;

    @Resource
    CommonService commonService;
    @Resource
    UserService userService;

    @Override
    public void refreshPraiseCountTask(EBusinessType businessType, boolean isAll) {
        // 获取最近一小时内变更的数量
        QueryWrapper<UserPraiseRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.RESOURCE_TYPE, EResourceType.BLOG.getType());
        if (businessType == EBusinessType.PRAISE) {
            queryWrapper.eq(SQLConf.PRAISE_TYPE, EPraiseType.PRAISE.getType());
        } else {
            queryWrapper.eq(SQLConf.PRAISE_TYPE, EPraiseType.TREAD.getType());
        }
        // 获取1小时前的日期
        if (!isAll) {
            Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000L);
            Date now = new Date();
            queryWrapper.between(SQLConf.CREATE_TIME, date, now);
        }

        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.select("distinct resource_uid");
        List<UserPraiseRecord> userPraiseRecordList = userPraiseRecordService.list(queryWrapper);
        List<String> resourceUidList = new ArrayList<>();
        for (UserPraiseRecord userPraiseRecord : userPraiseRecordList) {
            resourceUidList.add(userPraiseRecord.getResourceUid());
        }

        if (resourceUidList.size() == 0) {
            return;
        }
        // 获取对应
        queryWrapper.clear();
        queryWrapper.in(SQLConf.RESOURCE_UID, resourceUidList);
        if (businessType == EBusinessType.PRAISE) {
            queryWrapper.eq(SQLConf.PRAISE_TYPE, EPraiseType.PRAISE.getType());
        } else {
            queryWrapper.eq(SQLConf.PRAISE_TYPE, EPraiseType.TREAD.getType());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.groupBy(SQLConf.RESOURCE_UID);
        queryWrapper.select(SQLConf.RESOURCE_UID, "count(1) as count");
        List<Map<String, Object>> praiseMap = userPraiseRecordService.listMaps(queryWrapper);
        this.updateBlogInfo(praiseMap, businessType);
    }

    @Override
    public void refreshCollectCountTask(boolean isAll) {
        // 获取最近一小时内变更的数量
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        // 获取1小时前的日期
        if (!isAll) {
            Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000L);
            Date now = new Date();
            queryWrapper.between(SQLConf.CREATE_TIME, date, now);
        }
        queryWrapper.eq(SQLConf.COLLECT_TYPE, EResourceType.BLOG.getType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 获取1小时前的日期
        if (!isAll) {
            Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000L);
            Date now = new Date();
            queryWrapper.between(SQLConf.CREATE_TIME, date, now);
        }
        queryWrapper.select("distinct biz_uid");
        List<Collect> collectList = collectService.list(queryWrapper);
        List<String> resourceUidList = new ArrayList<>();
        for (Collect collect : collectList) {
            resourceUidList.add(collect.getBizUid());
        }
        if (resourceUidList.size() == 0) {
            return;
        }
        // 获取对应
        queryWrapper.clear();
        queryWrapper.in(SQLConf.BIZ_UID, resourceUidList);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.groupBy(SQLConf.RESOURCE_UID);
        queryWrapper.select("biz_uid as resource_uid", "count(1) as count");
        List<Map<String, Object>> collectMap = collectService.listMaps(queryWrapper);
        this.updateBlogInfo(collectMap, EBusinessType.COLLECT);
    }

    @Override
    public void refreshCommentCountTask(boolean isAll) {
        // 获取最近一小时内变更的数量
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        // 获取1小时前的日期
        if (!isAll) {
            Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000L);
            Date now = new Date();
            queryWrapper.between(SQLConf.CREATE_TIME, date, now);
        }
        queryWrapper.eq(SQLConf.SOURCE, ECommentSource.BLOG_INFO.getCode());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.select("distinct blog_uid");
        List<Comment> commentList = commentService.list(queryWrapper);
        List<String> resourceUidList = new ArrayList<>();
        for (Comment comment : commentList) {
            if (comment != null && StringUtils.isNotEmpty(comment.getBlogUid())) {
                resourceUidList.add(comment.getBlogUid());
            }
        }
        if (resourceUidList.size() == 0) {
            return;
        }
        // 获取对应
        queryWrapper.clear();
        queryWrapper.select("blog_uid as resource_uid", "count(1) as count");
        queryWrapper.in(SQLConf.BLOG_UID, resourceUidList);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.groupBy(SQLConf.RESOURCE_UID);
        List<Map<String, Object>> commentMap = commentService.listMaps(queryWrapper);
        this.updateBlogInfo(commentMap, EBusinessType.COMMENT);
    }

    @Override
    public void refreshResourceDownloadCountTask(boolean isAll) {
        // 获取最近一小时内变更的数量
        QueryWrapper<com.moxi.mogublog.commons.entity.Resource> queryWrapper = new QueryWrapper<>();
        // 获取1小时前的日期
        if (!isAll) {
            Date date = new Date(System.currentTimeMillis() - 60 * 60 * 1000L);
            Date now = new Date();
            queryWrapper.between(SQLConf.CREATE_TIME, date, now);
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<com.moxi.mogublog.commons.entity.Resource> resourceList = resourceService.list(queryWrapper);
        List<String> resourceUidList = new ArrayList<>();
        for (com.moxi.mogublog.commons.entity.Resource resource : resourceList) {
            // 调用feign，获取对应的数量
            // 判断支付订单数
            PayOrderVO payOrderVO = new PayOrderVO();
            payOrderVO.setResourceUid(resource.getUid());
            payOrderVO.setOrderStatus(EOrderStatus.Finish);
            int payCount = commonService.getPayOrderCount(payOrderVO);
            resource.setDownloadCount(payCount);
            resource.updateById();
        }
    }

    @Override
    public void refreshUserCardTask() {
        // 计算出活跃的用户UID
    }

    @Override
    public void dropUserVip() {
        // 查找出要过期的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 会员截止时间小于等于当前时间的用户
        queryWrapper.le(SQLConf.VIP_DEADLINE_TIME, new Date());
        // 截止时间不为空的
        queryWrapper.isNotNull(SQLConf.VIP_DEADLINE_TIME);
        // 并且是会员的用户
        queryWrapper.in(SQLConf.USER_TAG, EUserTag.VIP_DAY.getValue(), EUserTag.VIP_WEEK.getValue(), EUserTag.VIP_MONTH.getValue(), EUserTag.VIP_QUARTER.getValue(),EUserTag.VIP_YEAR.getValue(), EUserTag.VIP_LIFE_LONG.getValue());
        List<User> userList = userService.list(queryWrapper);
        // 将这些用户设置成普通用户
        for (User user : userList) {
            user.setVipDeadlineTime(null);
            user.setUserTag(EUserTag.NORMAL_USER.getValue());
            user.updateById();
        }
    }

    private void updateBlogInfo(List<Map<String, Object>> updateMap, EBusinessType businessType) {
        for (Map<String, Object> map : updateMap) {
            String resourceUid = map.get(SQLConf.RESOURCE_UID).toString();
            Integer count = Integer.valueOf(map.get(SQLConf.COUNT).toString());
            Blog blog = blogService.getById(resourceUid);
            if (blog == null) {
                continue;
            }
            switch (businessType) {
                case PRAISE: {
                    blog.setPraiseCount(count);
                }
                break;
                case TREAD: {
                    blog.setTreadCount(count);
                }
                break;
                case COLLECT: {
                    blog.setCollectCount(count);
                }
                break;
                case COMMENT: {
                    blog.setCommentCount(count);
                }
                break;
            }
            blogService.updateById(blog);
        }
    }
}
