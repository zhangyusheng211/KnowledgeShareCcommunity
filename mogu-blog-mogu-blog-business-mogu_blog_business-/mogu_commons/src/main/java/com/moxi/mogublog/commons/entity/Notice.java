package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.enums.ENoticeType;
import lombok.Data;

import java.util.List;

/**
 * 通知表
 *
 * @author 陌溪
 * @date 2021年8月5日23:19:55
 */
@Data
@TableName("t_notice")
public class Notice extends SuperEntity<Notice> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 管理员
     */
    private String adminUid;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private Integer noticeType;

    /**
     * 通知状态  0:已创建，1:已查看'
     */
    private Integer noticeStatus;

    /**
     * 业务uid
     */
    private String businessUid;

    /**
     * 业务类型 【博客，问答，评论】
     */
    private Integer businessType;

    /**
     * 是否前台通知 【0 前台 ，1 后台】
     */
    private Integer isBlack;
    /**
     * 评论
     */
    @TableField(exist = false)
    private Comment comment;

    /**
     * 关注
     */
    @TableField(exist = false)
    private UserWatch userWatch;

    /**
     * 博客
     */
    @TableField(exist = false)
    private Blog blog;

    /**
     * 问答
     */
    @TableField(exist = false)
    private Question question;

    /**
     * 问题
     */
    @TableField(exist = false)
    private Problem problem;

    /**
     * 动态
     */
    @TableField(exist = false)
    private UserMoment userMoment;

    /**
     * 谁给你发的通知
     */
    @TableField(exist = false)
    private User fromUser;

    /**
     *业务类型名称 【博客，问答，评论】
     */
    @TableField(exist = false)
    private String businessTypeName;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;
}
