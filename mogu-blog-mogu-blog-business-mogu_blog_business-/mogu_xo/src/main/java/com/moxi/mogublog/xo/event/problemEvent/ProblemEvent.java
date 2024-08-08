package com.moxi.mogublog.xo.event.problemEvent;

import org.springframework.context.ApplicationEvent;

public abstract class ProblemEvent extends ApplicationEvent {

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
    public ProblemEvent(Object source, Boolean isAdmin) {
        super(source);
        this.isAdmin = isAdmin;
    }


    public Boolean isAdmin() {
        return isAdmin;
    }
}
