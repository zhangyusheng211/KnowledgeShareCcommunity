package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.service.SignInRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 签到相关事件处理
 */
@Service
@Slf4j
public class SignIn implements Step {

    @Resource
    private SignInRecordService signInRecordService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.SIGN_IN) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();

        // 如果 Type 为空，默认就是连续签到请求
        String type = "serial";
        if (StringUtils.isNotEmpty(conf.getType())) {
            type = conf.getType();
        }
        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {

            switch (type) {
                case "serial": {
                    // 获取连续发文的时间
                    process = signInRecordService.getSignInCount(task.getUserUid());
                }
                break;
                case "all": {
                    // 获取累积签到次数
                    process = signInRecordService.getSignInSumCount(task.getUserUid());
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