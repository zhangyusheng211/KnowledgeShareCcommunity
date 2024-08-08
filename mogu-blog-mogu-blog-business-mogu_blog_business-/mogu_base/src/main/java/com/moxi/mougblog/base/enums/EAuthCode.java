package com.moxi.mougblog.base.enums;

/**
 * 用户权限Code枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年8月10日08:39:00
 */
public enum EAuthCode {

    /**
     * 开启私聊
     */
    PRIVATE_CHAT("PRIVATE_CHAT", "开启私聊"),

    /**
     * 开启群聊
     */
    PUBLIC_CHAT("PUBLIC_CHAT", "开启群聊"),

    /**
     * 发布面经
     */
    ADD_PROBLEM("ADD_PROBLEM", "发布面经"),

    /**
     * 发表动态
     */
    ADD_MOMENT("ADD_MOMENT", "发表动态"),

    /**
     * 发起问答
     */
    ADD_QUESTION("ADD_QUESTION", "发起问答"),

    /**
     * 创作文章
     */
    ADD_BLOG("ADD_BLOG", "创作文章"),

    /**
     * 发表评论
     */
    ADD_COMMENT("ADD_COMMENT", "发表评论"),

    /**
     * 上传文章
     */
    UPLOAD_BLOG("UPLOAD_BLOG", "上传文章"),

    /**
     * 文章免审
     */
    BLOG_AUDIT("BLOG_AUDIT", "文章免审"),

    /**
     * 积分抽奖
     */
    CREDIT_LUCKY("CREDIT_LUCKY", "积分抽奖"),

    ;


    private final String code;
    private final String name;

    EAuthCode(String code, String name) {
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