package com.moxi.mogublog.commons.schema;

import com.moxi.mougblog.base.enums.ELoginType;
import lombok.Data;
import lombok.ToString;

/**
 * 绑定相关请求类
 */
@Data
@ToString
public class BindRequest {

    /**
     * 登录ticketCode
     */
    private String ticketCode;

    /**
     * 绑定Code
     */
    private String bindCode;
}
