package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EWebDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 评论事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.COMMENT})
public class CommentEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Comment comment = objectMapper.convertValue(domainEvent.getEntity(), Comment.class);
        switch (domainEvent.getEventAction()) {
            case COMMENT_ADD: {
                this.add(comment, domainEvent.isAdminOperate());
            }
            break;
            case COMMENT_DELETE: {
                this.delete(comment, domainEvent.isAdminOperate());
            }
            break;
            case COMMENT_AUDIT: {
                this.audit(comment, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 审核评论
     *
     * @param comment
     * @param isAdminOperate
     */
    public void audit(Comment comment, boolean isAdminOperate) {
        if (EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
            /**
             * 增加积分
             */
            userService.addUserCredits(ECreditType.Comment.getAction(), null, comment.getUid(), comment.getUserUid());
        }
        /**
         * 发送邮件
         */
        if (StringUtils.isNotEmpty(comment.getUserUid())) {
            User user = userService.getById(comment.getUserUid());
            StringBuilder text = new StringBuilder();
            text.append(String.format("<p>可爱的博主%s,您的评论</p>", user.getNickName()));
            if (EAuditStatus.REJECT.equals(comment.getAuditStatus())) {
                text.append("审核未通过,审核原因为" + comment.getRejectReason());
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else if (EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
                text.append("审核已通过");
                rabbitMqUtil.sendSimpleEmail(user.getEmail(), text.toString());
            } else {
                log.error("审核状态异常,稿件为{}", comment.getUid());
            }
        }
    }

    /**
     * 添加评论
     *
     * @param comment
     * @param isAdminOperate
     */
    public void add(Comment comment, boolean isAdminOperate) {
        if (StringUtils.isNotEmpty(comment.getUserUid()) && EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
            // 增加用户积分
            userService.addUserCredits(ECreditType.Comment.getAction(), null, comment.getUid(), comment.getUserUid());
        }

        // 执行任务
        userTaskService.action(Action.COMMENT, null, null);

        // 回复用户的评论，需要判断被回复方的任务是否完成
        if (StringUtils.isNotEmpty(comment.getToUserUid())) {
            userTaskService.action(Action.TO_COMMENT, null, null, comment.getToUserUid());
        }

        // 判断是否是评论的机器人【若机器人会触发主动回复】
        String atUserUid = StringUtils.getAtUserUid(comment.getContent());
        // 判断是否触发AT功能，触发AT后的动作
        if (StringUtils.isNotEmpty(atUserUid)) {
            atEventHandler.atEventHandler(comment.getUserUid(), atUserUid, comment.getUid(), comment.getBlogUid(), comment.getSource(), comment.getContent(), true);
        } else if (StringUtils.isNotEmpty(comment.getToUserUid())) {
            atEventHandler.atEventHandler(comment.getUserUid(), comment.getToUserUid(), comment.getUid(), comment.getBlogUid(), comment.getSource(), comment.getContent(), false);
        }

        // 发送评论对应的通知
        noticeService.sendNoticeByComment(comment);
    }

    /**
     * 添加评论
     *
     * @param comment
     * @param isAdminOperate
     */
    public void delete(Comment comment, boolean isAdminOperate) {
        if (StringUtils.isNotEmpty(comment.getUserUid()) && EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
            // 评论删除，扣除积分
            userService.addUserCredits(ECreditType.Delete_Comment.getAction(), null, comment.getUid(), comment.getUserUid());
        }
    }
}
