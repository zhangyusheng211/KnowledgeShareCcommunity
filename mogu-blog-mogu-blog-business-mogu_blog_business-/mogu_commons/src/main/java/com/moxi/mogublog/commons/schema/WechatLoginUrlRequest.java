package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * YunGouOS获取登录url
 */
@Data
public class WechatLoginUrlRequest {
    /**
     * 登录的Key
     */
    private String loginKey;

    /**
     * 来源：PC或者H5
     */
    private String source;

}
