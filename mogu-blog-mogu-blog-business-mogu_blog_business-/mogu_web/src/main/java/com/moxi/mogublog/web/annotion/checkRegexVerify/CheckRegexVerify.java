package com.moxi.mogublog.web.annotion.checkRegexVerify;

import com.moxi.mougblog.base.enums.ERegexType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户提交正则校验接口【是否有无效的内容发文章、发动态、发评论，发问题，发问答】
 *
 * @author 陌溪
 * @date 2021年8月8日15:16:09
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRegexVerify {
    /**
     * 用户行为
     * @return
     */
    ERegexType behavior();
}
