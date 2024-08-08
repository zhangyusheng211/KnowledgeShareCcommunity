package com.moxi.mogublog.sms.event.service;

import com.moxi.mogublog.commons.domainEvent.DomainEvent;

/**
 * 领域事件接口
 *
 * @author thh
 * @date 2022年10月23日01:12:14
 */
public interface IEventService {

    /**
     * 事件处理
     *
     * @return
     */
    void doEventHandler(DomainEvent domainEvent);
}
