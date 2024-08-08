package com.moxi.mogublog.pay.model.dos;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 遇见
 * 会员优惠券实体
 */
@TableName("t_member_coupon")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class MemberCoupon implements Serializable {

    private static final long serialVersionUID = 5545788652245350L;

    /**
     * 主键
     */
    @TableId(value = "mc_id")
    @ApiModelProperty(hidden = true)
    private Integer mcId;

    /**
     * 优惠券表主键
     */
    @TableField(value = "coupon_id")
    @ApiModelProperty(name = "coupon_id", value = "优惠券表主键", required = false)
    private Integer couponId;

    /**
     * 会员表主键
     */
    @TableField(value = "member_id")
    @ApiModelProperty(name = "member_id", value = "会员表主键", required = false)
    private Integer memberId;

    /**
     * 使用时间
     */
    @TableField(value = "used_time")
    @ApiModelProperty(name = "used_time", value = "使用时间", required = false)
    private Long usedTime;

    /**
     * 领取时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(name = "create_time", value = "领取时间", required = false)
    private Long createTime;

    /**
     * 订单表主键
     */
    @TableField(value = "order_id")
    @ApiModelProperty(name = "order_id", value = "订单表主键", required = false)
    private Integer orderId;

    /**
     * 订单编号
     */
    @TableField(value = "order_sn")
    @ApiModelProperty(name = "order_sn", value = "订单编号", required = false)
    private String orderSn;

    /**
     * 会员用户名
     */
    @TableField(value = "member_name")
    @ApiModelProperty(name = "member_name", value = "会员用户名", required = false)
    private String memberName;

    /**
     * 优惠券名称
     */
    @TableField(value = "title")
    @ApiModelProperty(name = "title", value = "优惠券名称", required = false)
    private String title;

    /**
     * 优惠券面额
     */
    @TableField(value = "coupon_price")
    @ApiModelProperty(name = "coupon_price", value = "优惠券面额", required = false)
    private Double couponPrice;

    /**
     * 优惠券门槛金额
     */
    @TableField(value = "coupon_threshold_price")
    @ApiModelProperty(name = "coupon_threshold_price", value = "优惠券门槛金额", required = false)
    private Double couponThresholdPrice;

    /**
     * 有效期--起始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(name = "start_time", value = "有效期--起始时间", required = false)
    private Long startTime;

    /**
     * 有效期--截止时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(name = "end_time", value = "有效期--截止时间", required = false)
    private Long endTime;

    /**
     * 使用状态 0:未使用,1:已使用,2:已过期,3:已作废
     */
    @TableField(value = "used_status")
    @ApiModelProperty(name = "used_status", value = "使用状态", example = "0:未使用,1:已使用,2:已过期,3:已作废")
    private Integer usedStatus;

    /**
     * 使用状态文字（非数据库字段）
     */
    @ApiModelProperty(value = "使用状态文字")
    @TableField(exist = false)
    private String usedStatusText;

    /**
     * 使用范围 ALL:全品,CATEGORY:分类,SOME_GOODS:部分商品
     */
    @TableField(value = "use_scope")
    @ApiModelProperty(name = "use_scope", value = "使用范围 ALL:全品,CATEGORY:分类,SOME_GOODS:部分商品")
    private String useScope;

    /**
     * 范围关联的id
     * 全品或者商家优惠券时为0
     * 分类时为分类id
     * 部分商品时为商品ID集合
     */
    @TableField(value = "scope_id")
    @ApiModelProperty(name = "scope_id", value = "范围关联的id")
    private String scopeId;


    public MemberCoupon() {
    }

    public MemberCoupon(CouponDO couponDO) {
        this.setCouponId(couponDO.getCouponId());
        this.setTitle(couponDO.getTitle());
        this.setStartTime(couponDO.getStartTime());
        this.setEndTime(couponDO.getEndTime());
        this.setCouponPrice(couponDO.getCouponPrice());
        this.setCouponThresholdPrice(couponDO.getCouponThresholdPrice());
        this.setUseScope(couponDO.getUseScope());
        this.setScopeId(couponDO.getScopeId());
    }


    public String getUsedStatusText() {
        if (usedStatus == 0) {
            usedStatusText = "未使用";
        } else if (usedStatus == 2) {
            usedStatusText = "已过期";
        } else {
            usedStatusText = "已使用";
        }

        return usedStatusText;
    }
}
