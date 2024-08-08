package com.moxi.mogublog.commons.feign;

import com.moxi.mogublog.commons.config.feign.FeignConfiguration;
import com.moxi.mogublog.commons.vo.OrderAmountLogVO;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 支付服务feign远程调用
 *
 * @author 陌溪
 * @date 2023年4月25日22:24:31
 */

@FeignClient(name = "mogu-pay", configuration = FeignConfiguration.class)
public interface PayFeignClient {
    /**
     * 获取订单列表
     * @param payOrderVO
     * @return
     */
    @RequestMapping(value = "/pay/getPageList", method = RequestMethod.POST)
    String getPageList(@RequestBody PayOrderVO payOrderVO);


    /**
     * 校验是否完成支付
     * @param productUid：商品uid
     * @return
     */
    @RequestMapping(value = "/order/checkWhetherPay", method = RequestMethod.GET)
    String checkWhetherPay(@RequestParam("productUid") String productUid);

    /**
     * 获取订单数量
     * @param payOrderVO
     * @return
     */
    @RequestMapping(value = "/order/getPayOrderCount", method = RequestMethod.POST)
    String getPayOrderCount(@RequestBody PayOrderVO payOrderVO);

    /**
     * 获取订单累积金额
     * @param payOrderVO
     * @return
     */
    @RequestMapping(value = "/order/getPayOrderSumFee", method = RequestMethod.POST)
    String getPayOrderSumFee(@RequestBody PayOrderVO payOrderVO);

    /**
     * 天津爱订单记录
     * @param orderAmountLogVO
     * @return
     */
    @RequestMapping(value = "/orderAmountLog/add", method = RequestMethod.POST)
    String addOrderAmount(@RequestBody OrderAmountLogVO orderAmountLogVO);

    /**
     * 获取微信登录用户信息
     * @param code
     * @return
     */
    @RequestMapping(value = "/wechat/getWechatOauthInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getWechatOauthInfo(@RequestParam("code") String code);

    /**
     * 获取微信登录链接
     * @return
     */
    @RequestMapping(value = "/wechat/getWeChatLoginUrl", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    String getWeChatLoginUrl(@RequestParam("code") String code);
}