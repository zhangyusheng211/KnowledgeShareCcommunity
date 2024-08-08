package com.moxi.mogublog.pay.model.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.moxi.mogublog.pay.model.dos.MemberCoupon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券
 *
 * @author 遇见
 */
@ApiModel(description = "优惠券")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class CouponVO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5236361119763282449L;

    /**
     * 记录下单时使用的会员优惠券ID
     */
    @ApiModelProperty(value = "会员优惠券id")
    private Integer memberCouponId;

    /**
     * 记录下单时赠送的优惠券ID
     */
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;

    @ApiModelProperty(value = "卖家id")
    private Integer sellerId;

    @ApiModelProperty(value = "卖家名称")
    private String sellerName;

    @ApiModelProperty(value = "金额")
    private Double amount;

    @ApiModelProperty(value = "有效期")
    private Long endTime;

    @ApiModelProperty(value = "使用条件")
    private String useTerm;


    @ApiModelProperty(value = "优惠券门槛金额")
    private Double couponThresholdPrice;

    @ApiModelProperty(value = "是否可用，1为可用，0为不可用")
    private int enable;

    @ApiModelProperty(value = "是否被选中，1为选中，0为不选中")
    private int selected;

    @ApiModelProperty(name = "error_msg", value = "错误信息，结算页使用")
    private String errorMsg;

    @ApiModelProperty(name = "use_scope", value = "优惠券使用范围")
    private String useScope;

    @ApiModelProperty(name = "enable_sku_list", value = "允许使用的ids")
    private List<Integer> enableSkuList;


    public CouponVO() {
    }

    public CouponVO(MemberCoupon memberCoupon) {

        this.setCouponId(memberCoupon.getCouponId());
        this.setAmount(memberCoupon.getCouponPrice());
        this.setUseTerm("满" + memberCoupon.getCouponThresholdPrice() + "可用");
        this.setMemberCouponId(memberCoupon.getMcId());
        this.setEndTime(memberCoupon.getEndTime());
        this.setCouponThresholdPrice(memberCoupon.getCouponThresholdPrice());
        this.setUseScope(memberCoupon.getUseScope());

    }


}
