package com.moxi.mogublog.xo.event.momentEvent;

import com.moxi.mogublog.commons.entity.UserMoment;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客删除事件
 *
 * @author 陌溪
 * @date 2022年5月14日09:22:22
 */
@Slf4j
public class MomentDeleteEvent extends MomentEvent {
    public MomentDeleteEvent(UserMoment source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("动态删除事件");
    }
}
