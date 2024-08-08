package com.moxi.mogublog.web.annotion.checkRegexVerify;

import com.moxi.mogublog.utils.AspectUtil;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
public class RegexVerifyAspect {

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

    @Pointcut(value = "@annotation(checkRegexVerify)")
    public void pointcut(CheckRegexVerify checkRegexVerify) {

    }

    @Around(value = "pointcut(checkRegexVerify)")
    public Object doAround (ProceedingJoinPoint point, CheckRegexVerify checkRegexVerify) throws Throwable {
        // 获取方法参数
        Object[] args = point.getArgs();
        if (args.length <= 0){
            return ResultUtil.errorWithMessage(MessageConf.REGULAR_PARAM_NULL);
        }

        //获取方法名称
        Method currentMethod = AspectUtil.INSTANCE.getMethod(point);
        // 获取注解
        CheckRegexVerify annotation = currentMethod.getAnnotation(CheckRegexVerify.class);
        Boolean result = false;
        // 获取用户行为
        switch (annotation.behavior()) {
            case ADD_COMMENT :
                CommentVO commentVO = (CommentVO)args[0];
                result = checkRegex(commentVO.getContent());
                break;
            case ADD_PROBLEM:
                ProblemVO problemVO = (ProblemVO)args[0];
                result = checkRegex(problemVO.getTitle()) && checkRegex(problemVO.getSummary());
                break;
            case ADD_QUESTION:
                QuestionVO questionVO = (QuestionVO)args[0];
                result = checkRegex(questionVO.getSummary()) && checkRegex(questionVO.getTitle());
                break;
            case BLOG_PUBLISH:
                BlogVO blogVO = (BlogVO) args[0];
                result = checkRegex(blogVO.getTitle());
                break;
            case ADD_MOMENT:
                UserMomentVO userMomentVO = (UserMomentVO) args[0];
                result = checkRegex(userMomentVO.getContent());
                break;
        }
        if (!result){
            return ResultUtil.errorWithMessage("请勿发送无效内容");
        }
        //执行业务代码
        return point.proceed();  
    }

    
    private Boolean checkRegex (String content) {
        if (StringUtils.isEmpty(content)) {
            return true;
        }
        // 根据系统参数的正则表达式匹配
        String redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + SysConf.SYS_REGULAR_SLOGAN;
        String paramsValue = redisUtil.get(redisKey);
        if (StringUtils.isEmpty(paramsValue)) {
            paramsValue = sysParamsService.getSysParamsValueByKey(SysConf.SYS_REGULAR_SLOGAN);
        }
        String[] regexs = paramsValue.split(Constants.SYMBOL_COMMA);
        Boolean isUseless;
        for (int i = 0; i < regexs.length; i++) {
            Pattern pat = Pattern.compile(regexs[i].trim());//对比
            Matcher mat = pat.matcher(content.trim());
            isUseless = mat.matches();//判断是否匹配
            if (isUseless){
                return false;
            }
        }
        return true;
    }
}
