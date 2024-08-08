package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserMoment;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 动态事件处理器
 *
 * @author 陌溪
 * @date 2022年12月16日20:49:18
 */
@Slf4j
@Component
@EventType({EntityType.MOMENT})
public class MomentEventHandler extends AbstractEventHandler {
    @Resource
    private WebFeignClient webFeignClient;
    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserMoment userMoment = objectMapper.convertValue(domainEvent.getEntity(), UserMoment.class);
        switch (domainEvent.getEventAction()) {
            case MOMENT_ADD: {
                this.add(userMoment, domainEvent.isAdminOperate());
            }
            break;
            case MOMENT_EDIT: {
                this.edit(userMoment, domainEvent.isAdminOperate());
            }
            break;
            case MOMENT_DELETE: {
                this.delete(userMoment, domainEvent.isAdminOperate());
            }
            break;
            case MOMENT_AUDIT: {
                this.audit(userMoment, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 动态审核事件
     *
     * @param userMoment
     * @param isAdminOperate
     */
    public void audit(UserMoment userMoment, boolean isAdminOperate) {
        if (EPublish.PUBLISH.equals(userMoment.getIsPublish()) && EAuditStatus.AGREE.equals(userMoment.getAuditStatus())) {

            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Moment.getAction(), null, userMoment.getUid(), userMoment.getUserUid());
        }
        /**
         * 发送邮件
         */
        if (userMoment.getUserUid() != null) {
            User user = userService.getById(userMoment.getUserUid());
            StringBuilder text = new StringBuilder();
            text.append(String.format("<p>可爱的博主%s,您的动态</p>", user.getNickName()));
            if (EAuditStatus.REJECT.equals(userMoment.getAuditStatus())) {
                text.append("审核未通过,审核原因为" + userMoment.getRejectReason());
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else if (EAuditStatus.AGREE.equals(userMoment.getAuditStatus())) {
                text.append("审核已通过");
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else {
                log.error("审核状态异常,稿件为{}", userMoment.getUid());
            }
        }
        this.syncAggEsDoc(userMoment, EOperate.EDIT);
    }

    /**
     * 新增事件
     *
     * @param userMoment
     * @param isAdminOperate
     */
    public void add(UserMoment userMoment, boolean isAdminOperate) {
        if (EAuditStatus.AGREE.equals(userMoment.getAuditStatus()) && EPublish.PUBLISH.equals(userMoment.getIsPublish())) {
            // 增加用户积分
            userService.addUserCredits(ECreditType.Moment.getAction(), null, userMoment.getUid(), userMoment.getUserUid());
            // 通知关注的用户，发送动态通知
            asyncService.executeAsyncBusinessNotice(false, userMoment.getUserUid(), userMoment.getUid(), EBusinessType.MOMENT.getCode());
        }

        String atUserUid = StringUtils.getAtUserUid(userMoment.getContent());
        ;
        // 判断是否触发AT功能，触发AT后的动作
        if (StringUtils.isNotEmpty(atUserUid)) {
            atEventHandler.atEventHandler(userMoment.getUserUid(), atUserUid, userMoment.getUid(), "USER_MOMENT", userMoment.getContent());
            // 给AT的人发送一则消息
            webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, atUserUid);
        }

        // 发送后台站内信通知
        asyncService.executeAsyncBusinessBlackNotice(false, userMoment.getUserUid(), userMoment.getUid(), EBusinessType.MOMENT.getCode(), userMoment.getContent());
        // 更新ES索引
        this.syncAggEsDoc(userMoment, EOperate.ADD);
        // 执行任务
        userTaskService.action(Action.MOMENT, null, null);
    }

    /**
     * 更新动态
     *
     * @param userMoment
     * @param isAdminOperate
     */
    public void edit(UserMoment userMoment, boolean isAdminOperate) {
        this.syncAggEsDoc(userMoment, EOperate.EDIT);
    }

    /**
     * 删除动态
     *
     * @param userMoment
     * @param isAdminOperate
     */
    public void delete(UserMoment userMoment, boolean isAdminOperate) {
        if (StringUtils.isNotEmpty(userMoment.getUserUid())) {
            // 动态删除，扣除积分
            userService.addUserCredits(ECreditType.Delete_Moment.getAction(), null, userMoment.getUid(), userMoment.getUserUid());
        }
        this.syncAggEsDoc(userMoment, EOperate.DELETE);
    }

    /**
     * syncAggEsDoc 同步到ES
     *
     * @param userMoment
     * @param operate
     */
    private void syncAggEsDoc(UserMoment userMoment, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertMoment(userMoment);
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }
}
