package com.moxi.mogublog.pay.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 赞助订单VO
 *
 * @author 陌溪
 * @since 2023年5月24日22:07:14
 */
@Data
public class SponsorVO implements Serializable {

    private static final long serialVersionUID = -8580648928412433120L;

    @ApiModelProperty(name = "price", value = "赞助金额")
    private Double price;
}
