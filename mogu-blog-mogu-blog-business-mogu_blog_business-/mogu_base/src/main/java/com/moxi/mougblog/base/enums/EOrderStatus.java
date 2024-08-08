package com.moxi.mougblog.base.enums;

/**
 * 订单状态枚举类
 *
 * @author 陌溪
 * @date 2023年3月25日15:35:59
 */
public class EOrderStatus {
    /**
     * 未支付
     */
    public static final Integer New = 0;

    /**
     * 取消订单
     */
    public static final Integer Cancel = 1;

    /**
     * 超时取消
     */
    public static final Integer OverTimeCancel = 2;

    /**
     * 订单完成
     */
    public static final Integer Finish = 3;
}
