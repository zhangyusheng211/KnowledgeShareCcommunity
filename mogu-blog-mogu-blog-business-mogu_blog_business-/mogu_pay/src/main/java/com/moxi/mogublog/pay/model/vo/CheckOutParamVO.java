package com.moxi.mogublog.pay.model.vo;

import com.moxi.mogublog.pay.model.enums.PaymentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 结算参数VO
 *
 * @author 遇见
 */
@ApiModel(description = "结算参数")
@Data
public class CheckOutParamVO {

    @ApiModelProperty(name = "payment_type", value = "支付方式")
    private PaymentType paymentType;

    @ApiModelProperty(name = "receive_time", value = "收货时间")
    private String receiveTime;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(name = "client_type", value = "客户端类型")
    private String clientType;

}
