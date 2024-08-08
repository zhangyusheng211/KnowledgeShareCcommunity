package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * 立即购买返回
 */
@Data
public class BuyNowResponse {
    /**
     * 支付订单UID
     */
    private String payOrderUid;

    /**
     * 支付链接
     */
    private String url;
}
