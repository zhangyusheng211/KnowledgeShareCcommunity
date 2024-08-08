package com.moxi.mogublog.pay.manager;

import com.moxi.mogublog.pay.model.vo.BuyVO;
import com.moxi.mogublog.pay.model.vo.SponsorVO;

/**
 * @author 遇见
 * 下单管理
 */
public interface TradeManager {
    /**
     * 立即购买
     *
     * @param buyVO
     * @return
     */
    String buyNow(BuyVO buyVO);

    /**
     * 赞助订单
     *
     * @param sponsorVO
     * @return
     */
    String sponsor(SponsorVO sponsorVO);

    /**
     * 关闭支付订单【几小时前的】
     *
     * @param hour
     * @return
     */
    Boolean closePayOrder(int hour);
}
