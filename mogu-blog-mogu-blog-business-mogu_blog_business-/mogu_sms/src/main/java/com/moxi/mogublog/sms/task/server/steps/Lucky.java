package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
import com.moxi.mogublog.commons.vo.ProblemVO;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.xo.service.LuckyRecordService;
import com.moxi.mogublog.xo.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 抽奖相关事件处理
 */
@Service
@Slf4j
public class Lucky implements Step {
    @Resource
    private LuckyRecordService luckyRecordService;
    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.LUCKY) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();

        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            // 获取用户发表的面经数量
            LuckyRecordVO luckyRecordVO = new LuckyRecordVO();
            luckyRecordVO.setUserUid(task.getUserUid());
            process = luckyRecordService.getLuckyRecordCount(luckyRecordVO);
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