package com.moxi.mogublog.pay.model.enums;

/**
 * 支付方式
 *
 * @author Snow create in 2018/4/8
 * @version v2.0
 * @since v7.0.0
 */
public enum PaymentType {

    CREDITS("积分换购"),
    /**
     * 在线支付
     */
    ONLINE("在线支付"),

    /**
     * 货到付款
     */
    COMBINE("组合支付");

    private String description;


    PaymentType(String description) {
        this.description = description;
    }

    public static PaymentType defaultType() {

        return ONLINE;
    }

    public String description() {
        return this.description;
    }

    public String value() {
        return this.name();
    }

}
