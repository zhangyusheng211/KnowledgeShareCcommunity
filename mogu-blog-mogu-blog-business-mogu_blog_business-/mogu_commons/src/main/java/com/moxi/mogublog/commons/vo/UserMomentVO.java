package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.validator.annotion.Range;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户动态VO
 *
 * @author 陌溪
 * @date 2021年12月25日23:49:47
 */
@Data
public class UserMomentVO extends BaseVO<UserVO> {

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
    @Range(max = 1024)
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
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;
    /**
     * 依据排序
     */
    private String orderBy;

    /**
     * 最热
     */
    private Boolean isHot;
    /**
     * 最新
     */
    private Boolean isNew;
    /**
     * 是否后台传入
     */
    private Boolean isBlack;

    /**
     * 是否置顶
     */
    private Integer isTop;
}
