package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户提现表
 * @author 陌溪
 * @since 2023年8月2日19:56:42
 */
@Data
@TableName("t_withdraw")
public class Withdraw extends SuperEntity<Withdraw> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 提现账号
     */
    private String account;

    /**
     * 提现收款码文件UID【微信收款码、支付宝收款码】
     */
    private String fileUid;

    /**
     * 提现的金额
     */
    private Long amount;

    /**
     * 提现状态 0：初始状态，1：提现中，2：提现失败，3：提现完成
     */
    private Integer withdrawStatus;

    /**
     * 审核人
     */
    private String adminUid;

    /**
     * 审核状态
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
     * 备注
     */
    private String remark;

    // 以下字段不存入数据库
    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 审核的管理员
     */
    @TableField(exist = false)
    private Admin admin;

    /**
     * 收款码
     */
    @TableField(exist = false)
    private String photoUrl;
}
