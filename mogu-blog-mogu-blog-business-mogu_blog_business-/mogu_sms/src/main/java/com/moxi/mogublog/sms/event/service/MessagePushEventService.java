package com.moxi.mogublog.sms.event.service;

import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.User;

import java.util.List;

/**
 * 消息推送接口
 *
 * @author 陌溪
 * @date 2023年4月2日16:19:09
 */
public interface MessagePushEventService {

    /**
     * 消息推送事件处理
     *
     * @return
     */
    void pushHandler(MessagePush messagePush, List<User> userList) throws InterruptedException;
}
