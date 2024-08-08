package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 支付方式枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年4月22日17:52:47
 */
public enum EPayMethod {

    /**
     * 积分支付
     */
    CREDITS_PAY(1, "积分支付"),

    /**
     * 支付宝支付
     */
    ALI_PAY(2, "支付宝支付"),

    /**
     * 微信支付
     */
    WECHAT_PAY(3, "微信支付"),

    /**
     * 第三方支付宝支付【可以个人申请】
     * 地址：https://dwz.cn/VyskYSTn
     */
    YUN_GOU_OS_ALI_PAY(4, "第三方支付宝支付"),

    /**
     * 第三方微信支付【可以个人申请】
     * 地址：https://dwz.cn/VyskYSTn
     */
    YUN_GOU_OS_WECHAT_PAY(5, "第三方微信支付"),

    ;


    private final int type;
    private final String name;

    public static EPayMethod getPayMethod(int type) {
        for (EPayMethod payMethod : EPayMethod.values()) {
            if (payMethod.getType() == type) {
                return payMethod;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    EPayMethod(int type, String name) {
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