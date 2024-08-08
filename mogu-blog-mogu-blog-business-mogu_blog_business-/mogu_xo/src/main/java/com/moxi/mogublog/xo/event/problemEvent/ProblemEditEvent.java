package com.moxi.mogublog.xo.event.problemEvent;

import com.moxi.mogublog.commons.entity.Problem;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客修改事件
 *
 * @author 遇见
 */
@Slf4j
public class ProblemEditEvent extends ProblemEvent {
    public ProblemEditEvent(Problem source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("问题新增事件");
    }
}
