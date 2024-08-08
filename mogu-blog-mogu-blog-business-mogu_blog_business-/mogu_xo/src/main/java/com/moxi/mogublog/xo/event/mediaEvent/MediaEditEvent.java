package com.moxi.mogublog.xo.event.mediaEvent;

import com.moxi.mogublog.commons.entity.MediaInfo;
import lombok.extern.slf4j.Slf4j;


/**
 * 课程编辑事件
 *
 * @author 2022年4月8日08:38:28
 */
@Slf4j
public class MediaEditEvent extends MediaEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public MediaEditEvent(MediaInfo source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
