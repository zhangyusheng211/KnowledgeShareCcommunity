package com.moxi.mogublog.pay.model.dos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.moxi.mougblog.base.entity.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 订单表实体
 *
 * @author 遇见
 */
@TableName(value = "t_order")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class Order extends SuperEntity<Order> implements Serializable {

    private static final long serialVersionUID = 3154292647603519L;

    /**
     * 交易编号
     */
    @TableField(value = "trade_sn")
    @ApiModelProperty(name = "trade_sn", value = "交易编号", required = false)
    private String tradeSn;
    /**
     * 订单编号
     */
    @TableField(value = "order_sn")
    @ApiModelProperty(name = "order_sn", value = "订单编号", required = false)
    private String orderSn;
    /**
     * 会员ID
     */
    @TableField(value = "member_id")
    @ApiModelProperty(name = "member_id", value = "会员ID", required = false)
    private String memberId;
    /**
     * 买家账号
     */
    @TableField(value = "member_name")
    @ApiModelProperty(name = "member_name", value = "买家账号", required = false)
    private String memberName;
    /**
     * 订单状态
     */
    @TableField(value = "order_status")
    @ApiModelProperty(name = "order_status", value = "订单状态", required = false)
    private String orderStatus;
    /**
     * 付款状态
     */
    @TableField(value = "pay_status")
    @ApiModelProperty(name = "pay_status", value = "付款状态", required = false)
    private String payStatus;

    /**
     * 支付方式id
     */
    @TableField(value = "payment_method_id")
    @ApiModelProperty(name = "payment_method_id", value = "支付方式id", required = false)
    private String paymentMethodId;
    /**
     * 支付插件id
     */
    @TableField(value = "payment_plugin_id")
    @ApiModelProperty(name = "payment_plugin_id", value = "支付插件id", required = false)
    private String paymentPluginId;
    /**
     * 支付方式名称
     */
    @TableField(value = "payment_method_name")
    @ApiModelProperty(name = "payment_method_name", value = "支付方式名称", required = false)
    private String paymentMethodName;
    /**
     * 支付方式类型
     */
    @TableField(value = "payment_type")
    @ApiModelProperty(name = "payment_type", value = "支付方式类型", required = false)
    private String paymentType;
    /**
     * 支付时间
     */
    @TableField(value = "payment_time")
    @ApiModelProperty(name = "payment_time", value = "支付时间", required = false)
    private Long paymentTime;
    /**
     * 已支付金额
     */
    @TableField(value = "pay_money")
    @ApiModelProperty(name = "pay_money", value = "已支付金额", required = false)
    private Double payMoney;

    /**
     * 订单总额
     */
    @TableField(value = "order_price")
    @ApiModelProperty(name = "order_price", value = "订单总额", required = false)
    private Double orderPrice;
    /**
     * 商品总额
     */
    @TableField(value = "goods_price")
    @ApiModelProperty(name = "goods_price", value = "商品总额", required = false)
    private Double goodsPrice;

    /**
     * 是否被删除
     */
    @TableField(value = "disabled")
    @ApiModelProperty(name = "disabled", value = "是否被删除", required = false)
    private Integer disabled;
    /**
     * 订单商品总重量
     */
    @TableField(value = "weight")
    @ApiModelProperty(name = "weight", value = "订单商品总重量", required = false)
    private Double weight;
    /**
     * 商品数量
     */
    @TableField(value = "goods_num")
    @ApiModelProperty(name = "goods_num", value = "商品数量", required = false)
    private Integer goodsNum;
    /**
     * 订单备注
     */
    @TableField(value = "remark")
    @ApiModelProperty(name = "remark", value = "订单备注", required = false)
    private String remark;
    /**
     * 订单取消原因
     */
    @TableField(value = "cancel_reason")
    @ApiModelProperty(name = "cancel_reason", value = "订单取消原因", required = false)
    private String cancelReason;

    /**
     * 转换 List<OrderSkuVO>
     */
    @TableField(value = "items_json")
    @ApiModelProperty(name = "items_json", value = "货物列表json", required = false)
    private String itemsJson;

    @TableField(value = "need_pay_money")
    @ApiModelProperty(name = "need_pay_money", value = "应付金额", required = false)
    private Double needPayMoney;

    /**
     * 管理员备注
     */
    @TableField(value = "admin_remark")
    @ApiModelProperty(name = "admin_remark", value = "管理员备注", required = false)
    private Integer adminRemark;
    /**
     * 完成时间
     */
    @TableField(value = "complete_time")
    @ApiModelProperty(name = "complete_time", value = "完成时间", required = false)
    private Long completeTime;

    /**
     * 支付方式返回的交易号
     */
    @TableField(value = "pay_order_no")
    @ApiModelProperty(name = "pay_order_no", value = "支付方式返回的交易号", required = false)
    private String payOrderNo;

    /**
     * 售后状态
     */
    @TableField(value = "service_status")
    @ApiModelProperty(name = "service_status", value = "售后状态", required = false)
    private String serviceStatus;

    /**
     * 订单来源
     */
    @TableField(value = "client_type")
    @ApiModelProperty(name = "client_type", value = "订单来源", required = false)
    private String clientType;

    @TableField(value = "need_receipt")
    @ApiModelProperty(name = "need_receipt", value = "是否需要发票,0：否，1：是")
    private Integer needReceipt;

}
