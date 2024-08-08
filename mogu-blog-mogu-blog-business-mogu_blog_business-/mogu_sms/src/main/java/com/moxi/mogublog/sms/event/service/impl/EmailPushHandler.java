package com.moxi.mogublog.sms.event.service.impl;

import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.sms.event.annotation.MessagePushType;
import com.moxi.mogublog.sms.event.service.AbstractMessagePushHandler;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EPushMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 邮件推送事件处理器
 */
@Slf4j
@Component
@MessagePushType({EPushMethod.EMAIL})
public class EmailPushHandler extends AbstractMessagePushHandler {

    @Override
    public void pushHandler(MessagePush messagePush, List<User> userList) throws InterruptedException {
        // 短信推送
        log.error("开始推送邮件触达，标题：{}", messagePush.getTitle());
        // 开始批量发送邮件
        for (User user : userList) {
            if (StringUtils.isEmpty(user.getEmail())) {
                continue;
            }
            // 根据用户信息，完成邮件的推送
            rabbitMqUtil.sendSimpleEmail(user.getEmail(), messagePush.getContent());
            // 防止被封，每次发送间隔500ms
            Thread.sleep(500);
        }
        log.error("邮件触达推送完成");
    }
}
