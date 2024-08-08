package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 面经事件处理器
 *
 * @author 陌溪
 * @date 2022年12月16日20:49:18
 */
@Slf4j
@Component
@EventType({EntityType.PROBLEM})
public class ProblemEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Problem problem = objectMapper.convertValue(domainEvent.getEntity(), Problem.class);
        switch (domainEvent.getEventAction()) {
            case PROBLEM_ADD: {
                this.add(problem, domainEvent.isAdminOperate());
            }
            break;
            case PROBLEM_EDIT: {
                this.edit(problem, domainEvent.isAdminOperate());
            }
            break;
            case PROBLEM_DELETE: {
                this.delete(problem, domainEvent.isAdminOperate());
            }
            break;
            case PROBLEM_AUDIT: {
                this.audit(problem, domainEvent.isAdminOperate());
            }
            break;
            case PROBLEM_HOLD: {
                this.hold(problem, domainEvent.isAdminOperate());
            }
            break;
            case PROBLEM_VISIT: {
                this.visit(problem, domainEvent.isAdminOperate());
            }
            break;
        }
    }


    /**
     * 面经新增事件
     *
     * @param problem
     * @param isAdminOperate
     */
    public void add(Problem problem, boolean isAdminOperate) {
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

        // 同步更新ES
        this.syncAggEsDoc(problem, EOperate.ADD);

        // 新增面经，判断任务进行情况
        userTaskService.action(Action.PROBLEM, null, null);
    }


    /**
     * 面经审核事件
     *
     * @param problem
     * @param isAdminOperate
     */
    public void audit(Problem problem, boolean isAdminOperate) {
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

    /**
     * 编辑问题
     *
     * @param problem
     * @param isAdminOperate
     */
    public void edit(Problem problem, boolean isAdminOperate) {
        this.syncAggEsDoc(problem, EOperate.EDIT);
    }

    /**
     * 删除面经事件
     *
     * @param problem
     * @param isAdminOperate
     */
    public void delete(Problem problem, boolean isAdminOperate) {
        // 问题删除，扣除积分
        if (StringUtils.isNotEmpty(problem.getUserUid())) {
            userService.addUserCredits(ECreditType.Delete_Problem.getAction(), null, problem.getUid(), problem.getUserUid());
        }
        this.syncAggEsDoc(problem, EOperate.DELETE);
    }

    /**
     * 掌握问题
     *
     * @param problem
     * @param isAdminOperate
     */
    public void hold(Problem problem, boolean isAdminOperate) {

    }

    /**
     * 问答访问
     * @param problem
     * @param isAdminOperate
     */
    public void visit(Problem problem, boolean isAdminOperate) {
        // 执行面经任务
        Map<String, Object> map = new HashMap<>();
        map.put(SysConf.TO_USER_UID, problem.getUserUid());
        map.put(SysConf.RESOURCE_UID, problem.getUid());
        userTaskService.action(Action.TO_PROBLEM_VISIT, null, map, problem.getUserUid());

        // 执行面经访问任务
        if (StringUtils.isNotEmpty(ThreadLocalUtil.getUserUid())) {
            userTaskService.action(Action.PROBLEM_VISIT, null, new HashMap<>());
        }
    }


    /**
     * 发送后台通知
     *
     * @param problem
     */
    public void sendBlackNotice(Problem problem) {
        /**
         * 进入审批流程的博客 会给后台发送站内信通知
         */
        if (!EAuditStatus.AGREE.equals(problem.getAuditStatus()) || !EPublish.PUBLISH.equals(problem.getIsPublish())) {
            asyncService.executeAsyncBusinessBlackNotice(false, problem.getUserUid(), problem.getUid(), EBusinessType.PROBLEM.getCode(), problem.getTitle());
        }
    }

    /**
     * AggEsDoc装载
     *
     * @param problem
     * @return
     */
    private void syncAggEsDoc(Problem problem, EOperate operate) {
        AggEsDoc aggEsDoc = AggEsDocConvert.convertProblem(problem);
        if (EOperate.ADD == operate && aggEsDoc.getOid() == null) {
            Problem problemTemp = problemService.getById(problem.getUid());
            if (problemTemp != null) {
                aggEsDoc.setOid(problemTemp.getOid());
            }
        }
        asyncService.executeAsyncUpdateEsAndRedis(aggEsDoc, operate);
    }
}
