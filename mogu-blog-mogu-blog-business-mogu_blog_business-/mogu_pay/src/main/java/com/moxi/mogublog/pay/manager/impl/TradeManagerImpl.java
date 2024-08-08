package com.moxi.mogublog.pay.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.schema.BuyNowResponse;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mogublog.pay.factory.PayServiceFactory;
import com.moxi.mogublog.pay.global.SQLConf;
import com.moxi.mogublog.pay.global.SysConf;
import com.moxi.mogublog.pay.manager.TradeManager;
import com.moxi.mogublog.pay.model.vo.BuyVO;
import com.moxi.mogublog.pay.model.vo.SponsorVO;
import com.moxi.mogublog.pay.service.PayOrderService;
import com.moxi.mogublog.pay.utils.DomainEventUtil;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订单管理模块
 * 他负责 业务怎么做
 * manager 负责 把service里涉及多表的拆出来 放在这里处理
 * service 是具体的业务实现 db操作 一般是单表业务操作
 *
 * @author 遇见
 */
@Service(value = "tradeManager")
@Slf4j
public class TradeManagerImpl implements TradeManager {

    @Resource
    private WebFeignClient webFeignClient;
    @Resource
    private PayOrderService payOrderService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public String buyNow(BuyVO buyVO) {
        log.info("[buyNow] buyNow come in, req: {}", JsonUtils.objectToJson(buyVO));
        String userUid = RequestHolder.getUserUid();
        int userTag = RequestHolder.getUserTag();
        // 获取资源类型
        EResourceType resourceType = EResourceType.getType(buyVO.getResourceType().getType());
        // 获取商品请求
        ProductRequest productRequest = new ProductRequest();
        productRequest.setResourceUid(buyVO.getResourceUid());
        productRequest.setResourceType(resourceType.getType());
        String product = webFeignClient.getProduct(productRequest);
        if (StringUtils.isEmpty(product)) {
            throw new QueryException("请求商品不存在，下单失败");
        }
        // 获取商品信息失败
        ProductVO productVO = JsonUtils.jsonToPojo(product, ProductVO.class);
        if (productVO == null) {
            throw new QueryException("获取商品信息失败");
        }

        // 判断用户是否购买该商品
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.RESOURCE_UID, productVO.getResourceUid());
        queryWrapper.eq(SQLConf.RESOURCE_TYPE, productVO.getResourceType());
        queryWrapper.last(SysConf.LIMIT_ONE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        PayOrder payOrder = payOrderService.getOne(queryWrapper);

        // 如果订单存在，需要判断是否完成支付
        boolean buildOrder = true;
        if (payOrder != null) {
            // 会员商品允许反复购买
            // 非会员商品只能购买一次
            if (EOrderStatus.Finish.equals(payOrder.getOrderStatus()) && !EResourceType.VIP.getType().equals(productVO.getResourceType())) {
                throw new QueryException("订单已经完成支付");
            }
            // 还存在未支付的订单，需要重新走创建流程
            if (EOrderStatus.New.equals(payOrder.getOrderStatus())) {
                buildOrder = false;
            }
        }

        // 调用支付接口，进行支付处理
        EPayMethod payMethod = buyVO.getPayMethod();

        // 判断是否是折扣商品
        long price = productVO.getPrice();
        // 折扣商品，需要判断是否在折扣区间
        if (EChargeType.Discount.getType() == productVO.getChargeType()) {
            if (productVO.getDiscountStartTime() == null || productVO.getDiscountEndTime() == null) {
                // 判断是否设置折扣时间
            } else {
                // 判断是否命中折扣时间
                boolean checkIn = DateUtils.checkNowBetweenIn(productVO.getDiscountStartTime(), productVO.getDiscountEndTime());
                if (checkIn) {
                    price = productVO.getDiscountPrice();
                }
            }
        } else if (EChargeType.VIP_Free.getType() == productVO.getChargeType()) {
            // 判断是否是会员，如果是会员，价格为0元，非会员正常价
            if (userTag >= 1) {
                price = 0L;
            }
        }

        // 如果订单为空，则需要创建订单
        if (buildOrder) {
            log.info("[buyNow] 订单不存在，重新创建");
            payOrder = new PayOrder();
            payOrder.setUserUid(userUid);
            // TODO.mo 代理分账，需要设置分账人
            payOrder.setMerchantUserUid("");
            payOrder.setResourceUid(productVO.getResourceUid());
            payOrder.setResourceType(productVO.getResourceType());
            payOrder.setPrice(price);
            payOrder.setProductPrice(productVO.getPrice());
            payOrder.setTitle(productVO.getName());
            payOrder.setSummary(productVO.getSummary());
            payOrder.setSnapshot(product);
            payOrder.setPayMethod(payMethod.getType());
            payOrder.setPayType(productVO.getPayType());
            // 0元订单，订单状态直接变成已完成
            if (price == 0) {
                payOrder.setOrderStatus(EOrderStatus.Finish);
            } else {
                payOrder.setOrderStatus(EOrderStatus.New);
            }
            boolean isSave = payOrder.insert();
            if (!isSave) {
                log.error("[buyNow] 更新订单失败");
                throw new QueryException("更新订单失败");
            }
        } else {
            log.info("[buyNow] 重新设置订单价格");
            payOrder.setPrice(price);
            payOrder.setProductPrice(productVO.getPrice());
            if (price == 0) {
                payOrder.setOrderStatus(EOrderStatus.Finish);
            } else {
                payOrder.setOrderStatus(EOrderStatus.New);
            }
            payOrder.updateById();
        }

        // 如果是免费0元订单，直接支付完成，无需后续的扣款流程
        if (price == 0) {
            // 订单支付成功
            domainEventUtil.publishEvent(EventAction.ORDER_PAY_SUCCESS, payOrder);

            // 构建返回
            BuyNowResponse buyNowResponse = new BuyNowResponse();
            buyNowResponse.setPayOrderUid(payOrder.getUid());
            return ResultUtil.successWithData(buyNowResponse);
        }

        return PayServiceFactory.getPayService(payMethod).pay(payOrder.getUid());
    }

    @Override
    public String sponsor(SponsorVO sponsorVO) {
        log.info("[sponsor] sponsor come in, req: {}", JsonUtils.objectToJson(sponsorVO));
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            throw new InsertException("用户暂未登录，请先登录后再操作吧~");
        }
        if (sponsorVO.getPrice() <= 0) {
            throw new InsertException("赞助金额不能为空");
        }
        // 查询用户下未支付的订单
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.RESOURCE_TYPE, EResourceType.SPONSOR.getType());
        queryWrapper.eq(SQLConf.ORDER_STATUS, EOrderStatus.New);
        queryWrapper.last(SysConf.LIMIT_ONE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        PayOrder payOrder = payOrderService.getOne(queryWrapper);

        // 价格不一致，判断是否需要生成订单
        boolean buildOrder = true;
        if (payOrder != null) {
            long price = sponsorVO.getPrice().longValue();
            // 如果价格不同，需要重新生成订单ID
            if (price == payOrder.getPrice()) {
                buildOrder = false;
            }
        }

        if (payOrder == null || buildOrder) {
            // 没有历史订单，重新构建生成
            payOrder = new PayOrder();
            payOrder.setUserUid(userUid);
            // TODO.mo 代理分账，需要设置分账人
            payOrder.setMerchantUserUid("");
            payOrder.setResourceUid(StringUtils.getUUID());
            payOrder.setResourceType(EResourceType.SPONSOR.getType());
            payOrder.setPrice(sponsorVO.getPrice().longValue());
            payOrder.setPayType(EPayType.CASH_PAY.getType());
            payOrder.setPayMethod(EPayMethod.YUN_GOU_OS_WECHAT_PAY.getType()); // 目前只接入了第三方微信支付，写死
            payOrder.setProductPrice(sponsorVO.getPrice().longValue());
            payOrder.setTitle("社区网站赞助");
            payOrder.setSummary("社区网站赞助");
            payOrder.setOrderStatus(EOrderStatus.New);
            boolean isSave = payOrder.insert();
            if (!isSave) {
                log.error("[buyNow] 更新订单失败");
                throw new QueryException("更新订单失败");
            }
        }
        return PayServiceFactory.getPayService(EPayMethod.YUN_GOU_OS_WECHAT_PAY).pay(payOrder.getUid());
    }

    @Override
    public Boolean closePayOrder(int hour) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.ORDER_STATUS, EOrderStatus.New);
        Date beforeDate = new Date(System.currentTimeMillis() - hour * 60 * 60 * 1000L);
        queryWrapper.le(SQLConf.CREATE_TIME, beforeDate);
        List<PayOrder> payOrderList = payOrderService.list(queryWrapper);
        if (payOrderList.size() == 0) {
            return true;
        }
        // 调度对应实现，去关闭订单
        for (PayOrder payOrder : payOrderList) {
            try {
                EPayMethod payMethod = EPayMethod.getPayMethod(payOrder.getPayMethod());
                PayServiceFactory.getPayService(payMethod).closePayOrder(payOrder);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                payOrder.setOrderStatus(EOrderStatus.OverTimeCancel);
                payOrder.updateById();
            }
        }
        return true;
    }
}
