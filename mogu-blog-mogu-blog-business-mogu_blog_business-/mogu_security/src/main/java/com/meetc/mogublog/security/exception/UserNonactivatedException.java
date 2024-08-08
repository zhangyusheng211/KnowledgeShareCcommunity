package com.meetc.mogublog.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户未激活异常
 * @author 遇见
 */
public class UserNonactivatedException extends AuthenticationException {
    public UserNonactivatedException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserNonactivatedException(String msg) {
        super(msg);
    }
}
