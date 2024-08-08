package com.moxi.mogublog.pay.service.impl.PayServiceImpl;


import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.schema.BuyNowResponse;
import com.moxi.mogublog.pay.annotation.PayMethod;
import com.moxi.mogublog.pay.model.bean.WxPayBean;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.pay.service.PayService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayMethod;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务
 *
 * @author 陌溪
 */
@Service
@PayMethod({EPayMethod.WECHAT_PAY})
@Slf4j
public class WechatPayService implements PayService {

    @Resource
    private WxPayBean wxPayBean;
    @Resource
    private PayOrderService payOrderService;

    /**
     * 普通公钥模式
     */
    private final static String NOTIFY_URL = "/trade/wxPay/callback";
    private final static String RETURN_URL = "/wxPay/refundNotify";

    @Override
    public String pay(String payOrderUid) {
        // 获取支付订单
        PayOrder payOrder = payOrderService.getById(payOrderUid);
        if (payOrder == null) {
            log.error("支付订单不存在: payOrderUid: {}", payOrderUid);
            throw new InsertException("支付订单不存在");
        }
        double price = payOrder.getPrice() / 100D;
        String totalAmount = "" + price;
        String ip = RequestHolder.getIp();
        if (StringUtils.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        // 构建微信支付参数
        WxPayApiConfig wxPayApiConfig = getApiConfig();
        Map<String, String> params = UnifiedOrderModel
                .builder()
                .appid(wxPayApiConfig.getAppId())
                .mch_id(wxPayApiConfig.getMchId())
                .nonce_str(WxPayKit.generateStr())
                .body(payOrder.getTitle())
                .attach(payOrder.getTitle())
                .out_trade_no(payOrder.getUid())
                .total_fee("1")
                .spbill_create_ip(ip)
                .notify_url(wxPayApiConfig.getDomain().concat(NOTIFY_URL))
                .trade_type(TradeType.NATIVE.getTradeType())
                .build()
                .createSign(wxPayApiConfig.getPartnerKey(), SignType.HMACSHA256);

        String xmlResult = WxPayApi.pushOrder(false, params);
        log.info("统一下单:" + xmlResult);
        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);
        String returnCode = result.get("return_code");
        String returnMsg = result.get("return_msg");
        System.out.println(returnMsg);
        if (!WxPayKit.codeIsOk(returnCode)) {
            return ResultUtil.errorWithMessage("error:" + returnMsg);
        }
        String resultCode = result.get("result_code");
        if (!WxPayKit.codeIsOk(resultCode)) {
            return ResultUtil.errorWithMessage("error:" + returnMsg);
        }
        //生成预付订单success
        String qrCodeUrl = result.get("code_url");

        // 构建返回
        BuyNowResponse buyNowResponse = new BuyNowResponse();
        buyNowResponse.setPayOrderUid(payOrder.getUid());
        buyNowResponse.setUrl(qrCodeUrl);

        return ResultUtil.successWithData(buyNowResponse);
    }

    @Override
    public String callback() {
        HttpServletRequest request = RequestHolder.getRequest();
        String xmlMsg = HttpKit.readData(request);
        log.info("支付通知=" + xmlMsg);
        Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);
        String returnCode = params.get("return_code");
        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        // 注意此处签名方式需与统一下单的签名类型一致
        if (WxPayKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPartnerKey(), SignType.HMACSHA256)) {
            if (WxPayKit.codeIsOk(returnCode)) {
                // 更新订单信息
                String orderUid = params.get("out_trade_no");
                PayOrder payOrder = payOrderService.getById(orderUid);
                if (payOrder == null) {
                    log.error("获取订单失败, orderUid: " + orderUid);
                    // 发送通知等
                    Map<String, String> xml = new HashMap<>(2);
                    xml.put("return_code", "ERROR");
                    xml.put("return_msg", "NO");
                    return WxPayKit.toXml(xml);
                }
                payOrder.setOrderStatus(EOrderStatus.Finish);
                payOrder.updateById();
                // 发送通知等
                Map<String, String> xml = new HashMap<String, String>(2);
                xml.put("return_code", "SUCCESS");
                xml.put("return_msg", "OK");
                return WxPayKit.toXml(xml);
            }
        }
        return null;
    }

    @Override
    public String closePayOrder(PayOrder payOrder) {
        return null;
    }

    public WxPayApiConfig getApiConfig() {
        WxPayApiConfig apiConfig;
        try {
            apiConfig = WxPayApiConfigKit.getApiConfig(wxPayBean.getAppId());
        } catch (Exception e) {
            apiConfig = WxPayApiConfig.builder()
                    .appId(wxPayBean.getAppId())
                    .mchId(wxPayBean.getMchId())
                    .partnerKey(wxPayBean.getPartnerKey())
                    .certPath(wxPayBean.getCertPath())
                    .domain(wxPayBean.getDomain())
                    .build();
        }
        return apiConfig;
    }
}
