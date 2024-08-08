package com.moxi.mogublog.pay.service;

import com.moxi.mogublog.commons.entity.PayOrder;

/**
 * 支付抽象接口
 *
 * @author 遇见
 */
public interface PayService {
    /**
     * 发起支付，生成支付二维码
     *
     * @param payOrderUid 订单编码 后台生成
     */
    String pay(String payOrderUid);

    /**
     * 支付回调
     *
     * @return
     */
    String callback();


    /**
     * 关闭订单
     *
     * @param payOrder
     * @return
     */
    String closePayOrder(PayOrder payOrder);
}
