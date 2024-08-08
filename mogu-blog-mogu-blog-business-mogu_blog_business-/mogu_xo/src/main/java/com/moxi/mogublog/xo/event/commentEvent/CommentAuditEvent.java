package com.moxi.mogublog.xo.event.commentEvent;

import com.moxi.mogublog.commons.entity.Comment;
import lombok.extern.slf4j.Slf4j;


/**
 * 评论审核事件
 *
 * @author 2022年4月8日08:38:28
 */
@Slf4j
public class CommentAuditEvent extends CommentEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public CommentAuditEvent(Comment source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
