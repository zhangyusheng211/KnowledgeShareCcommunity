package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.enums.TaskSign;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.xo.service.UserPraiseRecordService;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mougblog.base.enums.EPraiseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 被点赞/点踩相关
 */
@Service
@Slf4j
public class ToPraise implements Step {

    @Resource
    private UserPraiseRecordService userPraiseRecordService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.TO_PRAISE) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();
        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            // 获取用户收到的点赞数量
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setToUserUid(task.getUserUid());
            userPraiseRecordVO.setPraiseType(EPraiseType.PRAISE.getType());
            process = userPraiseRecordService.getPraiseCount(userPraiseRecordVO);
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