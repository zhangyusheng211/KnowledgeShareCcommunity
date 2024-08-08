package com.moxi.mougblog.base.enums;

/**
 * 网站登录方式枚举类
 *
 * @Author: 陌溪
 * @Date: 2020年10月14日11:10:04
 */
public enum ELoginType {

    /**
     * 账号密码
     */
    PASSWORD("1", "PASSWORD", "用户注册"),

    /**
     * 码云
     */
    GITEE("2", "GITEE", "码云"),

    /**
     * GITHUB
     */
    GITHUB("3", "GITHUB", "Github"),

    /**
     * QQ
     */
    QQ("4", "QQ", "QQ"),

    /**
     * 微信公众号登录
     */
    WECHAT("5", "WECHAT", "微信公众号"),

    /**
     * 小程序登录
     */
    MINI("6", "MINI", "微信小程序"),

    /**
     * 微信登录
     */
    PERSON_WECHAT("7", "PERSON_WECHAT", "微信"),

    ;


    /**
     * code标识
     */
    private final String code;
    /**
     * 来源
     */
    private final String name;
    /**
     * 来源描述
     */
    private final String desc;

    ELoginType(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据type返回枚举类型，主要在switch中使用
     *
     * @param name
     * @return
     */
    public static ELoginType getLoginTypeByName(String name) {
        for (ELoginType loginType : values()) {
            if (loginType.name.equals(name)) {
                return loginType;
            }
        }
        return null;
    }
}