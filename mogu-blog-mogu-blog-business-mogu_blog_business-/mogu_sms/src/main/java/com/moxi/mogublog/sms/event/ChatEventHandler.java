package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EWebDomainEvent;
import com.moxi.mougblog.base.global.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 关注事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.CHAT})
public class ChatEventHandler extends AbstractEventHandler {
    @Resource
    private WebFeignClient webFeignClient;

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        ImMessage imMessage = objectMapper.convertValue(domainEvent.getEntity(), ImMessage.class);
        switch (domainEvent.getEventAction()) {
            case CHAT_ADD: {
                this.add(imMessage);
            }
            break;
            case CHAT_CANCEL: {
                this.cancel(imMessage);
            }
            break;
        }
    }

    /**
     * 新增消息
     *
     * @param imMessage
     */
    public void add(ImMessage imMessage) {
        log.info("[ChatEventHandler] 处理用户发布聊天事件");

        // 新增聊天，判断任务进行情况
        userTaskService.action(Action.CHAT, null, null);

        // 推送消息触达前端
        switch (imMessage.getMessageType()) {
            case MessageConstant.MESSAGE_TYPE_PRIVATE: {
                String toUserUid = imMessage.getToUserId();
                if (StringUtils.isNotEmpty(toUserUid)) {
                    webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, toUserUid);
                }
            }; break;
            case MessageConstant.MESSAGE_TYPE_GROUP: {
                webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, "");
            } break;
        }

    }

    /**
     * 撤回消息
     *
     * @param imMessage
     */
    public void cancel(ImMessage imMessage) {
        log.info("[ChatEventHandler] 处理用户撤回聊天事件");

    }


}
