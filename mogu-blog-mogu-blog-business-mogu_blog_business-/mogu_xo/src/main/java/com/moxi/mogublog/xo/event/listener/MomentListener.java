package com.moxi.mogublog.xo.event.listener;

import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserMoment;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.event.momentEvent.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.service.UserMomentService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MomentListener {

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 简易的mq邮件封装工具类
     */
    @Resource
    private RabbitMqUtil rabbitMqUtil;

    /**
     * 异步执行服务
     */
    @Resource
    private AsyncService asyncService;

    /**
     * 动态服务
     */
    @Resource
    private UserMomentService userMomentService;

    /**
     * 动态审核事件
     *
     * @param event
     */
    @EventListener(value = MomentAuditEvent.class)
    public void audit(MomentEvent event) {
        UserMoment userMoment = (UserMoment) event.getSource();
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
     * @param event
     */
    @EventListener(value = MomentAddEvent.class)
    public void add(MomentEvent event) {
        UserMoment userMoment = (UserMoment) event.getSource();

        if (EAuditStatus.AGREE.equals(userMoment.getAuditStatus()) && EPublish.PUBLISH.equals(userMoment.getIsPublish())) {
            // 增加用户积分
            userService.addUserCredits(ECreditType.Moment.getAction(), null, userMoment.getUid(), userMoment.getUserUid());
            // 通知关注的用户，发送动态通知
            asyncService.executeAsyncBusinessNotice(false, userMoment.getUserUid(), userMoment.getUid(), EBusinessType.MOMENT.getCode());
        }
        // 发送后台站内信通知
        asyncService.executeAsyncBusinessBlackNotice(false, userMoment.getUserUid(), userMoment.getUid(), EBusinessType.MOMENT.getCode(), userMoment.getContent());
        // 更新ES索引
        this.syncAggEsDoc(userMoment, EOperate.ADD);
    }

    @EventListener(value = MomentEditEvent.class)
    public void edit(MomentEvent event) {
        UserMoment userMoment = (UserMoment) event.getSource();
        this.syncAggEsDoc(userMoment, EOperate.EDIT);
    }

    @EventListener(value = MomentDeleteEvent.class)
    public void delete(MomentEvent event) {
        UserMoment userMoment = (UserMoment) event.getSource();
        if (StringUtils.isNotEmpty(userMoment.getUserUid())) {
            // 动态删除，扣除积分
            userService.addUserCredits(ECreditType.Delete_Moment.getAction(), null, userMoment.getUid(), userMoment.getUserUid());
        }
        this.syncAggEsDoc(userMoment, EOperate.DELETE);
    }

    private void syncAggEsDoc(UserMoment userMoment, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertMoment(userMoment);
//        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
//            log.info("syncAggEsDoc: aggEsDoc oid is nil");
//            UserMoment userMomentTemp = userMomentService.getById(userMoment.getUid());
//            if (userMomentTemp != null) {
//                aggEsDoc.setOid(userMomentTemp.getOid());
//            }
//            /**
//             * 更新索引
//             */
//        }
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }


}
