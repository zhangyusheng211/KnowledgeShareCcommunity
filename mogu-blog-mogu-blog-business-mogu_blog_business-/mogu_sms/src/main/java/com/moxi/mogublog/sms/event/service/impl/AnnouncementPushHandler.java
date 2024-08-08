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
 * 公告推送事件处理器
 */
@Slf4j
@Component
@MessagePushType({EPushMethod.ANNOUNCEMENT})
public class AnnouncementPushHandler extends AbstractMessagePushHandler {

    @Override
    public void pushHandler(MessagePush messagePush, List<User> userList) {
        // 短信推送
        log.error("暂未实现公告推送");
    }
}
