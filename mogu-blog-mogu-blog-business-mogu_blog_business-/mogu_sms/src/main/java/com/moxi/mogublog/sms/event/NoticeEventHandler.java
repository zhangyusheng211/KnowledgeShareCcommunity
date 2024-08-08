package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Notice;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通知事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.NOTICE})
public class NoticeEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Notice notice = objectMapper.convertValue(domainEvent.getEntity(), Notice.class);
        switch (domainEvent.getEventAction()) {
            case COLLECT_ADD: {
                this.view(notice);
            }
            break;
            case COLLECT_CANCEL: {
                this.delete(notice);
            }
            break;
        }
    }

    /**
     * 阅读通知
     *
     * @param notice
     */
    public void view(Notice notice) {
        log.info("[NoticeEventHandler] 阅读通知");
    }


    /**
     * 取消通知
     *
     * @param notice
     */
    public void delete(Notice notice) {
        log.info("[NoticeEventHandler] 删除通知");
    }
}
