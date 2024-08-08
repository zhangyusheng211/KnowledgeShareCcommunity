package com.moxi.mogublog.xo.event.questionEvent;

import com.moxi.mogublog.commons.entity.Question;

public class QuestionAddEvent extends QuestionEvent {

    public QuestionAddEvent(Question source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
