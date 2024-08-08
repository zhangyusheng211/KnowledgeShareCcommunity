package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.sms.global.SysConf;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 博客被访问
 */
@Service
@Slf4j
public class ToBlogVisit implements Step {

    @Resource
    private BlogService blogService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.TO_BLOG_VISIT) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();

        // 如果 Type 为空，默认就是单个阅读成就
        String type = "single";
        if (StringUtils.isNotEmpty(conf.getType())) {
            type = conf.getType();
        }

        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            switch (type) {
                case "single": {
                    // 单个阅读量达到上限
                    if (extraMap.get(SysConf.BLOG_UID) == null) {
                        log.error("[BlogStep] 获取博客uid为空");
                        throw new InsertException("获取博客uid为空");
                    }
                    com.moxi.mogublog.commons.entity.Blog blog = blogService.getById(extraMap.get(SysConf.BLOG_UID).toString());
                    if (blog != null && blog.getClickCount() > process) {
                        process = blog.getClickCount();
                    }
                }
                break;

                case "all": {
                    // 获取阅读该文章的用户
                    if (extraMap.get(SysConf.TO_USER_UID) == null) {
                        log.error("[BlogStep] 获取用户uid为空");
                        throw new InsertException("获取用户uid为空");
                    }
                    // 所有文章的阅读量达到上限
                    process = blogService.getBlogClickCount(task.getUserUid());
                }
                break;
            }
            task.setProcess(process);
        } else {
            // 如果任务，那么执行累加计算
            process = task.increaseProcess();
        }
        if (process >= target) {
            return State.SUCCESS;
        }
        return State.WAIT;
    }
}