package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.CreditsLog;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayType;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 积分变更事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.CREDITS})
public class CreditsEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        CreditsLog creditsLog = objectMapper.convertValue(domainEvent.getEntity(), CreditsLog.class);
        switch (domainEvent.getEventAction()) {
            case CREDITS_CHANGE: {
                this.creditsChange(creditsLog);
            }
            break;
        }
    }

    /**
     * 积分变更领域事件
     *
     * @param creditsLog
     */
    public void creditsChange(CreditsLog creditsLog) {
        log.info("[creditsChange] 积分变更事件");
        if (StringUtils.isNotEmpty(creditsLog.getUserUid())) {
            log.info("[creditsChange] 执行用户积分变更任务, userUid: {}", creditsLog.getUserUid());
            userTaskService.action(Action.CREDITS, null, null, creditsLog.getUserUid());
        }
    }


}
