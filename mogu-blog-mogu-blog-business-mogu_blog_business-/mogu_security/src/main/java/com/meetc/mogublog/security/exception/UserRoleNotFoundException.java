package com.meetc.mogublog.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 未查询到用户角色异常
 * @author 遇见
 */
public class UserRoleNotFoundException extends AuthenticationException {
    public UserRoleNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserRoleNotFoundException(String msg) {
        super(msg);
    }
}
