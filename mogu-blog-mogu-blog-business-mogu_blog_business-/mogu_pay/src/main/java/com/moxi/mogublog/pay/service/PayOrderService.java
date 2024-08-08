package com.moxi.mogublog.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 支付订单服务类
 *
 * @author 陌溪
 * @since 2023年4月25日22:21:22
 */
public interface PayOrderService extends SuperService<PayOrder> {

    /**
     * 分页获取列表
     *
     * @param payOrderVO
     * @return
     */
    IPage<PayOrder> getPageList(PayOrderVO payOrderVO);

    /**
     * 获取支付订单数
     *
     * @param payOrderVO
     * @return
     */
    int getPayOrderCount(PayOrderVO payOrderVO);

    /**
     * 获取支付订单数
     *
     * @param payOrderVO
     * @return
     */
    int getPayOrderSumFee(PayOrderVO payOrderVO);

    /**
     * 校验某商品是否支付
     *
     * @param productUid
     * @return
     */
    boolean checkWhetherPay(String productUid, String orderUid);
}
