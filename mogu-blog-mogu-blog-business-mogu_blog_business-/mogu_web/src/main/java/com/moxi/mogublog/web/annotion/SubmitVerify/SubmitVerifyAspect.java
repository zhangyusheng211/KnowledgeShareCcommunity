package com.moxi.mogublog.web.annotion.SubmitVerify;

import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 登录校验 切面实现
 *
 * @author: 陌溪
 * @create: 2020-03-06-19:05
 */
@Aspect
@Component
@Slf4j
public class SubmitVerifyAspect {

    @Resource
    private SysParamsService sysParamsService;

    @Pointcut(value = "@annotation(submitVerify)")
    public void pointcut(SubmitVerify submitVerify) {

    }

    @Around(value = "pointcut(submitVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, SubmitVerify submitVerify) throws Throwable {
        HttpServletRequest request = RequestHolder.getRequest();
        // 判断该角色是否能够访问该接口
        if (request.getAttribute(SysConf.USER_UID) != null) {
            log.info("用户已登录");
            Object createTime = request.getAttribute(SysConf.CREATE_TIME);
            if (createTime != null) {
                // 拉取到用户的创建时间
                String createTimeStr = request.getAttribute(SysConf.CREATE_TIME).toString();
                Date userCreateTime = DateUtils.strToDateTime(createTimeStr);
                Date now = new Date();
                // 新注册用户限制时间【后台配置】
                String newUserLimitTimeStr = sysParamsService.getSysParamsValueByKey(RedisConf.NEW_USER_LIMIT_TIME);
                Long newUserLimitTime = Long.valueOf(newUserLimitTimeStr) * 3600000;
                if (now.getTime() - userCreateTime.getTime() < newUserLimitTime) {
                    return ResultUtil.errorWithMessage("注册时间小于" + newUserLimitTimeStr + "小时，无法使用该功能");
                }
            }
            //执行业务
            return joinPoint.proceed();
        } else {
            log.info("用户未登录");
            return ResultUtil.errorWithMessage("用户未登录");
        }
    }
}
