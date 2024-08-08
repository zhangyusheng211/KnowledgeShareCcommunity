package com.moxi.mogublog.xo.event.problemEvent;

import com.moxi.mogublog.commons.entity.Problem;
import lombok.extern.slf4j.Slf4j;

/**
 * 面经审核事件
 *
 * @author 2022年4月8日08:38:28
 */
@Slf4j
public class ProblemAuditEvent extends ProblemEvent {

    public ProblemAuditEvent(Problem source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("面经审核事件");
    }
}
