package com.moxi.mogublog.xo.event.listener;

import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.event.questionEvent.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.service.SystemConfigService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class QuestionListener {

    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 系统配置；服务
     */
    @Resource
    private SystemConfigService systemConfigService;

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

    @Resource
    private QuestionService questionService;

    /**
     * 问答审核通过事件
     *
     * @param event
     */
    @EventListener(value = QuestionAuditEvent.class)
    public void audit(QuestionEvent event) {
        Question question = (Question) event.getSource();
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
            text.append(String.format("<p>可爱的博主%s,您的问答稿件</p>", user.getNickName()));
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

    @EventListener(value = QuestionAddEvent.class)
    public void add(QuestionEvent event) {
        Question question = (Question) event.getSource();
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
    }

    @EventListener(value = QuestionEditEvent.class)
    public void edit(QuestionEvent event) {
        Question question = (Question) event.getSource();
        // 同步索引
        this.syncAggEsDoc(question, EOperate.EDIT);
    }

    @EventListener(value = QuestionDeleteEvent.class)
    public void delete(QuestionEvent event) {
        Question question = (Question) event.getSource();
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
     * AggEsDoc装好
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
     * AggEsDoc装好
     *
     * @param question
     * @return
     */
    private AggEsDoc syncAggEsDoc(Question question, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertQuestion(question);
        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
            Question questionTemp = questionService.getById(question.getUid());
            if (questionTemp != null) {
                aggEsDoc.setOid(questionTemp.getOid());
            }
        }
        return aggEsDoc;
    }
}
