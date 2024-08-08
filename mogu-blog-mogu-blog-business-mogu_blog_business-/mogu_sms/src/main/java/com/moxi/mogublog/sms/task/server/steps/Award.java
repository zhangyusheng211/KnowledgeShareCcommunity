package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.sms.task.dto.Conf;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.AwardType;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.service.MedalService;
import com.moxi.mogublog.xo.service.UserEquityRecordService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EWebDomainEvent;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 奖赏步骤；用于发布奖励的步骤
 */
@Service
@Slf4j
public class Award implements Step {

    @Resource
    private UserService userService;
    @Resource
    private UserEquityRecordService userEquityRecordService;
    @Resource
    private AsyncService asyncService;
    @Resource
    private MedalService medalService;
    @Resource
    private WebFeignClient webFeignClient;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {

        // 颁发奖励
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        String type = conf.getType();
        String value = conf.getValue();
        AwardType awardType = AwardType.getByType(type);

        if (awardType == null) {
            throw new QueryException("[Award] 获取奖励类型失败");
        }

        switch (awardType) {
            case CREDITS: {
                Integer credits = Integer.valueOf(value);
                // 增加用户积分
                userService.addUserCredits(ECreditType.TASK_AWARD.getAction(), credits, task.getUid(), task.getUserUid());
                // 发完积分奖励，发个站内信
                asyncService.executeAsyncBusinessNotice(task.getUserUid(), String.format("恭喜您通过完成任务 %s 获得：%d 积分", task.getName(), credits));
                log.info("[Award] 完成任务，获得积分奖励; userUid: {}, credits: {}", task.getUserUid(), credits);
                // 通知前台任务完成
                String result = webFeignClient.sendDomainEventMessage(EWebDomainEvent.TASK_FINISH, task.getUserUid());
            }
            break;
            case SIGN_IN_CARD: {
                int count = Integer.parseInt(value);
                if (count == 0) {
                    count = 1;
                }
                // 调用发放签到卡接口
                boolean sendSuccess = userEquityRecordService.sendSignInCards(task.getUserUid(), count);

                // 发完签到卡，发个站内信
                if (sendSuccess) {
                    asyncService.executeAsyncBusinessNotice(task.getUserUid(), String.format("恭喜您通过完成任务获得：签到卡 %d 张", count));
                }
                log.info("[Award] 完成任务，获得签到卡奖励; userUid: {}, count: {}", task.getUserUid(), count);

                // 通知前台任务完成
                String result = webFeignClient.sendDomainEventMessage(EWebDomainEvent.TASK_FINISH, task.getUserUid());
                System.out.println(result);
            }
            break;
            case VIP: {
                // 调用放发vip体验卡接口
                log.info("[Award] 开始放发VIP体验卡");
            }
            break;

            case MEDAL: {
                log.info("[Award] 开始放发用户勋章: medalUid: {}", value);
                // 调用发放勋章的接口
                medalService.awardMedal(task.getUserUid(), value);
                // 通知前台获得勋章【控制弹出提示】
                webFeignClient.sendDomainEventMessage(EWebDomainEvent.MEDAL_AWARD, task.getUserUid());
            }
            break;
        }

        return State.SUCCESS;
    }
}