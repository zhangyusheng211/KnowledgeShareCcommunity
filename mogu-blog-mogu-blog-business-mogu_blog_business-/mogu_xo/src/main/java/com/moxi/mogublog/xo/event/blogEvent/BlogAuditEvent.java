package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import lombok.extern.slf4j.Slf4j;

/**
 * blog审核事件
 *
 * @author 遇见
 */
@Slf4j
public class BlogAuditEvent extends BlogEvent {

    public BlogAuditEvent(Blog source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("博客审核事件");
    }
}
