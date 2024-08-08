package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import lombok.extern.slf4j.Slf4j;

/**
 * blog 发布/下架 事件
 *
 * @author 遇见
 */
@Slf4j
public class BlogPublishEvent extends BlogEvent {

    public BlogPublishEvent(Blog source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("博客发布事件");
    }
}
