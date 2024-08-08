package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import lombok.extern.slf4j.Slf4j;

/**
 * 博客删除事件
 *
 * @author 陌溪
 * @date 2022年5月14日09:22:22
 */
@Slf4j
public class BlogDeleteEvent extends BlogEvent {
    public BlogDeleteEvent(Blog source, Boolean isAdmin) {
        super(source, isAdmin);
        log.info("博客删除事件");
    }
}
