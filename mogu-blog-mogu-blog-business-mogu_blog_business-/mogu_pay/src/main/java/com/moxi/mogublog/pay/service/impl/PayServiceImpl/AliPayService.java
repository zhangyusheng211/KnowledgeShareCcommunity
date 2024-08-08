package com.moxi.mogublog.pay.service.impl.PayServiceImpl;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.schema.BuyNowResponse;
import com.moxi.mogublog.pay.annotation.PayMethod;
import com.moxi.mogublog.pay.model.bean.AliPayBean;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.pay.service.PayService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayMethod;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 阿里支付服务
 *
 * @author 陌溪
 */
@Service
@PayMethod({EPayMethod.ALI_PAY})
@Slf4j
public class AliPayService implements PayService {

    @Resource
    private AliPayBean aliPayBean;
    @Resource
    private PayOrderService payOrderService;

    /**
     * 普通公钥模式
     */
    private final static String NOTIFY_URL = "/trade/alipay/callback";
    private final static String RETURN_URL = "/aliPay/return_url";

    @Override
    public String pay(String payOrderUid) {
        AliPayApiConfig aliPayApiConfig = getApiConfig();
        AliPayApiConfigKit.setThreadLocalAppId(aliPayBean.getAppId());
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
        // 获取支付订单
        PayOrder payOrder = payOrderService.getById(payOrderUid);
        if (payOrder == null) {
            log.error("支付订单不存在: payOrderUid: {}", payOrderUid);
            throw new InsertException("支付订单不存在");
        }
        // 计算出支付现金
        double priceStr = payOrder.getPrice() / 100D;
        String totalAmount = "" + priceStr;
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(payOrder.getTitle());
        model.setTotalAmount(totalAmount);
        model.setStoreId(payOrder.getResourceUid());
        // 设置过期时间
        model.setTimeoutExpress("5m");
        model.setOutTradeNo(payOrder.getUid());
        String notifyUrl = aliPayBean.getDomain() + NOTIFY_URL;
        try {
            String resultStr = AliPayApi.tradePrecreatePayToResponse(model, notifyUrl).getBody();
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            String qrCodeUrl = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");

            // 构建返回
            BuyNowResponse buyNowResponse = new BuyNowResponse();
            buyNowResponse.setPayOrderUid(payOrder.getUid());
            buyNowResponse.setUrl(qrCodeUrl);
            return ResultUtil.successWithData(buyNowResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new QueryException("生成支付二维码失败");
    }

    @Override
    public String callback() {
        // 获取支付宝GET过来反馈信息
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = AliPayApi.toMap(request);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        try {
            boolean verifyResult = AlipaySignature.rsaCheckV1(map, aliPayBean.getPublicKey(), "UTF-8", "RSA2");
            if (!verifyResult) {
                log.error("订单校验失败");
            }
            System.out.println("return_url 验证成功");
            // 获取外部订单
            String orderUid = map.get("out_trade_no");
            PayOrder payOrder = payOrderService.getById(orderUid);
            if (payOrder == null) {
                log.error("获取订单失败, orderUid: " + orderUid);
                return "";
            }
            if (payOrder.getOrderStatus() != EOrderStatus.New) {
                log.error("订单已完成, orderUid: " + orderUid);
                return "";
            }
            payOrder.setOrderStatus(EOrderStatus.Finish);
            payOrder.updateById();

        } catch (Exception e) {
            log.error("支付回调异常");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String closePayOrder(PayOrder payOrder) {
        return null;
    }

    public AliPayApiConfig getApiConfig() {
        AliPayApiConfig aliPayApiConfig;
        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(aliPayBean.getAppId());
        } catch (Exception e) {
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(aliPayBean.getAppId())
                    .setAliPayPublicKey(aliPayBean.getPublicKey())
                    .setAppCertPath(aliPayBean.getAppCertPath())
                    .setAliPayCertPath(aliPayBean.getAliPayCertPath())
                    .setAliPayRootCertPath(aliPayBean.getAliPayRootCertPath())
                    .setCharset("UTF-8")
                    .setPrivateKey(aliPayBean.getPrivateKey())
                    .setServiceUrl(aliPayBean.getServerUrl())
                    .setSignType("RSA2")
                    // 普通公钥方式
                    .build();
            // 证书模式
//                    .buildByCert();
        }
        return aliPayApiConfig;
    }
}
