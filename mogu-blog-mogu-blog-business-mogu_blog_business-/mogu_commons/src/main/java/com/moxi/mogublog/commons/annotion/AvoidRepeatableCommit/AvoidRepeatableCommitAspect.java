package com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit;

import com.moxi.mogublog.utils.*;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 避免接口重复提交AOP
 *
 * @author: 陌溪
 * @create: 2020-04-23-12:12
 */
@Aspect
@Component
@Slf4j
public class AvoidRepeatableCommitAspect {

    public final static String AVOID_REPEATABLE_COMMIT = "AVOID_REPEATABLE_COMMIT";
    public final static String ERROR = "error";

    @Autowired
    private RedisUtil redisUtil;

    /**
     * @param point
     */
    @Around("@annotation(com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        HttpServletRequest request = RequestHolder.getRequest();

        String ip = IpUtils.getIpAddr(request);

        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        //目标类、方法
        String className = method.getDeclaringClass().getName();

        String name = method.getName();
        // 默认根据类名作为Key
        String ipKey = String.format("%s#%s", className, name);

        // 通过map封装参数和参数值
        String[] parameterNames = signature.getParameterNames();
        HashMap<String, Object> paramMap = new HashMap();
        // 参数值
        Object[] args = point.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }
        if (paramMap.size() > 0) {
            // 当有参数时，使用参数作为Key
            ipKey = MD5Utils.string2MD5(paramMap.toString());
        }

        // 转换成HashCode
        int hashCode = Math.abs(ipKey.hashCode());

        String key = String.format("%s:%s_%d", AVOID_REPEATABLE_COMMIT, ip, hashCode);

        log.info("ipKey={},hashCode={},key={}", ipKey, hashCode, key);

        AvoidRepeatableCommit avoidRepeatableCommit = method.getAnnotation(AvoidRepeatableCommit.class);

        long timeout = avoidRepeatableCommit.timeout();

        String value = redisUtil.get(key);

        if (StringUtils.isNotBlank(value)) {
            log.info("请勿重复提交表单");
            return ResultUtil.errorWithMessage("请勿重复提交，请刷新后页面后重试");
        }

        // 设置过期时间
        redisUtil.setEx(key, StringUtils.getUUID(), timeout, TimeUnit.MILLISECONDS);

        //执行方法
        Object object = point.proceed();
        return object;
    }

}
