package com.moxi.mogublog.xo.event.momentEvent;

import org.springframework.context.ApplicationEvent;

public abstract class MomentEvent extends ApplicationEvent {

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MomentEvent(Object source, Boolean isAdmin) {
        super(source);
        this.isAdmin = isAdmin;
    }


    public Boolean isAdmin() {
        return isAdmin;
    }
}