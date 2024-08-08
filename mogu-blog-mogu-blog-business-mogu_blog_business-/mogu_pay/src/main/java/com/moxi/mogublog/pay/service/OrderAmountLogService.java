package com.moxi.mogublog.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.OrderAmountLog;
import com.moxi.mogublog.commons.vo.OrderAmountLogVO;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 订单流水表 服务类
 *
 * @author 陌溪
 * @date 2022年7月18日08:27:21
 */
public interface OrderAmountLogService extends SuperService<OrderAmountLog> {
    /**
     * 获取博客标签列表
     *
     * @param orderAmountLogVO
     * @return
     */
    IPage<OrderAmountLog> getPageList(OrderAmountLogVO orderAmountLogVO);

    /**
     * 新增博客标签
     *
     * @param orderAmountLogVO
     */
    String addOrderAmountLog(OrderAmountLogVO orderAmountLogVO);

    /**
     * 编辑博客标签
     *
     * @param orderAmountLogVO
     */
    String editOrderAmountLog(OrderAmountLogVO orderAmountLogVO);

    /**
     * 获取用户可提现金额
     *
     * @return
     */
    Long getUserAmount();

}
