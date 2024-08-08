package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 资金流水表
 * @author 陌溪
 * @date 2023年8月5日11:00:30
 */
@Data
@TableName("t_order_amount_log")
public class OrderAmountLog extends SuperEntity<OrderAmountLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 业务类型：66: 资金分账、 67: 用户提现
     */
    private Integer businessType;

    /**
     * 资源UID
     */
    private String resourceUid;

    /**
     * 资源类型：订单、提现单
     */
    private String resourceType;

    /**
     * 用户的金额
     */
    private Long userAmount;

    /**
     * 平台的金额【平台参与分账，收款金额默认到平台】
     */
    private Long platformAmount;

    /**
     * 变更前用户余额
     */
    private Long oldAmount;

    /**
     * 变更后的用户钱包余额
     */
    private Long newAmount;

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

    @TableField(exist = false)
    private PayOrder payOrder;
}
