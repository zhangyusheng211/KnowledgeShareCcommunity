package com.moxi.mogublog.sms.languageModel.annotation;

import com.moxi.mougblog.base.enums.ELanguageModelType;
import com.moxi.mougblog.base.enums.EPayMethod;

import java.lang.annotation.*;

/**
 * 语言模型类型注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LanguageModelType {
    ELanguageModelType[] value() default {};
}