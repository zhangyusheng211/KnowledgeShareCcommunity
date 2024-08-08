package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.PayOrderVO;
import com.moxi.mogublog.sms.global.SQLConf;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.xo.service.CommonService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayType;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 积分变更相关任务
 */
@Service
@Slf4j
public class Credits implements Step {
    @Resource
    private UserService userService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.CREDITS) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();
        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            // 获取用户积分
            User user = userService.getById(task.getUserUid());
            task.setProcess(user.getCredits());
        } else {
            // 如果任务，那么执行累加计算
            long value = (long) extraMap.get(SQLConf.PRICE);
            process = process + value;
        }
        if (process >= target) {
            return State.SUCCESS;
        }
        return State.WAIT;
    }
}