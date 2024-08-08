package com.moxi.mogublog.commons.annotion.FeignSecurity;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Feign请求安全策略【标识接口只允许内部访问】
 *
 * @author: 陌溪
 * @create: 2022年9月12日11:17:06
 */
@Aspect
@Component
@Slf4j
public class FeignSecurityAspect {

    public final static String ERROR = "error";

    /**
     * @param point
     */
    @Around("@annotation(com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //目标类、方法
        String name = method.getName();
        HttpServletRequest request = RequestHolder.getRequest();
        // 判断请求中，是否携带了特殊的请求头，只有feign调用的时候才会携带
        String header = request.getHeader(BaseSysConf.FEIGN_REQUEST);
        if (!BaseSysConf.FEIGN_REQUEST.equals(header)) {
            String errMessage = "[非法请求] 方法: " + name + ",只允许内部调用";
            log.info(errMessage);
            return ResultUtil.errorWithMessage(errMessage);
        }
        //执行方法
        Object object = point.proceed();
        return object;
    }

}
