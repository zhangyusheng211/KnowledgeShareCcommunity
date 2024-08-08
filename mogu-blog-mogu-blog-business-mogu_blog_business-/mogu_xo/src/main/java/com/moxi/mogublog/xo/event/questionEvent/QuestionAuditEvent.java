package com.moxi.mogublog.xo.event.questionEvent;

import com.moxi.mogublog.commons.entity.Question;

public class QuestionAuditEvent extends QuestionEvent {

    /**
     * Construct a new instance of this class, associated with the specified
     * Context instance.
     *
     * @param source The associated Context instance
     */
    public QuestionAuditEvent(Question source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
