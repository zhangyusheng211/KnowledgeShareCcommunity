package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 博客事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.QUESTION})
public class QuestionEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Question question = objectMapper.convertValue(domainEvent.getEntity(), Question.class);
        switch (domainEvent.getEventAction()) {
            case QUESTION_ADD: {
                this.add(question, domainEvent.isAdminOperate());
            }
            break;
            case QUESTION_EDIT: {
                this.edit(question, domainEvent.isAdminOperate());
            }
            break;
            case QUESTION_DELETE: {
                this.delete(question, domainEvent.isAdminOperate());
            }
            break;
            case QUESTION_AUDIT: {
                this.audit(question, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 问答审核通过事件
     *
     * @param question
     * @param isAdminOperate
     */
    public void audit(Question question, boolean isAdminOperate) {
        // 审批通过，增加积分，发送站内通知
        if (EPublish.PUBLISH.equals(question.getIsPublish()) && EAuditStatus.AGREE.equals(question.getAuditStatus())) {

            /**
             * 站内信通知
             */
            this.sendNotice(question);

            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Question.getAction(), null, question.getUid(), question.getUserUid());
        }
        /**
         * 发送邮件
         */
        if (question.getUserUid() != null) {
            User user = userService.getById(question.getUserUid());
            StringBuilder text = new StringBuilder();
            text.append(String.format("<p>可爱的博主%s, 您的问答稿件</p>", user.getNickName()));
            if (EAuditStatus.REJECT.equals(question.getAuditStatus())) {
                text.append("审核未通过,审核原因为" + question.getRejectReason());
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else if (EAuditStatus.AGREE.equals(question.getAuditStatus())) {
                text.append("审核已通过");
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else {
                log.error("审核状态异常,稿件为{}", question.getUid());
            }
        }

        /**
         * 同步索引
         */
        this.syncAggEsDoc(question, EOperate.AUDIT);
    }

    /**
     * 新增问答
     *
     * @param question
     * @param isAdminOperate
     */
    public void add(Question question, boolean isAdminOperate) {
        /**
         * 新增情况下 审核通过  说明是免审
         * 一般都不会走这一层
         * 博主属于特殊角色
         * 在新增博客选择发布时需要给与积分
         */
        if (EAuditStatus.AGREE.equals(question.getAuditStatus()) && EPublish.PUBLISH.equals(question.getIsPublish())) {
            /**
             * 站内信通知
             */
            this.sendNotice(question);
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Question.getAction(), null, question.getUid(), question.getUserUid());
        }


        /**
         * 新增时候选择发布的博客都给与网站管理员发送邮件提示
         */
        if (EPublish.PUBLISH.equals(question.getIsPublish())) {
            /**
             * 通知运营
             */
            this.sendEmailToManager(question);
        }

        // 用户投稿的问答，需要发送后台站内信，提示需要审核
        if (StringUtils.isNotEmpty(question.getUserUid())) {
            asyncService.executeAsyncBusinessBlackNotice(false, question.getUserUid(), question.getUid(), EBusinessType.QUESTION.getCode(), question.getTitle());
        }

        // 同步索引
        this.syncAggEsDoc(question, EOperate.ADD);

        // 新增问答，判断任务进行情况
        userTaskService.action(Action.QUESTION, null, null);
    }


    /**
     * 编辑问答
     *
     * @param question
     * @param isAdminOperate
     */
    public void edit(Question question, boolean isAdminOperate) {
        // 同步索引
        this.syncAggEsDoc(question, EOperate.EDIT);
    }

    /**
     * 删除问答
     *
     * @param question
     * @param isAdminOperate
     */
    public void delete(Question question, boolean isAdminOperate) {
        // 删除事件，扣除增加的积分
        if (StringUtils.isNotEmpty(question.getUserUid())) {
            userService.addUserCredits(ECreditType.Delete_Question.getAction(), null, question.getUid(), question.getUserUid());
        }
        // 删除索引
        this.syncAggEsDoc(question, EOperate.DELETE);
    }

    /**
     * 发送通知
     *
     * @param question
     */
    public void sendNotice(Question question) {
        /**
         * 上架操作
         */
        if (EAuditStatus.AGREE.equals(question.getAuditStatus()) && EPublish.PUBLISH.equals(question.getIsPublish())) {
            /**
             * 站内信通知
             */
            if (question.getUserUid() != null) {
                asyncService.executeAsyncBusinessNotice(false, question.getUserUid(), question.getUid(), EBusinessType.QUESTION.getCode());
            } else {
                asyncService.executeAsyncBusinessNotice(true, question.getAdminUid(), question.getUid(), EBusinessType.QUESTION.getCode());
            }
        }
    }

    /**
     * 发送邮件通知管理员
     *
     * @param question
     */
    public void sendEmailToManager(Question question) {
        /**
         * 发送邮件用纸
         */
        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
            if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                String emailContent = "收到用户的投稿问答：" + question.getTitle();
                rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), emailContent);
            } else {
                log.error("网站没有配置通知接收的邮箱地址！");
            }
        }
    }

    /**
     * AggEsDoc转载
     *
     * @param question
     * @return
     */
    private AggEsDoc convertQuestion2AggEsDoc(Question question) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertQuestion(question);
        Question questionTemp = questionService.getById(question.getUid());
        if (questionTemp != null) {
            aggEsDoc.setOid(questionTemp.getOid());
        }
        return aggEsDoc;
    }

    /**
     * 更新聚合AggEsDoc
     *
     * @param question
     * @return
     */
    private void syncAggEsDoc(Question question, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertQuestion(question);
        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
            Question questionTemp = questionService.getById(question.getUid());
            if (questionTemp != null) {
                aggEsDoc.setOid(questionTemp.getOid());
            }
        }
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }
}
