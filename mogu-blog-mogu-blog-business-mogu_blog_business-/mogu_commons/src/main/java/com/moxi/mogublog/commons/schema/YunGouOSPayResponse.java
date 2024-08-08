package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * YunGouOS支付回调
 */
@Data
public class YunGouOSPayResponse {
    /**
     * 消息
     */
    private String msg;

    private String data;

    private int code;
}
