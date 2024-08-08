package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 语言大模型枚举类
 *
 * @Author: 陌溪
 * @Date: 2024年2月17日12:18:18
 */
public enum ELanguageModelType {

    /**
     * 星火大模型
     */
    XING_HUO("1", "星火大模型"),

    /**
     * ChatGPT
     */
    CHAT_GPT("2", "ChatGPT"),

    /**
     * 文心一言
     */
    WEN_XIN("3", "文心一言"),

    ;

    private final String type;
    private final String name;

    public static ELanguageModelType getLanguageModelType(String type) {
        for (ELanguageModelType payMethod : ELanguageModelType.values()) {
            if (payMethod.getType().equals(type)) {
                return payMethod;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    ELanguageModelType(String type, String name) {
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