package com.moxi.mogublog.xo.event.problemEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProblemAddEvent extends ProblemEvent {


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public ProblemAddEvent(Object source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("面经新增事件");
    }
}
