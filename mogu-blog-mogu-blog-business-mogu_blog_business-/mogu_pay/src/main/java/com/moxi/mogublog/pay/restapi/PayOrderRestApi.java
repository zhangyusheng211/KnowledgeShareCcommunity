package com.moxi.mogublog.pay.restapi;

import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EResourceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 支付服务API接口
 */
@RestController
@RequestMapping("/order")
@Api(value = "订单模块", tags = {"订单模块"})
public class PayOrderRestApi {

    @Resource
    private PayOrderService payOrderService;

    //    @FeignSecurity
    @ApiOperation(value = "获取订单列表", notes = "获取订单列表")
    @RequestMapping(value = "/getPageList", method = RequestMethod.POST)
    public String getPageList(@RequestBody PayOrderVO payOrderVO) {
        return ResultUtil.successWithData(payOrderService.getPageList(payOrderVO));
    }

    //    @FeignSecurity
    @ApiOperation(value = "获取订单数量", notes = "获取订单数量")
    @RequestMapping(value = "/getPayOrderCount", method = RequestMethod.POST)
    public String getPayOrderCount(@RequestBody PayOrderVO payOrderVO) {
        return ResultUtil.successWithData(payOrderService.getPayOrderCount(payOrderVO));
    }


    @ApiOperation(value = "获取订单累积金额", notes = "获取订单累积金额")
    @RequestMapping(value = "/getPayOrderSumFee", method = RequestMethod.POST)
    public String getPayOrderSumFee(@RequestBody PayOrderVO payOrderVO) {
        return ResultUtil.successWithData(payOrderService.getPayOrderSumFee(payOrderVO));
    }

    @ApiOperation(value = "校验订单是否支付", notes = "校验订单是否支付")
    @RequestMapping(value = "/checkWhetherPay", method = RequestMethod.GET)
    public String checkWhetherPay(@RequestParam String productUid, @RequestParam(value = "orderUid", required = false) String orderUid) {
        return ResultUtil.successWithData(payOrderService.checkWhetherPay(productUid, orderUid));
    }

    @ApiOperation(value = "获取赞助订单列表", notes = "获取赞助订单列表")
    @RequestMapping(value = "/getSponsorOrderList", method = RequestMethod.POST)
    public String getSponsorOrderList(@RequestBody PayOrderVO payOrderVO) {
        payOrderVO.setResourceType(EResourceType.SPONSOR.getType());
        payOrderVO.setOrderStatus(EOrderStatus.Finish);
        return ResultUtil.successWithData(payOrderService.getPageList(payOrderVO));
    }
}
