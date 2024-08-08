package com.moxi.mougblog.base.enums;

/**
 * 收费模式枚举类
 *
 * @Author: 陌溪
 * @Date: 2022年11月27日23:02:09
 */
public enum EChargeType {

    /**
     * 免费
     */
    Free(1, "免费"),

    /**
     * 付费价格
     */
    Normal(2, "付费"),

    /**
     * 折扣
     */
    Discount(3, "折扣价"),

    /**
     * 会员免费
     */
    VIP_Free(4, "会员免费");


    private final int type;
    private final String name;

    EChargeType(int type, String name) {
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