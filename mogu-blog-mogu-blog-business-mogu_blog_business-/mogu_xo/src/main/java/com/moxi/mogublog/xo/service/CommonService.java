package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.schema.CheckResourceVisitAuthVO;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.commons.vo.PayOrderVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 通用逻辑处理
 *
 * @author
 * @date 2022-07-07
 */
public interface CommonService {

    /**
     * 校验是否审批
     *
     * @param request
     * @param content
     * @return
     */
    String checkIsAudit(HttpServletRequest request, String... content);

    /**
     * 构建商品信息
     *
     * @return
     */
    PayOrder buildPayOrder(ProductVO productVO);


    /**
     * 校验商品是否购买
     *
     * @param productUid
     * @return
     */
    boolean checkWhetherPay(String productUid);

    /**
     * 获取支付订单数量
     * @param payOrderVO
     * @return
     */
    int getPayOrderCount(PayOrderVO payOrderVO);

    /**
     * 获取订单金额
     * @param payOrderVO
     * @return
     */
    int getPayOrderSumFee(PayOrderVO payOrderVO);


    /**
     * 校验资源访问权限
     * @param checkResourceVisitAuthVO
     * @return
     */
    boolean checkResourceVisit(CheckResourceVisitAuthVO checkResourceVisitAuthVO);
}
