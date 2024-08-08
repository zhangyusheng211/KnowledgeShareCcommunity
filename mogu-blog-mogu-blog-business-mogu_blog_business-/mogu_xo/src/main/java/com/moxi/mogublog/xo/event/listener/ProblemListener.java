package com.moxi.mogublog.xo.event.listener;

import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.event.problemEvent.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.service.ProblemService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ProblemListener {

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

    @Resource
    private ProblemService problemService;

    /**
     * 面经新增事件
     *
     * @param event
     */
    @EventListener(value = ProblemAddEvent.class)
    public void add(ProblemEvent event) {
        Problem problem = (Problem) event.getSource();
        if (EAuditStatus.AGREE.equals(problem.getAuditStatus()) && EPublish.PUBLISH.equals(problem.getIsPublish())) {
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Problem.getAction(), null, problem.getUid(), problem.getUserUid());
        }
        //发布后台系统消息通知
        if (!EAuditStatus.AGREE.equals(problem.getAuditStatus()) || !EPublish.PUBLISH.equals(problem.getIsPublish())) {
            this.sendBlackNotice(problem);
        }
        this.syncAggEsDoc(problem, EOperate.ADD);
    }


    /**
     * 面经审核事件
     *
     * @param event
     */
    @EventListener(value = ProblemAuditEvent.class)
    public void audit(ProblemEvent event) {
        Problem problem = (Problem) event.getSource();
        if (EPublish.PUBLISH.equals(problem.getIsPublish()) && EAuditStatus.AGREE.equals(problem.getAuditStatus())) {

            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Problem.getAction(), null, problem.getUid(), problem.getUserUid());
        }
        /**
         * 发送邮件
         */
        if (problem.getUserUid() != null) {
            User user = userService.getById(problem.getUserUid());
            StringBuilder text = new StringBuilder();
            text.append(String.format("<p>可爱的博主%s,您的面经稿件</p>", user.getNickName()));
            if (EAuditStatus.REJECT.equals(problem.getAuditStatus())) {
                text.append("审核未通过,审核原因为" + problem.getRejectReason());
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else if (EAuditStatus.AGREE.equals(problem.getAuditStatus())) {
                text.append("审核已通过");
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else {
                log.error("审核状态异常,稿件为{}", problem.getUid());
            }
        }
        this.syncAggEsDoc(problem, EOperate.EDIT);
    }

    @EventListener(value = ProblemEditEvent.class)
    public void edit(ProblemEvent event) {
        Problem problem = (Problem) event.getSource();
        this.syncAggEsDoc(problem, EOperate.EDIT);
    }

    @EventListener(value = ProblemDeleteEvent.class)
    public void delete(ProblemEvent event) {
        Problem problem = (Problem) event.getSource();
        // 问题删除，扣除积分
        if (StringUtils.isNotEmpty(problem.getUserUid())) {
            userService.addUserCredits(ECreditType.Delete_Problem.getAction(), null, problem.getUid(), problem.getUserUid());
        }
        this.syncAggEsDoc(problem, EOperate.DELETE);
    }

    public void sendBlackNotice(Problem problem) {
        /**
         * 进入审批流程的博客 会给后台发送站内信通知
         */
        if (!EAuditStatus.AGREE.equals(problem.getAuditStatus()) || !EPublish.PUBLISH.equals(problem.getIsPublish())) {
            asyncService.executeAsyncBusinessBlackNotice(false, problem.getUserUid(), problem.getUid(), EBusinessType.PROBLEM.getCode(), problem.getTitle());
        }

    }

    /**
     * AggEsDoc装好
     *
     * @param problem
     * @return
     */
    private AggEsDoc syncAggEsDoc(Problem problem, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertProblem(problem);
        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
            Problem problemTemp = problemService.getById(problem.getUid());
            if (problemTemp != null) {
                aggEsDoc.setOid(problemTemp.getOid());
            }
        }
        return aggEsDoc;
    }
}
