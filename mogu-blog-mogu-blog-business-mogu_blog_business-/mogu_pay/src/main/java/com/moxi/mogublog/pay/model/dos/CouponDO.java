package com.moxi.mogublog.pay.model.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 优惠券实体
 *
 * @author 遇见
 */
@TableName(value = "t_coupon")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class CouponDO implements Serializable {

    private static final long serialVersionUID = 8587456467004980L;

    /**
     * 主键
     */
    @TableId(value = "coupon_id")
    @ApiModelProperty(hidden = true)
    private Integer couponId;

    /**
     * 优惠券名称
     */
    @TableField(value = "title")
    @ApiModelProperty(name = "title", value = "优惠券名称", required = true)
    @NotEmpty(message = "请填写优惠券名称")
    private String title;

    /**
     * 优惠券面额
     */
    @TableField(value = "coupon_price")
    @ApiModelProperty(name = "coupon_price", value = "优惠券面额", required = false)
    @NotNull(message = "请填写优惠券面额")
    @Max(value = 99999999, message = "优惠券面额不能超过99999999")
    private Double couponPrice;

    /**
     * 优惠券门槛价格
     */
    @TableField(value = "coupon_threshold_price")
    @ApiModelProperty(name = "coupon_threshold_price", value = "优惠券门槛价格", required = false)
    @NotNull(message = "请填写优惠券门槛价格")
    @Max(value = 99999999, message = "优惠券门槛价格不能超过99999999")
    private Double couponThresholdPrice;

    /**
     * 使用起始时间
     */
    @TableField(value = "start_time")
    @ApiModelProperty(name = "start_time", value = "使用起始时间", required = false)
    @NotNull(message = "请填写起始时间")
    private Long startTime;

    /**
     * 使用截止时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(name = "end_time", value = "使用截止时间", required = false)
    @NotNull(message = "请填写截止时间")
    private Long endTime;

    /**
     * 发行量
     */
    @TableField(value = "create_num")
    @ApiModelProperty(name = "create_num", value = "发行量", required = false)
    @NotNull(message = "请正确填写发行量")
    private Integer createNum;

    /**
     * 每人限领数量
     */
    @TableField(value = "limit_num")
    @ApiModelProperty(name = "limit_num", value = "每人限领数量", required = false)
    @NotNull(message = "请正确填写每人限领数量")
    private Integer limitNum;

    /**
     * 已被使用的数量
     */
    @TableField(value = "used_num")
    @ApiModelProperty(name = "used_num", value = "已被使用的数量", required = false)
    private Integer usedNum;

    /**
     * 已被领取的数量
     */
    @TableField(value = "received_num")
    @ApiModelProperty(name = "received_num", value = "已被领取的数量", required = false)
    private Integer receivedNum;

    /**
     * 优惠券类型，分为免费领取和活动赠送
     */
    @TableField(value = "type")
    @ApiModelProperty(name = "type", value = "优惠券类型，分为免费领取和活动赠送")
    private String type;

    /**
     * 使用范围，全品，分类，部分商品
     */
    @TableField(value = "use_scope")
    @ApiModelProperty(name = "use_scope", value = "使用范围，全品，分类，部分商品")
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


    /**
     * 范围描述
     */
    @TableField(value = "scope_description")
    @ApiModelProperty(name = "scope_description", value = "范围描述")
    private String scopeDescription;

    /**
     * 活动说明
     */
    @TableField(value = "activity_description")
    @ApiModelProperty(name = "activity_description", value = "活动说明")
    private String activityDescription;

}
