package com.moxi.mogublog.xo.event.momentEvent;

import com.moxi.mogublog.commons.entity.UserMoment;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客修改事件
 *
 * @author 遇见
 */
@Slf4j
public class MomentEditEvent extends MomentEvent {
    public MomentEditEvent(UserMoment source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("动态新增事件");
    }
}
