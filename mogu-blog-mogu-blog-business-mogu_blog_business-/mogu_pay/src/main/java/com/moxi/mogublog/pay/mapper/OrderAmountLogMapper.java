package com.moxi.mogublog.pay.mapper;

import com.moxi.mogublog.commons.entity.OrderAmountLog;
import com.moxi.mogublog.commons.vo.OrderAmountLogVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单流水表 Mapper 接口
 *
 * @author 陌溪
 * @date 2023年8月2日20:36:08
 */
public interface OrderAmountLogMapper extends SuperMapper<OrderAmountLog> {

    /**
     * 获取用户金额
     *
     * @param orderAmountLogVO
     * @return
     */
    Long getUserAmount(@Param("orderAmountLogVO") OrderAmountLogVO orderAmountLogVO);
}
