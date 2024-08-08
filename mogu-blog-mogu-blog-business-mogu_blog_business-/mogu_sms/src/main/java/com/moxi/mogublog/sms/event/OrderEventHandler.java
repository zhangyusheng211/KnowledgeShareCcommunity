package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.schema.VipExtraConfig;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 关注事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.ORDER})
public class OrderEventHandler extends AbstractEventHandler {

    @Resource
    private WebFeignClient webFeignClient;

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        PayOrder payOrder = objectMapper.convertValue(domainEvent.getEntity(), PayOrder.class);
        switch (domainEvent.getEventAction()) {
            case ORDER_PAY_SUCCESS: {
                this.orderPaySuccess(payOrder);
            }
            break;
            case ORDER_CREATE_SUCCESS: {
                this.orderCreateSuccess(payOrder);
            }
            break;
        }
    }

    /**
     * 订单支付成功
     *
     * @param payOrder
     */
    public void orderPaySuccess(PayOrder payOrder) {
        // 判断订单是否完成
        if (!EOrderStatus.Finish.equals(payOrder.getOrderStatus())) {
            return;
        }

        // 如果是资源类型，需要更新该资源的下载数量
        if (EResourceType.RESOURCE.getType().equals(payOrder.getResourceType())) {
            // 通过id，获取资源
            com.moxi.mogublog.commons.entity.Resource resource = resourceService.getById(payOrder.getResourceUid());
            if (resource != null) {
                resource.setDownloadCount(resource.getDownloadCount() + 1);
                resource.updateById();
            }
        }

        // 判断是否是开通的VIP
        if (EResourceType.VIP.getType().equals(payOrder.getResourceType())) {
            setUserVip(payOrder);
        }

        // 判断是否是现金支付
        if (EPayType.CASH_PAY.getType() != payOrder.getPayType()) {
            return;
        }

        // 如果是赞赏，需要执行赞赏相关的任务
        if (EResourceType.SPONSOR.getType().equals(payOrder.getResourceType())) {
            // 执行赞赏次数任务或成就
            userTaskService.action(Action.SPONSOR_COUNT, null, null, payOrder.getUserUid());
            Map<String, Object> extra = new HashMap<>();
            extra.put("price", payOrder.getPrice());
            // 执行赞赏金额任务或成就
            userTaskService.action(Action.SPONSOR_FEE, null, extra, payOrder.getUserUid());
            // 通知前台，赞赏成功
            webFeignClient.sendDomainEventMessage(EWebDomainEvent.PAY_SUCCESS, payOrder.getUserUid());

            // 订单金额小于一角，不处理
            if (payOrder.getPrice() < 10) {
                return;
            }
            // 计算出需要增加的积分
            int addCredits = (int) (payOrder.getPrice() / 10);
            /**
             * 每次赞赏，将增加对应的积分
             */
            userService.addUserCredits(ECreditType.SPONSOR.getAction(), addCredits, payOrder.getUid(), payOrder.getUserUid());
        }
    }

    /**
     * 设置用户会员信息
     * @param payOrder
     */
    private void setUserVip(PayOrder payOrder) {
        // 获取当前下单的用户，给改VIP进行续费
        User user = userService.getById(payOrder.getUserUid());
        if (user == null) {
            log.error("[setUserVip] 会员充值失败，用户不存在， userUid: {}, orderUid: {}", payOrder.getUserUid(), payOrder.getUid());
            return;
        }
        // 通过id，获取资源
        VipConfig vipConfig = vipConfigService.getById(payOrder.getResourceUid());
        if (vipConfig == null) {
            log.error("[setUserVip] 会员充值失败，会员配置信息不存在， userUid: {}, orderUid: {}", payOrder.getUserUid(), payOrder.getUid());
            return;
        }

        if (vipConfig.getExtra() == null) {
            log.error("[setUserVip] 会员额外配置信息不存在，无法开通会员； userUid: {}, orderUid: {}", payOrder.getUserUid(), payOrder.getUid());
            return;
        }

        VipExtraConfig vipExtraConfig = JsonUtils.jsonToPojo(vipConfig.getExtra(), VipExtraConfig.class);
        if (vipExtraConfig == null) {
            log.error("[setUserVip] 会员额外配置信息转化失败，无法开通会员； userUid: {}, orderUid: {}", payOrder.getUserUid(), payOrder.getUid());
            return;
        }

        // 获取会员的生效时间
        int effectDay = vipConfig.getEffectDay();
        int userTag = vipConfig.getUserTag();

        // 判断用户是否有会员到期时间
        if (user.getVipDeadlineTime() == null) {
            // 如果会员已经过期，根据当前时间计算会员到期时间
            user.setVipDeadlineTime(DateUtils.getNextDate(new Date(), effectDay));
            user.setUserTag(userTag);
        } else if (user.getVipDeadlineTime().getTime() < System.currentTimeMillis()) {
            // 如果会员已经过期，根据当前时间计算会员到期时间
            user.setVipDeadlineTime(DateUtils.getNextDate(new Date(), effectDay));
            user.setUserTag(userTag);
        } else {
            // 如果会员未过期，直接在当前基础上累积时间
            user.setVipDeadlineTime(DateUtils.getNextDate(user.getVipDeadlineTime(), effectDay));
            // 如果当前会员等级更高，会进行覆盖操作
            if (userTag >= user.getUserTag()) {
                user.setUserTag(userTag);
            }
        }
        // 更新用户表
        boolean save = user.updateById();
        if (save) {
            log.info("[setUserVip] 成功给用户开通会员；userUid: {}, orderUid: {}", payOrder.getUserUid(), payOrder.getUid());
        }
        // 更新完成后，在刷新用户的标签
        userService.refreshUserCache(payOrder.getUserUid());
    }

    /**
     * 订单创建成功
     *
     * @param payOrder
     */
    public void orderCreateSuccess(PayOrder payOrder) {
        log.info("[orderCreateSuccess] 订单事件暂未实现");
    }


}
