package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 第三方密钥类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2024年2月25日18:45:28
 */
public enum EThirdSecretType {

    /**
     * CoderUtil热搜
     */
    CODER_UTIL("1", "CoderUtil热搜"),

    BAIDU_SEO("2", "百度SEO推送"),

    ;

    private final String type;
    private final String name;

    public static EThirdSecretType getLanguageModelType(String type) {
        for (EThirdSecretType payMethod : EThirdSecretType.values()) {
            if (payMethod.getType().equals(type)) {
                return payMethod;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    EThirdSecretType(String type, String name) {
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