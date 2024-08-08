package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户动态表
 *
 * @author 陌溪
 * @date 2021年12月25日23:39:47
 */
@Data
@TableName("t_user_moment")
public class UserMoment extends SuperEntity<UserMoment> {

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
     * 动态内容
     */
    private String content;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 文件uids
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fileUids;

    /**
     * 话题uids
     */
    private String topicUids;

    /**
     * url链接
     */
    private String url;

    /**
     * url链接信息
     */
    private String urlInfo;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 点击数
     */
    private Integer commentCount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 是否置顶
     */
    private Integer isTop;

    // 以下字段不存入数据库
    /**
     * 图片列表信息
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 话题列表
     */
    @TableField(exist = false)
    private List<UserMomentTopic> userMomentTopicList;

    /**
     * 评论列表
     */
    @TableField(exist = false)
    private List<Comment> commentList;

    /**
     * 点赞相关信息
     */
    @TableField(exist = false)
    private Map<String, Object> praiseInfo;

    /**
     * 点赞的用户列表
     */
    @TableField(exist = false)
    private List<User> praiseUserList;

    /**
     * 收藏相关信息
     */
    @TableField(exist = false)
    private Map<String, Object> collectInfo;

    /**
     * 积分合计
     */
    @TableField(exist = false)
    private String sumCredits;
}
