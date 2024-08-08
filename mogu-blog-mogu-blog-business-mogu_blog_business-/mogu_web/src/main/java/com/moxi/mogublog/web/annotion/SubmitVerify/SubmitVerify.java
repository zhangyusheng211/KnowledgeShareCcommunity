package com.moxi.mogublog.web.annotion.SubmitVerify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户提交校验接口【是否有权限发文章、发动态、发评论】
 *
 * @author 陌溪
 * @date 2021年8月8日15:16:09
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SubmitVerify {

}