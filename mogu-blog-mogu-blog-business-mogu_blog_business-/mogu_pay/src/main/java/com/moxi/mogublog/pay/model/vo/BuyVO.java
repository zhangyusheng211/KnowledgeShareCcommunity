package com.moxi.mogublog.pay.model.vo;

import com.moxi.mougblog.base.enums.EPayMethod;
import com.moxi.mougblog.base.enums.EResourceType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "下单参数")
@Data
public class BuyVO {

    @ApiModelProperty(name = "client", value = "客户端类型PC,WAP,NATIVE,REACT,MINI")
    private String client;

    @ApiModelProperty(name = "resourceUid", value = "资源uid")
    private String resourceUid;

    @ApiModelProperty(name = "resourceType", value = "资源类型")
    private EResourceType resourceType;

    @ApiModelProperty(name = "payMethod", value = "支付类型")
    private EPayMethod payMethod;

}
