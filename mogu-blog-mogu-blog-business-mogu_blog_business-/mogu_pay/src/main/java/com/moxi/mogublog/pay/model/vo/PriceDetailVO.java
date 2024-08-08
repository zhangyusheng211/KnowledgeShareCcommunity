package com.moxi.mogublog.pay.model.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.moxi.mogublog.utils.CurrencyUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单价格信息
 *
 * @author 遇见
 */
@ApiModel(value = "PriceDetailVO", description = "价格明细")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class PriceDetailVO implements Serializable {


    private static final long serialVersionUID = -960537582096338500L;

    @ApiModelProperty(value = "总价")
    private Double totalPrice;


    /**
     * 用来判断满减、优惠券等优惠的基数
     */
    @ApiModelProperty(value = "商品原价，没有优惠过的")
    private Double originalPrice;

    @ApiModelProperty(value = "商品价格，优惠后的")
    private Double goodsPrice;


    @ApiModelProperty(value = "配送费")
    private Double freightPrice;

    /**
     * 此金额 = 各种优惠工具的优惠金额 + 优惠券优惠的金额
     */
    @ApiModelProperty(value = "优惠金额")
    private Double discountPrice;

    @ApiModelProperty(value = "返现金额，不含优惠券")
    private Double cashBack;

    @ApiModelProperty(value = "优惠券抵扣金额")
    private Double couponPrice;

    @ApiModelProperty(value = "满减金额")
    private Double fullMinus;

    /**
     * 1为免运费
     */
    @ApiModelProperty(value = "是否免运费,1为免运费")
    private Integer isFreeFreight;

    @ApiModelProperty(value = "使用的积分")
    private Long exchangePoint;

    /**
     * 构造器，初始化默认值
     */
    public PriceDetailVO() {
        this.goodsPrice = 0.0;
        this.freightPrice = 0.0;
        this.totalPrice = 0.0;
        this.discountPrice = 0.0;
        this.exchangePoint = 0L;
        this.isFreeFreight = 0;
        this.couponPrice = 0D;
        this.cashBack = 0D;
        this.originalPrice = 0D;
        fullMinus = 0d;
    }


    /**
     * 清空功能
     */
    public void clear() {

        this.goodsPrice = 0.0;
        this.freightPrice = 0.0;
        this.totalPrice = 0.0;
        this.discountPrice = 0.0;
        this.exchangePoint = 0L;
        this.isFreeFreight = 0;
        this.cashBack = 0D;
        this.originalPrice = 0D;
        this.couponPrice = 0D;
        fullMinus = 0d;
    }

    /**
     * 价格累加运算
     *
     * @param price
     * @return
     */
    public PriceDetailVO plus(PriceDetailVO price) {

        double total = CurrencyUtil.add(totalPrice, price.getTotalPrice());
        double original = CurrencyUtil.add(originalPrice, price.getOriginalPrice());
        double goods = CurrencyUtil.add(goodsPrice, price.getGoodsPrice());
        double freight = CurrencyUtil.add(this.freightPrice, price.getFreightPrice());
        double discount = CurrencyUtil.add(this.discountPrice, price.getDiscountPrice());
        double couponPrice = CurrencyUtil.add(this.couponPrice, price.getCouponPrice());
        double cashBack = CurrencyUtil.add(this.cashBack, price.getCashBack());
        double fullMinus = CurrencyUtil.add(this.cashBack, price.getFullMinus());
        Long point = this.exchangePoint + price.getExchangePoint();

        PriceDetailVO newPrice = new PriceDetailVO();
        newPrice.setTotalPrice(total);
        newPrice.setGoodsPrice(goods);
        newPrice.setFreightPrice(freight);
        newPrice.setDiscountPrice(discount);
        newPrice.setExchangePoint(point);
        newPrice.setCouponPrice(couponPrice);
        newPrice.setCashBack(cashBack);
        newPrice.setIsFreeFreight(price.getIsFreeFreight());
        newPrice.setOriginalPrice(original);
        newPrice.setFullMinus(fullMinus);
        return newPrice;
    }


    /**
     * 当前店铺总价计算
     */
    public void countPrice() {
        //购物车内当前商家的商品原价总计
        Double goodsPrice = this.getGoodsPrice();

        //购物车内当前商家的配送金额总计
        Double freightPrice = this.getFreightPrice();

        //购物车内当前商家的应付金额总计
        //运算过程=商品原价总计-返现金额+配送费用
        Double totalPrice = CurrencyUtil.add(CurrencyUtil.sub(goodsPrice, this.getCashBack()), freightPrice);

        //运算过程=商品原价总计-优惠券金额
        totalPrice = CurrencyUtil.sub(totalPrice, this.getCouponPrice());

        //防止金额为负数
        if (totalPrice.doubleValue() <= 0) {
            totalPrice = 0d;
        }
        this.setTotalPrice(totalPrice);

    }

    /**
     * 重新计算优惠金额
     */
    public void reCountDiscountPrice() {
        discountPrice = CurrencyUtil.add(cashBack, this.getCouponPrice());
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Double getFreightPrice() {
        return freightPrice;
    }

    public void setFreightPrice(Double freightPrice) {
        this.freightPrice = freightPrice;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getIsFreeFreight() {
        return isFreeFreight;
    }

    public void setIsFreeFreight(Integer isFreeFreight) {
        this.isFreeFreight = isFreeFreight;
    }

    public Long getExchangePoint() {
        return exchangePoint;
    }

    public void setExchangePoint(Long exchangePoint) {
        this.exchangePoint = exchangePoint;
    }


    public Double getFullMinus() {
        return fullMinus;
    }

    public void setFullMinus(Double fullMinus) {
        this.fullMinus = fullMinus;
    }

    public Double getCashBack() {

        return cashBack;
    }

    public void setCashBack(Double cashBack) {
        this.cashBack = cashBack;
    }

    public Double getCouponPrice() {
        if (couponPrice == null) {
            return 0D;
        }
        return couponPrice;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }


}
