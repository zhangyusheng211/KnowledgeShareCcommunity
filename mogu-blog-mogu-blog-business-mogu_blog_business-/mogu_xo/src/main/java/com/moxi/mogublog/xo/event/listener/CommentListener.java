package com.moxi.mogublog.xo.event.listener;

import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.event.commentEvent.CommentAddEvent;
import com.moxi.mogublog.xo.event.commentEvent.CommentAuditEvent;
import com.moxi.mogublog.xo.event.commentEvent.CommentDeleteEvent;
import com.moxi.mogublog.xo.event.commentEvent.CommentEvent;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.ECreditType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class CommentListener {

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

    @EventListener(value = {CommentAuditEvent.class})
    public void audit(CommentEvent commentEvent) {
        Comment comment = (Comment) commentEvent.getSource();
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

    @EventListener(value = {CommentAddEvent.class})
    public void add(CommentEvent commentEvent) {
        Comment comment = (Comment) commentEvent.getSource();
        if (StringUtils.isNotEmpty(comment.getUserUid()) && EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
            // 增加用户积分
            userService.addUserCredits(ECreditType.Comment.getAction(), null, comment.getUid(), comment.getUserUid());
        }
    }

    @EventListener(value = {CommentDeleteEvent.class})
    public void delete(CommentEvent commentEvent) {
        Comment comment = (Comment) commentEvent.getSource();
        if (StringUtils.isNotEmpty(comment.getUserUid()) && EAuditStatus.AGREE.equals(comment.getAuditStatus())) {
            // 评论删除，扣除积分
            userService.addUserCredits(ECreditType.Delete_Comment.getAction(), null, comment.getUid(), comment.getUserUid());
        }
    }
}
