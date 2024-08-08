package com.moxi.mogublog.pay.service.impl.PayServiceImpl;

import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.schema.BuyNowResponse;
import com.moxi.mogublog.commons.schema.CreditsChangeRequest;
import com.moxi.mogublog.pay.annotation.PayMethod;
import com.moxi.mogublog.pay.global.SysConf;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.pay.service.PayService;
import com.moxi.mogublog.pay.utils.DomainEventUtil;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayMethod;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 积分支付服务
 *
 * @author 陌溪
 */
@Service
@PayMethod({EPayMethod.CREDITS_PAY})
@Slf4j
public class CreditsPayService implements PayService {
    @Resource
    private PayOrderService payOrderService;

    @Resource
    private WebFeignClient webFeignClient;

    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public String pay(String payOrderUid) {
        // 获取支付订单
        PayOrder payOrder = payOrderService.getById(payOrderUid);
        if (payOrder == null) {
            log.error("支付订单不存在: payOrderUid: {}", payOrderUid);
            throw new InsertException("支付订单不存在");
        }
        // 获取需要支付的积分
        int price = payOrder.getPrice().intValue();
        CreditsChangeRequest creditsChangeRequest = new CreditsChangeRequest();
        // 将资源类型变成积分变更类型
        EResourceType resourceType = EResourceType.getType(payOrder.getResourceType());
        ECreditType creditType = ECreditType.getCreditsByResourceType(resourceType);
        creditsChangeRequest.setCreditType(creditType);
        // 下单，肯定是扣除积分的
        creditsChangeRequest.setChangeCredits(-price);
        creditsChangeRequest.setResourceUid(payOrder.getResourceUid());
        creditsChangeRequest.setResourceType(resourceType);
        String result = webFeignClient.creditsChange(creditsChangeRequest);
        // 检查是否支付成功
        Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
        if (SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE))) {
            log.info("支付成功");
            // 更新订单状态
            payOrder.setOrderStatus(EOrderStatus.Finish);
            payOrder.updateById();
            // 订单支付成功
            domainEventUtil.publishEvent(EventAction.ORDER_PAY_SUCCESS, payOrder);

            BuyNowResponse buyNowResponse = new BuyNowResponse();
            buyNowResponse.setPayOrderUid(payOrder.getUid());
            return ResultUtil.successWithData(buyNowResponse);
        } else {
            log.error("[积分支付失败] 支付失败，失败原因: {}", resultMap.get(SysConf.MESSAGE));
            if (resultMap.get(SysConf.MESSAGE) != null) {
                throw new QueryException(resultMap.get(SysConf.MESSAGE).toString());
            }
            throw new QueryException("支付异常，请稍后再试");
        }
    }

    @Override
    public String callback() {
        return null;
    }

    @Override
    public String closePayOrder(PayOrder payOrder) {
        return null;
    }
}
