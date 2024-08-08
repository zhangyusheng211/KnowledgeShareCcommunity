package com.moxi.mogublog.sms.task.server.steps;

import com.alibaba.fastjson.JSONObject;
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
import com.moxi.mougblog.base.enums.EOrderStatus;
import com.moxi.mougblog.base.enums.EPayType;
import com.moxi.mougblog.base.enums.EResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 赞助订单金额相关任务
 */
@Service
@Slf4j
public class SponsorFee implements Step {

    @Resource
    private CommonService commonService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.SPONSOR_FEE) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();
        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            PayOrderVO payOrderVO = new PayOrderVO();
            payOrderVO.setOrderStatus(EOrderStatus.Finish);
            payOrderVO.setUserUid(task.getUserUid());
            payOrderVO.setResourceType(EResourceType.SPONSOR.getType());
            payOrderVO.setPayType(EPayType.CASH_PAY.getType());
            process = commonService.getPayOrderSumFee(payOrderVO);
            task.setProcess(process);
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