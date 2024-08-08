package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 推送方式枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年4月2日16:42:54
 */
public enum EPushMethod {

    /**
     * 公告
     */
    ANNOUNCEMENT(1, "公告"),

    /**
     * 站内信
     */
    NOTICE(2, "站内信"),

    /**
     * 邮件
     */
    EMAIL(3, "邮件"),

    /**
     * 私信
     */
    PRIVATE_LETTER(4, "私信"),

    /**
     * 短信
     */
    SMS(5, "短信"),

    ;


    private final int type;
    private final String name;

    EPushMethod(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取推送方法
     *
     * @param type
     * @return
     */
    public static EPushMethod getPushMethod(Integer type) {
        for (EPushMethod pushMethod : EPushMethod.values()) {
            if (pushMethod.getType() == type) {
                return pushMethod;
            }
        }
        throw new BusinessException("获取推送方式失败");
    }
}