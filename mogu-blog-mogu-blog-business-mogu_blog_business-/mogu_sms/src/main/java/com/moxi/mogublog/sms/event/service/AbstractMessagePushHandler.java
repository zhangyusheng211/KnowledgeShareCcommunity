package com.moxi.mogublog.sms.event.service;

import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName 抽象消息推送事件处理
 * @Desc
 * @Author 陌溪
 * @Date 2023年4月2日16:58:59
 * @Version 1.0
 **/
public abstract class AbstractMessagePushHandler implements MessagePushEventService {

    @Resource
    protected UserService userService;

    /**
     * 简易的mq邮件封装工具类
     */
    @Resource
    protected RabbitMqUtil rabbitMqUtil;

    /**
     * 系统配置；服务
     */
    @Resource
    protected SystemConfigService systemConfigService;

    /**
     * 异步执行服务
     */
    @Resource
    protected AsyncService asyncService;

    /**
     * redis
     */
    @Resource
    protected RedisUtil redisUtil;

    /**
     *
     */
    @Resource
    protected NoticeService noticeService;

    @Resource
    protected ImService imService;

    @Resource
    protected RoomService roomService;

    /**
     * 消息推送处理事件
     *
     * @param messagePush
     */
    public abstract void pushHandler(MessagePush messagePush, List<User> userList) throws InterruptedException;
}
