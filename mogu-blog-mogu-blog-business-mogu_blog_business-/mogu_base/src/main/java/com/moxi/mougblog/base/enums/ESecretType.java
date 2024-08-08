package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 密钥配置类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2024年2月17日19:38:44
 */
public enum ESecretType {

    /**
     * 语言大模型
     */
    LANGUAGE_MODEL("1", "语言大模型"),

    /**
     * 第三方支付
     */
    PAY("2", "第三方支付"),

    /**
     * 第三方登录
     */
    THIRD_LOGIN("3", "第三方登录"),

    /**
     * 对象存储
     */
    OBJECT_STORAGE("4", "对象存储"),

    /**
     * 第三方密钥
     */
    THIRD_SECRET("10", "第三方密钥"),

    ;

    private final String type;
    private final String name;

    public static ESecretType getLanguageModelType(String type) {
        for (ESecretType payMethod : ESecretType.values()) {
            if (payMethod.getType().equals(type)) {
                return payMethod;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    ESecretType(String type, String name) {
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