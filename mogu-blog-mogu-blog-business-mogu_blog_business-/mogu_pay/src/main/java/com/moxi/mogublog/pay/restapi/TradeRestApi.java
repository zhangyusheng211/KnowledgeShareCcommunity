package com.moxi.mogublog.pay.restapi;

import com.moxi.mogublog.pay.factory.PayServiceFactory;
import com.moxi.mogublog.pay.manager.TradeManager;
import com.moxi.mogublog.pay.model.vo.BuyVO;
import com.moxi.mogublog.pay.model.vo.SponsorVO;
import com.moxi.mougblog.base.enums.EPayMethod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 交易模块
 *
 * @author 遇见
 */
@RestController
@RequestMapping("/trade")
@Api(value = "交易模块", tags = {"交易模块"})
@Slf4j
public class TradeRestApi {

    @Resource
    TradeManager tradeManager;

    @ApiOperation(value = "阿里支付回调")
    @RequestMapping(value = "/alipay/callback")
    public String aliPayCallback() {
        log.info("[aliPayCallback] 阿里云支付回调");
        return PayServiceFactory.getPayService(EPayMethod.ALI_PAY).callback();
    }

    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wxPay/callback")
    public String wechatPayCallback() {
        log.info("[aliPayCallback] 微信支付回调");
        return PayServiceFactory.getPayService(EPayMethod.WECHAT_PAY).callback();
    }

    @ApiOperation(value = "第三方微信支付回调")
    @RequestMapping(value = "/yunGouOSWxPay/callback")
    public String yunGouOSWechatPayCallback() {
        log.info("[aliPayCallback] 第三方微信支付回调");
        return PayServiceFactory.getPayService(EPayMethod.YUN_GOU_OS_WECHAT_PAY).callback();
    }

    /**
     * 立即购买
     *
     * @return
     */
    @ApiOperation(value = "立即购买")
    @PostMapping(value = "/buyNow")
    public String buyNow(@RequestBody BuyVO buyVO) {
        return tradeManager.buyNow(buyVO);
    }

    /**
     * 资金赞助
     *
     * @return
     */
    @ApiOperation(value = "资金赞助")
    @PostMapping(value = "/sponsor")
    public String sponsor(@RequestBody SponsorVO sponsorVO) {
        return tradeManager.sponsor(sponsorVO);
    }
}
