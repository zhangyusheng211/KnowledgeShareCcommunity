package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客修改事件
 *
 * @author 遇见
 */
@Slf4j
public class BlogEditEvent extends BlogEvent {
    public BlogEditEvent(Blog source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("博客新增事件");
    }
}
