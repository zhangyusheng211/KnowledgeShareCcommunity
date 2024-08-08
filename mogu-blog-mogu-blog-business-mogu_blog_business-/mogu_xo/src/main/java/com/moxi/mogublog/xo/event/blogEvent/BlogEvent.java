package com.moxi.mogublog.xo.event.blogEvent;

import com.moxi.mogublog.commons.entity.Blog;
import org.springframework.context.ApplicationEvent;

/**
 * 博客基础事件
 *
 * @author 遇见
 */
public abstract class BlogEvent extends ApplicationEvent {
    /**
     * 是否是管理员
     */
    private Boolean isAdmin;

    public BlogEvent(Blog source, Boolean isAdmin) {

        super(source);
        this.isAdmin = isAdmin;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }
}
