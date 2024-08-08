package com.moxi.mougblog.base.enums;

/**
 * 支付类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年3月25日16:28:59
 */
public enum EPayType {

    /**
     * 积分支付
     */
    CREDITS_PAY(1, "积分支付"),

    /**
     * 现金支付
     */
    CASH_PAY(2, "现金支付"),

    ;


    private final int type;
    private final String name;

    EPayType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}