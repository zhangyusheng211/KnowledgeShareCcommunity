package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.GetOne;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * CommentVO
 *
 * @author: 陌溪
 * @create: 2020年1月11日16:15:52
 */
@ToString
@Data
public class CommentVO extends BaseVO<CommentVO> {

    /**
     * 用户uid
     */
    @NotBlank(groups = {GetOne.class})
    private String userUid;

    /**
     * 回复某条评论的uid
     */
    private String toUid;

    /**
     * 回复某个人的uid
     */
    private String toUserUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评论类型： 0: 评论   1: 点赞
     */
    private Integer type;

    /**
     * 该条评论下的，一级评论UID
     */
    private String firstCommentUid;

    /**
     * 评论内容
     */
    @NotBlank(groups = {Insert.class})
    private String content;

    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 业务uid
     */
    private List<String> businessUidList;

    /**
     * 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等
     */
    @NotBlank(groups = {Insert.class, GetList.class})
    private String source;


    /**
     * 审核状态【0：未审核，1：审核拒绝，2：审核通过】
     */
    private String auditStatus;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
     * 审核人名称
     */
    private String auditName;

    /**
     * 审批拒绝原因
     */
    private String rejectReason;

    /**
     * 是否前台通知 【0 前台 ，1 后台】
     */
    private Integer isBlack;

    /**
     * 开启二级模式
     */
    private boolean openSecondLevel;

    /**
     * 是否置顶
     */
    private Integer isTop;
}
