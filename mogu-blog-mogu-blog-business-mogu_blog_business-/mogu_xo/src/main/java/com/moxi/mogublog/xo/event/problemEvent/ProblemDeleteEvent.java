package com.moxi.mogublog.xo.event.problemEvent;

import com.moxi.mogublog.commons.entity.Problem;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客删除事件
 *
 * @author 陌溪
 * @date 2022年5月14日09:22:22
 */
@Slf4j
public class ProblemDeleteEvent extends ProblemEvent {
    public ProblemDeleteEvent(Problem source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("问题删除事件");
    }
}
