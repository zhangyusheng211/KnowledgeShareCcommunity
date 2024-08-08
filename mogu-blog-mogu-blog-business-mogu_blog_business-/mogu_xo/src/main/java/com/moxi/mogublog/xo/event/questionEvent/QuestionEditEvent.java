package com.moxi.mogublog.xo.event.questionEvent;

import com.moxi.mogublog.commons.entity.Question;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客修改事件
 *
 * @author 遇见
 */
@Slf4j
public class QuestionEditEvent extends QuestionEvent {
    public QuestionEditEvent(Question source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("问答新增事件");
    }
}
