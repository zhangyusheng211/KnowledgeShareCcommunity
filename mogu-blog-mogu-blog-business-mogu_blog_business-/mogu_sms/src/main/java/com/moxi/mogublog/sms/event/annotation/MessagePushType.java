package com.moxi.mogublog.sms.event.annotation;

import com.moxi.mougblog.base.enums.EPushMethod;

import java.lang.annotation.*;

/**
 * 事件类型
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessagePushType {
    EPushMethod[] value() default {};
}