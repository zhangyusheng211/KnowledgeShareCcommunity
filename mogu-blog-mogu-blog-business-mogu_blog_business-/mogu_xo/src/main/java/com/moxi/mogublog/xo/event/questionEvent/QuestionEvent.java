package com.moxi.mogublog.xo.event.questionEvent;

import org.springframework.context.ApplicationEvent;

public abstract class QuestionEvent extends ApplicationEvent {

    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    /**
     * Construct a new instance of this class, associated with the specified
     * Context instance.
     *
     * @param source The associated Context instance
     */
    public QuestionEvent(Object source, Boolean isAdmin) {
        super(source);
        this.isAdmin = isAdmin;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }
}
