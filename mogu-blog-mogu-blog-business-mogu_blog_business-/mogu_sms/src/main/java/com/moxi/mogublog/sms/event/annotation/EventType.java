package com.moxi.mogublog.sms.event.annotation;

import com.moxi.mogublog.commons.domainEvent.EntityType;

import java.lang.annotation.*;

/**
 * 事件类型
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventType {
    EntityType[] value() default {};
}