package com.moxi.mogublog.picture.annotation;

import com.moxi.mogublog.picture.enums.UploadTypeEnum;

import java.lang.annotation.*;

/**
 * 上传类型
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UploadType {
    UploadTypeEnum[] value() default {};
}