package com.moxi.mogublog.pay.restapi;

import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.vo.OrderAmountLogVO;
import com.moxi.mogublog.pay.service.OrderAmountLogService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.holder.RequestHolder;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 订单流水API接口
 */
@RestController
@RequestMapping("/orderAmountLog")
@Api(value = "订单流水模块", tags = {"订单流水模块"})
public class OrderAmountLogRestApi {

    @Resource
    private OrderAmountLogService orderAmountLogService;

    /**
     * 获取订单金额流水日志
     *
     * @param orderAmountLogVO
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public String orderAmountLogList(@RequestBody OrderAmountLogVO orderAmountLogVO) {
        return ResultUtil.successWithData(orderAmountLogService.getPageList(orderAmountLogVO));
    }

    /**
     * 新增资金流水记录
     * @param orderAmountLogVO
     * @return
     */
    @FeignSecurity
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addOrderAmount(@RequestBody OrderAmountLogVO orderAmountLogVO) {
        return orderAmountLogService.addOrderAmountLog(orderAmountLogVO);
    }

    /**
     * 获取用户可提现金额
     *
     * @return
     */
    @RequestMapping(value = "/getUserAmount", method = RequestMethod.POST)
    public String getUserAmount() {
        return ResultUtil.successWithData(orderAmountLogService.getUserAmount());
    }
}
