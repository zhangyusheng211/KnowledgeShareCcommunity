package com.moxi.mogublog.xo.event.momentEvent;

import com.moxi.mogublog.commons.entity.UserMoment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MomentAuditEvent extends MomentEvent {

    public MomentAuditEvent(UserMoment source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("动态审核事件");
    }
}
