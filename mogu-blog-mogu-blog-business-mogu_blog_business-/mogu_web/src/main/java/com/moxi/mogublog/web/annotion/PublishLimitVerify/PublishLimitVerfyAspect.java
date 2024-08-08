package com.moxi.mogublog.web.annotion.PublishLimitVerify;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EPublishLimitType;
import com.moxi.mougblog.base.enums.EUserLevel;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户每日发表数校验 切面实现
 *
 * @create: 2022-06-29-19:05
 */
@Aspect
@Component
@Slf4j
public class PublishLimitVerfyAspect {

    private DateUtils dateUtils;
    /**
     * 缓存工具
     */
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 系统参数服务
     */
    @Resource
    private SysParamsService sysParamsService;
    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    @Pointcut(value = "@annotation(publishLimitVerify)")
    public void pointcut(PublishLimitVerify publishLimitVerify) {

    }

    @Around(value = "pointcut(publishLimitVerify)")
    public Object doAround(ProceedingJoinPoint joinPoint, PublishLimitVerify publishLimitVerify) throws Throwable {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        Double credits = (Double)request.getAttribute(SysConf.CREDITS);
        Double userTag = (Double)request.getAttribute(SysConf.USER_TAG);

        Method currentMethod = AspectUtil.INSTANCE.getMethod(joinPoint);
        //获取操作名称
        PublishLimitVerify annotation = currentMethod.getAnnotation(PublishLimitVerify.class);
        EPublishLimitType behavior = annotation.behavior();

        // 获取用户发布等级限制信息
        String jsonResult = sysParamsService.getSysParamsValueByKey(SysConf.LEVEL_LIMIT_PUBLISH);
        Map<String, Map<String, String>> jsonToMapLevel = (Map<String, Map<String, String>>) JsonUtils.jsonToMap(jsonResult, Map.class);

        User user = new User();
        user.setUid(userUid.toString());
        int creditsValue = 0;
        int userTagValue = 0;
        int expValue = 0; // 经验值
        if (credits != null) {
            creditsValue = credits.intValue();
        }
        if (request.getAttribute(SysConf.EXP_VALUE) != null) {
            Double expValueDouble = (Double) request.getAttribute(SysConf.EXP_VALUE);
            expValue = expValueDouble.intValue();
        }
        if (userTag != null) {
            userTagValue = userTag.intValue();
        }
        user.setExpValue(expValue);
        user.setCredits(creditsValue);
        user.setUserTag(userTagValue);
        // 特权用户无发布限制
        if (user.getUserTag() > Constants.NUM_ZERO) {
            //执行业务
            return joinPoint.proceed();
        }
        // 根据用户经验值，设置用户的等级
        user.setUserLevel(EUserLevel.getLvByExpValue(user.getExpValue()).getLevel());
        String publishLimit = "";
        String publiscCount = "";
        // 根据用户等级查询出用户 每日发布文章数限制
        switch (behavior) {
            case BLOG_COUNT:
                publishLimit = jsonToMapLevel.get(com.moxi.mogublog.xo.global.SysConf.PREFIX_LV + user.getUserLevel()).get(com.moxi.mogublog.xo.global.SysConf.BLOG_COUNT);
                publiscCount = redisUtil.get(RedisConf.BLOG_LIMIT + RedisConf.SEGMENTATION + user.getUid());
                if (StringUtils.isNotEmpty(publiscCount)){
                    if (Integer.parseInt(publiscCount) >= Integer.parseInt(publishLimit)){
                        return ResultUtil.errorWithMessage("您已经达到当前等级的文章发布限额，请明天再试~");
                    }
                    redisUtil.incrBy(RedisConf.BLOG_LIMIT + RedisConf.SEGMENTATION + user.getUid(), 1);
                }else {
                    redisUtil.setEx(RedisConf.BLOG_LIMIT + RedisConf.SEGMENTATION + user.getUid(), String.valueOf(1), DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
                }
            break;
            case MOMENT_COUNT:
                publishLimit = jsonToMapLevel.get(com.moxi.mogublog.xo.global.SysConf.PREFIX_LV + user.getUserLevel()).get(com.moxi.mogublog.xo.global.SysConf.MOMENT_COUNT);
                publiscCount = redisUtil.get(RedisConf.MOMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid());
                if (StringUtils.isNotEmpty(publiscCount)){
                    if (Integer.parseInt(publiscCount) >= Integer.parseInt(publishLimit)){
                        return ResultUtil.errorWithMessage("您已经达到当前等级的动态发布限额，请明天再试~");
                    }
                    redisUtil.incrBy(RedisConf.MOMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid(), 1);
                }else {
                    redisUtil.setEx(RedisConf.MOMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid(), String.valueOf(1), DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
                }
            break;
            case QUESTION_COUNT:
                publishLimit = jsonToMapLevel.get(com.moxi.mogublog.xo.global.SysConf.PREFIX_LV + user.getUserLevel()).get(com.moxi.mogublog.xo.global.SysConf.QUESTION_COUNT);
                publiscCount = redisUtil.get(RedisConf.QUESTION_LIMIT + RedisConf.SEGMENTATION + user.getUid());
                if (StringUtils.isNotEmpty(publiscCount)){
                    if (Integer.parseInt(publiscCount) >= Integer.parseInt(publishLimit)){
                        return ResultUtil.errorWithMessage("您已经达到当前等级的问答发布限额，请明天再试~");
                    }
                    redisUtil.incrBy(RedisConf.QUESTION_LIMIT + RedisConf.SEGMENTATION + user.getUid(), 1);
                }else {
                    redisUtil.setEx(RedisConf.QUESTION_LIMIT + RedisConf.SEGMENTATION + user.getUid(), String.valueOf(1), DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
                }
            break;
            case PROBLEM_COUNT:
                publishLimit = jsonToMapLevel.get(com.moxi.mogublog.xo.global.SysConf.PREFIX_LV + user.getUserLevel()).get(com.moxi.mogublog.xo.global.SysConf.PROBLEM_COUNT);
                publiscCount = redisUtil.get(RedisConf.PROBLEM_LIMIT + RedisConf.SEGMENTATION + user.getUid());
                if (StringUtils.isNotEmpty(publiscCount)){
                    if (Integer.parseInt(publiscCount) >= Integer.parseInt(publishLimit)){
                        return ResultUtil.errorWithMessage("您已经达到当前等级的面经发布限额，请明天再试~");
                    }
                    redisUtil.incrBy(RedisConf.PROBLEM_LIMIT + RedisConf.SEGMENTATION + user.getUid(), 1);
                }else {
                    redisUtil.setEx(RedisConf.PROBLEM_LIMIT + RedisConf.SEGMENTATION + user.getUid(), String.valueOf(1), DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
                }
            break;
            case COMMENT_COUNT:
                publishLimit = jsonToMapLevel.get(com.moxi.mogublog.xo.global.SysConf.PREFIX_LV + user.getUserLevel()).get(com.moxi.mogublog.xo.global.SysConf.COMMENT_COUNT);
                publiscCount = redisUtil.get(RedisConf.COMMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid());
                if (StringUtils.isNotEmpty(publiscCount)){
                    if (Integer.parseInt(publiscCount) >= Integer.parseInt(publishLimit)){
                        return ResultUtil.errorWithMessage("您已经达到当前等级的评论发布限额，请明天再试~");
                    }
                    redisUtil.incrBy(RedisConf.COMMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid(), 1);
                }else {
                    redisUtil.setEx(RedisConf.COMMENT_LIMIT + RedisConf.SEGMENTATION + user.getUid(), String.valueOf(1), DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
                }
            break;
        }

        //执行业务
        return joinPoint.proceed();

    }
}
