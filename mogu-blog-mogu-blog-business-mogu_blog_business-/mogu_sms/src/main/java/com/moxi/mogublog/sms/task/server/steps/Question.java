package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.commons.vo.QuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 问答相关事件处理
 */
@Service
@Slf4j
public class Question implements Step {

    @Resource
    private QuestionService questionService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.QUESTION) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();
        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            // 获取用户发表的博客数量
            QuestionVO questionVO = new QuestionVO();
            questionVO.setUserUid(task.getUserUid());
            process = questionService.getQuestionCount(questionVO);
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