package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 通用修改表
 * @author 陌溪
 * @date 2022年4月22日21:42:26
 */
@Data
@TableName("t_general_edit")
public class GeneralEdit extends SuperEntity<GeneralEdit> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一oid，自动递增
     */
    private Integer oid;
    /**
     * 用户id
     */
    private String userUid;
    /**
     * 博客/面经类型(0：面经,1: 博客)
     */
    private String bizType;
    /**
     * 博客/面经id
     */
    private String bizUid;
    /**
     * 修改原因
     */
    private String reason;
    /**
     * 题目
     */
    private String summary;
    /**
     * 修改前内容
     */
    private String oldContent;
    /**
     * 修改内容
     */
    private String content;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 管理员uid
     */
    private String adminUid;
    /**
     * 是否通过审核
     */
    private String auditStatus;
    /**
     * 审批拒绝原因
     */
    private String rejectReason;
    /**
     * 审核人
     */
    private String auditName;
    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 投稿人
     */
    @TableField(exist = false)
    private User user;

}
