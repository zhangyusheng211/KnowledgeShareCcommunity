package com.moxi.mogublog.pay.annotation;

import com.moxi.mougblog.base.enums.EPayMethod;

import java.lang.annotation.*;

/**
 * 支付方式注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PayMethod {
    EPayMethod[] value() default {};
}