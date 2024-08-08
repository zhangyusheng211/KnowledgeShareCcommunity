package com.moxi.mogublog.xo.event.commentEvent;

import com.moxi.mogublog.commons.entity.Comment;
import lombok.extern.slf4j.Slf4j;


/**
 * 评论新增事件
 *
 * @author 2022年4月8日08:38:28
 */
@Slf4j
public class CommentAddEvent extends CommentEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source  the object on which the event initially occurred or with
     *                which the event is associated (never {@code null})
     * @param isAdmin
     */
    public CommentAddEvent(Comment source, Boolean isAdmin) {
        super(source, isAdmin);
    }
}
