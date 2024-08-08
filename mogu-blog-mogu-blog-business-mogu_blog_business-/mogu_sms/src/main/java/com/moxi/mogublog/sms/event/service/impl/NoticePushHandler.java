package com.moxi.mogublog.sms.event.service.impl;

import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.sms.event.annotation.MessagePushType;
import com.moxi.mogublog.sms.event.service.AbstractMessagePushHandler;
import com.moxi.mougblog.base.enums.EPushMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 站内信推送事件处理器
 */
@Slf4j
@Component
@MessagePushType({EPushMethod.NOTICE})
public class NoticePushHandler extends AbstractMessagePushHandler {

    @Override
    public void pushHandler(MessagePush messagePush, List<User> userList) {
        // 站内信推送
        log.error("开始推送站内信触达，标题：{}", messagePush.getTitle());
        // 开始批量发送站内信
        for (User user : userList) {
            asyncService.executeAsyncBusinessNotice(user.getUid(), messagePush.getContent());
        }
        log.error("站内信触达推送完成");
    }
}
