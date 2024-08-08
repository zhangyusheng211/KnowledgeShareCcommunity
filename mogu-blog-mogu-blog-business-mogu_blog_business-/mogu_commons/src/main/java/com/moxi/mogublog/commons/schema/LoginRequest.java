package com.moxi.mogublog.commons.schema;

import com.moxi.mougblog.base.enums.ELoginType;
import lombok.Data;
import lombok.ToString;

/**
 * 登录相关请求类
 */
@Data
@ToString
public class LoginRequest {

    /**
     * 登录ticketCode
     */
    private String ticketCode;

    /**
     * 资源类型
     */
    private ELoginType loginType;
}
