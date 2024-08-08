package com.moxi.mogublog.xo.event.questionEvent;

import com.moxi.mogublog.commons.entity.Question;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客删除事件
 *
 * @author 陌溪
 * @date 2022年5月14日09:22:22
 */
@Slf4j
public class QuestionDeleteEvent extends QuestionEvent {
    public QuestionDeleteEvent(Question source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("问题删除事件");
    }
}
