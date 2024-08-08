package com.moxi.mogublog.xo.event.commentEvent;

import org.springframework.context.ApplicationEvent;

public abstract class CommentEvent extends ApplicationEvent {

    private Boolean isAdmin;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public CommentEvent(Object source, Boolean isAdmin) {
        super(source);
        this.isAdmin = isAdmin;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }
}
