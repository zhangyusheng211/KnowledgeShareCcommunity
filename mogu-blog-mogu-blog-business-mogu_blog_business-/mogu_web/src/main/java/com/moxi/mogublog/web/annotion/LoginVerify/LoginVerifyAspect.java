package com.moxi.mogublog.web.annotion.LoginVerify;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录校验 切面实现
 *
 * @author: 陌溪
 * @create: 2020-03-06-19:05
 */
@Aspect
@Component
@Slf4j
public class LoginVerifyAspect {

    @Pointcut(value = "@annotation(loginVerify)")
    public void pointcut(LoginVerify loginVerify) {

    }

    @Around(value = "pointcut(loginVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, LoginVerify loginVerify) throws Throwable {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        // 判断该角色是否能够访问该接口
        if (request.getAttribute(SysConf.USER_UID) != null) {
            log.info("用户已登录");
            //执行业务
            return joinPoint.proceed();
        } else {
            log.info("用户未登录");
            return ResultUtil.result(ECode.NO_OPERATION_AUTHORITY, "用户未登录");
        }
    }
}
