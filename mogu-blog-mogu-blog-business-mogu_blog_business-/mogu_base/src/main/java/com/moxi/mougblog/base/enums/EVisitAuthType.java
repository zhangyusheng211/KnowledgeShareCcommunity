package com.moxi.mougblog.base.enums;

/**
 * 阅读权限枚举类
 *
 * @Author: 陌溪
 * @Date: 阅读权限
 */
public enum EVisitAuthType {

    /**
     * 公开
     */
    PUBLIC("1", "公开"),

    /**
     * 登录后可见
     */
    LOGIN("2", "登录阅读"),

    /**
     * 评论后可见
     */
    COMMENT("3", "评论阅读"),

    /**
     * 输入校验码后可见
     */
    CODE("4", "验证阅读"),

    /**
     * 会员可见
     */
    VIP("5", "会员可见"),

    /**
     * 付费阅读
     */
    CASH("6", "付费阅读"),

    /**
     * 点赞阅读
     */
    PRAISE("7", "点赞阅读"),

    /**
     * 收藏阅读
     */
    COLLECT("8", "收藏阅读"),

    /**
     * 关注作者可见
     */
    WATCH("9", "关注阅读"),

    /**
     * 输入密码可见
     */
    PASSWORD("10", "密码阅读"),

    /**
     * 仅作者可见
     */
    AUTHOR("11", "仅作者可见"),

    /**
     * 标签用户可见
     */
    TAG("12", "标签用户可见"),

    /**
     * 指定用户可见
     */
    USER("13", "指定用户可见"),

    /**
     * 订阅专栏可见
     */
    SUBJECT("14", "订阅专栏可见"),

    ;

    private final String type;
    private final String name;

    EVisitAuthType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}