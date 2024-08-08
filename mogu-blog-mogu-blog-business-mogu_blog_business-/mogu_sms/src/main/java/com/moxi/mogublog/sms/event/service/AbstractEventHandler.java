package com.moxi.mogublog.sms.event.service;

import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.sms.event.AtEventHandler;
import com.moxi.mogublog.sms.task.server.UserTaskService;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;

import javax.annotation.Resource;

/**
 * @ClassName 抽象领域事件处理
 * @Desc
 * @Author 陌溪
 * @Date 2022年12月25日11:08:46
 * @Version 1.0
 **/
public abstract class AbstractEventHandler implements IEventService {

    @Resource
    protected UserService userService;

    /**
     * 管理员服务
     */
    @Resource
    protected AdminService adminService;

    /**
     * 简易的mq邮件封装工具类
     */
    @Resource
    protected RabbitMqUtil rabbitMqUtil;

    /**
     * 异步执行服务
     */
    @Resource
    protected AsyncService asyncService;

    /**
     * 系统配置；服务
     */
    @Resource
    protected SystemConfigService systemConfigService;

    /**
     * 博客服务
     */
    @Resource
    protected BlogService blogService;

    /**
     * 专栏服务
     */
    @Resource
    protected SubjectService subjectService;
    /**
     * 专题服务
     */
    @Resource
    protected SubjectItemService subjectItemService;

    /**
     * 评论服务
     */
    @Resource
    protected CommentService commentService;

    /**
     * 用户任务
     */
    @Resource
    protected UserTaskService userTaskService;

    /**
     * 通用管理
     */
    @Resource
    protected CommonManager commonManager;

    @Resource
    protected RedisUtil redisUtil;

    @Resource
    protected ProblemService problemService;

    @Resource
    protected QuestionService questionService;

    @Resource
    protected AtEventHandler atEventHandler;

    @Resource
    protected NoticeService noticeService;

    @Resource
    protected UserSubscribeService userSubscribeService;

    @Resource
    protected ResourceService resourceService;

    @Resource
    protected VipConfigService vipConfigService;

    /**
     * 处理事件
     *
     * @param domainEvent
     */
    public abstract void doEventHandler(DomainEvent domainEvent);

}
