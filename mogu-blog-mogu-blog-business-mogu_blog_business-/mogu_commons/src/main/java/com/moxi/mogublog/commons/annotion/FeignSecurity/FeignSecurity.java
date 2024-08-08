package com.moxi.mogublog.commons.annotion.FeignSecurity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解  Feign请求安全策略【标识接口只允许内部访问，标识该方法只有携带特定请求头才能访问，会在网关层进行擦除】
 *
 * @author Administrator
 * @date 2022年9月12日11:16:58
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeignSecurity {

}
