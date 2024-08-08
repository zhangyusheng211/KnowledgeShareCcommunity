package com.moxi.mogublog.web.annotion.PublishLimitVerify;

import com.moxi.mougblog.base.enums.EPublishLimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户每日发表数校验接口
 *
 * @date 2022年6月29日15:16:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishLimitVerify {

    /**
     * 用户行为
     * @return
     */
    EPublishLimitType behavior();
}
