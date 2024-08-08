package com.moxi.mogublog.pay.service.impl.PayServiceImpl;


import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.pay.annotation.PayMethod;
import com.moxi.mogublog.pay.global.SysConf;
import com.moxi.mogublog.pay.model.bean.YunGouOSWxPayBean;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.pay.service.PayService;
import com.moxi.mogublog.pay.utils.DomainEventUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.utils.WebUtils;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayMethod;
import com.moxi.mougblog.base.enums.ESecretType;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.yungouos.pay.util.PaySignUtil;
import com.yungouos.pay.wxpay.WxPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方微信支付服务
 *
 * @author 陌溪
 */
@Service
@PayMethod({EPayMethod.YUN_GOU_OS_WECHAT_PAY})
@Slf4j
public class YunGouOSWechatPayService implements PayService {

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private DomainEventUtil domainEventUtil;

    @Resource
    private SmsFeignClient smsFeignClient;

    /**
     * 普通公钥模式
     */
    private final static String NOTIFY_URL = "/trade/yunGouOSWxPay/callback";

    @Override
    public String pay(String payOrderUid) {
        YunGouOSWxPayBean yunGouOSWxPayBean = getYunGouOSWxPayBean();
        // 获取支付订单
        PayOrder payOrder = payOrderService.getById(payOrderUid);
        if (payOrder == null) {
            log.error("支付订单不存在: payOrderUid: {}", payOrderUid);
            throw new InsertException("支付订单不存在");
        }
        double price = payOrder.getPrice() / 100D;
        String totalAmount = "" + price;

        // 参数文档：https://open.pay.yungouos.com/#/api/api/pay/wxpay/nativePay
        String result = WxPay.nativePay(payOrder.getUid(),
                totalAmount, yunGouOSWxPayBean.getMchId(), payOrder.getTitle(),
                "1",  // 返回类型（1、返回微信原生的支付连接需要自行生成二维码；2、直接返回付款二维码地址，页面上展示即可。不填默认1 ）
                null, payOrder.getUid(), yunGouOSWxPayBean.getDomain().concat(NOTIFY_URL), null, null, "0", null, null, yunGouOSWxPayBean.getKey());

        // 订单创建成功
        domainEventUtil.publishEvent(EventAction.ORDER_CREATE_SUCCESS, payOrder);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(SysConf.URL, result);
        resultMap.put(SysConf.PAY_ORDER_UID, payOrderUid);
        return ResultUtil.successWithData(resultMap);
    }

    @Override
    public String callback() {
        YunGouOSWxPayBean yunGouOSWxPayBean = getYunGouOSWxPayBean();
        HttpServletRequest request = RequestHolder.getRequest();
        log.info("接收到第三方支付回调通知");
        try {
            boolean sign = PaySignUtil.checkNotifySign(request, yunGouOSWxPayBean.getKey());
            if (!sign) {
                log.error("签证校验未通过");
                return "FAIL";
            }
            String orderUid = request.getParameter("outTradeNo");
            // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
            // 注意此处签名方式需与统一下单的签名类型一致
            if (StringUtils.isNotEmpty(orderUid)) {
                // 更新订单信息
                PayOrder payOrder = payOrderService.getById(orderUid);
                if (payOrder == null) {
                    log.error("获取订单失败, orderUid: {}", orderUid);
                    // 发送通知等
                    return "FAIL";
                }
                if (EOrderStatus.Finish.equals(payOrder.getOrderStatus())) {
                    log.info("支付已完成, orderUid: {}", orderUid);
                    return "SUCCESS";
                }
                payOrder.setOrderStatus(EOrderStatus.Finish);
                payOrder.updateById();

                // 执行分账逻辑

                // 订单支付成功
                domainEventUtil.publishEvent(EventAction.ORDER_PAY_SUCCESS, payOrder);
                return "SUCCESS";
            }
        } catch (Exception e) {
            log.error("[callback] 第三方微信支付回调异常");
            e.printStackTrace();
        }
        return "FAIL";
    }

    @Override
    public String closePayOrder(PayOrder payOrder) {
        YunGouOSWxPayBean yunGouOSWxPayBean = getYunGouOSWxPayBean();
        String result = WxPay.closeOrder(payOrder.getUid(), yunGouOSWxPayBean.getMchId(), yunGouOSWxPayBean.getKey());
        log.info("[closePayOrder] result: {}", result);
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    private YunGouOSWxPayBean getYunGouOSWxPayBean() {
        YunGouOSWxPayBean yunGouOSWxPayBean = new YunGouOSWxPayBean();
        // 获取YunGouOS微信密钥配置
        SecretConfig secretConfig = smsFeignClient.getSecretConfig(ESecretType.PAY.getType(), EPayMethod.YUN_GOU_OS_WECHAT_PAY.getType() + "");
        if (secretConfig == null) {
            log.error("[getYunGouOSWxPayBean] 暂未配置YungouOS支付能力，请到后台管理->系统配置->支付配置完成操作");
            throw new BusinessException("暂未配置YungouOS支付能力，请到后台管理->系统配置->支付配置完成操作");
        }
        yunGouOSWxPayBean.setDomain(secretConfig.getRequestUrl());
        yunGouOSWxPayBean.setKey(secretConfig.getBizKey());
        yunGouOSWxPayBean.setMchId(secretConfig.getBizId());
        return yunGouOSWxPayBean;
    }
}
