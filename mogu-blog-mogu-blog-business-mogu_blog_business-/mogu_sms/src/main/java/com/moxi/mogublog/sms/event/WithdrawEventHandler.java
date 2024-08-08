package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.PayOrder;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.Withdraw;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 关注事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.WITHDRAW})
public class WithdrawEventHandler extends AbstractEventHandler {

    @Resource
    private WebFeignClient webFeignClient;

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Withdraw withdraw = objectMapper.convertValue(domainEvent.getEntity(), Withdraw.class);
        switch (domainEvent.getEventAction()) {
            case ADD_WITHDRAW: {
                this.addWithdraw(withdraw);
            }
            break;
            case AUDIT_WITHDRAW: {
                this.auditWithdraw(withdraw);
            }
            break;
        }
    }

    /**
     * 新增提现
     * @param withdraw
     */
    public void addWithdraw(Withdraw withdraw) {
        log.info("[auditWithdraw] 用户发起审核");

        /**
         * 进入审批流程的提现，会给后台发送站内信通知
         */
        if (!EAuditStatus.AGREE.equals(withdraw.getAuditStatus())) {
            DecimalFormat df = new DecimalFormat("0.00");
            String result = df.format(withdraw.getAmount().doubleValue() / 100D);
            asyncService.executeAsyncBusinessBlackNotice(false, withdraw.getUserUid(), withdraw.getUid(), EBusinessType.WITHDRAW.getCode(), "申请提现金额: ¥" + result);
        }
    }

    /**
     * 提现审核
     *
     * @param withdraw
     */
    public void auditWithdraw(Withdraw withdraw) {
        log.info("[auditWithdraw] 提现审核通过");
        // 给用户发送提现成功的领域事件
        /**
         * 对用户发送邮件和站内信
         */
        User user = userService.getById(withdraw.getUserUid());
        StringBuilder text = new StringBuilder();
        text.append("您的发起的提现申请");
        if (EAuditStatus.REJECT.equals(withdraw.getAuditStatus())) {
            text.append("审核未通过，未通过原因：" + withdraw.getRejectReason());
            rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
        } else if (EAuditStatus.AGREE.equals(withdraw.getAuditStatus())) {
            text.append("审核已通过");
            rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
        } else {
            log.error("审核状态异常, 提现单号：{}", withdraw.getUid());
        }

        // 发送提现审核站内信
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUserUid(withdraw.getUserUid());
        noticeVO.setContent(text.toString());
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        noticeVO.setBusinessType(EBusinessType.WITHDRAW_SUCCESS.getCode());
        noticeVO.setBusinessUid(withdraw.getUid());
        noticeVO.setIsBlack(Constants.NUM_ZERO);
        asyncService.executeAsyncBusinessNotice(noticeVO);
    }

}
