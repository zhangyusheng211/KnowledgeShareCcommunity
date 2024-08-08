package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客新增事件
 *
 * @author 遇见
 */
@Slf4j
public class BlogAddEvent extends BlogEvent {
    public BlogAddEvent(Blog source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("博客新增事件");
    }
}
