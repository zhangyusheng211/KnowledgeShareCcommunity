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
import com.moxi.mogublog.xo.service.ResourceService;
import com.moxi.mogublog.commons.vo.ResourceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 资源相关事件处理
 */
@Service
@Slf4j
public class Resource implements Step {

    @javax.annotation.Resource
    private ResourceService resourceService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.RESOURCE) {
            return State.WAIT;
        }
        // 额外的参数
        Map extraMap = (Map) o;
        Conf conf = JSONObject.parseObject(config.getExt(), Conf.class);
        long target = conf.getTarget();
        long process = task.getProcess();

        // 如果 Type 为空，默认就是发布博客的请求
        String type = "publish";
        if (StringUtils.isNotEmpty(conf.getType())) {
            type = conf.getType();
        }

        // 如果是成就，那么需要直接查数据获取
        if (TaskSign.ACHIEVE.getType().equals(task.getMarketName())) {
            // 获取用户发表的资源数量
            ResourceVO resourceVO = new ResourceVO();
            resourceVO.setUserUid(task.getUserUid());
            process = resourceService.getResourceCount(resourceVO);
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