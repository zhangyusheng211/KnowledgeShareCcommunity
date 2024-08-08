package com.moxi.mougblog.base.enums;

/**
 * 评论来源枚举类
 *
 * @Author: 陌溪
 * @Date: 2020年1月20日13:16:33
 */
public enum ECommentSource {

    /**
     * 关于我
     */
    ABOUT("ABOUT", "关于我"),

    /**
     * 博客详情
     */
    BLOG_INFO("BLOG_INFO", "博客详情"),

    /**
     * 留言板
     */
    MESSAGE_BOARD("MESSAGE_BOARD", "留言板"),

    /**
     * 问答详情
     */
    QUESTION_INFO("QUESTION_INFO", "问答详情"),

    /**
     * 用户动态
     */
    USER_MOMENT("USER_MOMENT", "用户动态"),

    /**
     * 媒资详情
     */
    MEDIA_INFO("MEDIA_INFO", "媒资详情"),

    /**
     * 问题详情
     */
    PROBLEM_INFO("PROBLEM_INFO", "问题详情"),

    /**
     * 问题详情
     */
    RESOURCE_INFO("RESOURCE_INFO", "资源详情"),

    ;

    private final String code;
    private final String name;

    ECommentSource(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}