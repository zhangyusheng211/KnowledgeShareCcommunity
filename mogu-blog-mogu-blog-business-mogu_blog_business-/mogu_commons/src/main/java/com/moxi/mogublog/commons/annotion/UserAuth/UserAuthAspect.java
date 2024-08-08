package com.moxi.mogublog.commons.annotion.UserAuth;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.moxi.mogublog.commons.entity.SysParams;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.AspectUtil;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EAuthCode;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.holder.RequestHolder;
import io.jsonwebtoken.lang.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户权限校验切面实现
 *
 * @author: 陌溪
 * @create: 2023年8月10日08:38:06
 */
@Aspect
@Component
@RefreshScope
@Slf4j
public class UserAuthAspect {

    // 爬虫拦截是否开启
    @Value("${reptile.filter:false}")
    private boolean reptileFilter;

    @Pointcut(value = "@annotation(userAuth)")
    public void pointcut(UserAuth userAuth) {

    }

    @Around(value = "pointcut(userAuth)")
    public Object doAround(ProceedingJoinPoint joinPoint, UserAuth userAuth) throws Throwable {
        HttpServletRequest request = RequestHolder.getRequest();
        // 判断该角色是否能够访问该接口
        if (request.getAttribute(SysConf.USER_UID) != null) {
            Method currentMethod = AspectUtil.INSTANCE.getMethod(joinPoint);
            // 获取操作的枚举
            UserAuth annotation = currentMethod.getAnnotation(UserAuth.class);
            // 判断当前用户是否拥有该权限
            EAuthCode authCode = annotation.value();
            // 判断用户是否拥有下面的权限
            Object authCodeList = request.getAttribute(SysConf.AUTH_CODE_LIST);
            // 暂时兼容处理，一个人不可能没有任何权限
            if (authCodeList != null && !authCodeList.toString().contains(authCode.getCode())) {
                log.error("[UserAuthAspect] 抱歉，您权限不足无法进行操作, authCode: " + authCode.getCode());
                return ResultUtil.errorWithMessage("抱歉，您权限不足无法进行操作");
            }
            List<String> skipAuthList = new ArrayList<>();
            skipAuthList.add(EAuthCode.CREDIT_LUCKY.getCode());
            skipAuthList.add(EAuthCode.UPLOAD_BLOG.getCode());
            if (!skipAuthList.contains(authCode.getCode()) && reptileFilter) {
                String pragmas = request.getHeader("Pragmas");
                if (StringUtils.isEmpty(pragmas)) {
                    log.info("[UserAuthAspect] 未获取到请求头中的爬虫过滤参数信息，命中爬虫逻辑");
                    return ResultUtil.successWithMessage("操作成功");
                }
                long num = Long.parseLong(pragmas);
                long timeLong =  2000000000000L - System.currentTimeMillis();
                long timeValue = timeLong - num;
                log.info("[UserAuthAspect] 命中刷数权限校验，timeLong: {}, num: {}, isHit: {}", timeLong, num, timeValue < -100000 || timeValue > 0);
                if (timeValue < -100000 || timeValue > 100000) {
                    log.info("[UserAuthAspect] 时间戳不一致，命中爬虫逻辑");
                    return ResultUtil.successWithMessage("操作成功");
                }
            }
            //执行业务
            return joinPoint.proceed();
        } else {
            log.error("[UserAuthAspect] 用户未登录");
            return ResultUtil.errorWithMessage( "用户未登录");
        }
    }
}
