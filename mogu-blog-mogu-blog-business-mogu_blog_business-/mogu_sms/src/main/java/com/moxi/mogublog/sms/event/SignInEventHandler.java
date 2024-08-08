package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.SignInRecord;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 签到事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.SIGN_IN})
public class SignInEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        SignInRecord signInRecord = objectMapper.convertValue(domainEvent.getEntity(), SignInRecord.class);
        switch (domainEvent.getEventAction()) {
            case SIGN_ADD: {
                this.add(signInRecord);
            }
            break;
            case RETROACTIVE_ADD: {
                this.retroactive(signInRecord);
            }
            break;
        }
    }

    /**
     * 添加签到
     *
     * @param signInRecord
     */
    public void add(SignInRecord signInRecord) {
        log.info("[SignInEventHandler] 触发用户签到事件");

        // 新增签到，判断任务进行情况
        userTaskService.action(Action.SIGN_IN, null, null);

    }


    /**
     * 用户补签
     *
     * @param signInRecord
     */
    public void retroactive(SignInRecord signInRecord) {
        log.info("[SignInEventHandler] 触发用户补签事件");
    }
}
