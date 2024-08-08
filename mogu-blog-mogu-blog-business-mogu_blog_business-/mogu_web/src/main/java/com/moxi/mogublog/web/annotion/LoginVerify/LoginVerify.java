package com.moxi.mogublog.web.annotion.LoginVerify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录校验接口
 *
 * @author 陌溪
 * @date 2021年8月8日15:16:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginVerify {
    String value() default "";
}