package com.moxi.mogublog.wechat.annotion.LoginVerify;

import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.wechat.common.utils.R;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Value(value = "${tokenHead}")
    private String tokenHead;

    @Value(value = "${tokenHeader}")
    private String tokenHeader;

    @Resource
    private RedisUtil redisUtil;

    @Pointcut(value = "@annotation(loginVerify) || @within(loginVerify)")
    public void pointcut(LoginVerify loginVerify) {

    }


    @Around(value = "pointcut(loginVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, LoginVerify loginVerify) throws Throwable {

        //得到请求头信息authorization信息
        HttpServletRequest request = RequestHolder.getRequest();
        String authHeader = request.getHeader(tokenHeader);

        //请求头 'Authorization': tokenHead + token
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            // 获取在线的管理员信息
            String onlineAdmin = redisUtil.get("LOGIN_TOKEN_KEY:" + authHeader);
            if (onlineAdmin != null) {
                //执行业务
                return joinPoint.proceed();
            }
        }
        return R.error("用户未登录");
    }
}
