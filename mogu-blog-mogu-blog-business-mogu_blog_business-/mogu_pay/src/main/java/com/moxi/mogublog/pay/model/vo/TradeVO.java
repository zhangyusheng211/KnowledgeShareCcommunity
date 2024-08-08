package com.moxi.mogublog.pay.model.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.moxi.mogublog.pay.model.dto.OrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 交易VO
 *
 * @author Snow create in 2018/4/9
 * @version v2.0
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class TradeVO implements Serializable {

    private static final long serialVersionUID = -8580648928412433120L;
    @ApiModelProperty(name = "trade_sn", value = "交易编号")
    private String tradeSn;

    @ApiModelProperty(value = "会员id")
    private Integer memberId;

    @ApiModelProperty(value = "会员昵称")
    private String memberName;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;

    @ApiModelProperty(value = "价格信息")
    private PriceDetailVO priceDetail;

    @ApiModelProperty(value = "优惠券集合")
    private List<CouponVO> couponList;

    @ApiModelProperty(value = "订单集合")
    private List<OrderDTO> orderList;

    @ApiModelProperty(value = "赠品集合")
    private List<GiftVO> giftList;
}
