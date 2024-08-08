package com.moxi.mogublog.xo.event.mediaEvent;

import com.moxi.mogublog.commons.entity.MediaInfo;
import lombok.extern.slf4j.Slf4j;


/**
 * 课程删除事件
 *
 * @author 2022年9月11日21:43:17
 */
@Slf4j
public class MediaDeleteEvent extends MediaEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public MediaDeleteEvent(MediaInfo source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
