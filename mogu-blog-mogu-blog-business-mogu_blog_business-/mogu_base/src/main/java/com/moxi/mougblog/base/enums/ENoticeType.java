package com.moxi.mougblog.base.enums;

/**
 * 通知类型枚举类
 *
 * @author: 陌溪
 * @date: 2021年8月8日15:26:43
 */
public enum ENoticeType {

    /**
     * 评论
     */
    COMMENT(1, "评论"),

    /**
     * 关注
     */
    WATCH(2, "关注"),

    /**
     * 点赞
     */
    PRAISE(3, "点赞"),

    /**
     * 系统
     */
    SYSTEM(4, "系统"),

    /**
     * 私信
     */
    MESSAGE(5, "私信"),

    /**
     * 收藏
     */
    COLLECT(6, "收藏");


    private final Integer code;
    private final String name;

    ENoticeType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取通知类型
     *
     * @param code
     * @return
     */
    public static ENoticeType getNoticeType(Integer code) {
        for (ENoticeType noticeType : ENoticeType.values()) {
            if (noticeType.getCode().equals(code)) {
                return noticeType;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}