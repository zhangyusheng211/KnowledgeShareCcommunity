package com.moxi.mogublog.xo.event.mediaEvent;

import com.moxi.mogublog.commons.entity.MediaInfo;
import lombok.extern.slf4j.Slf4j;


/**
 * 课程新增事件
 *
 * @author 2022年9月11日21:42:48
 */
@Slf4j
public class MediaAddEvent extends MediaEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public MediaAddEvent(MediaInfo source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
