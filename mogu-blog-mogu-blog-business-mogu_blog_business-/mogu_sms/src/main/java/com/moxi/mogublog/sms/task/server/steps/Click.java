package com.moxi.mogublog.sms.task.server.steps;

import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.server.Step;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 简单页面点击步骤
 * ext = "abc"
 * 需要页面携带 key=abc
 * <p>
 * 兼容多次点击
 * ext = {"str":"abc","count":2}
 * <p>
 * 兼容aes加密参数
 * ext = {"type":"aes","str":"abc","count":2}
 */
@Service
@Slf4j
public class Click implements Step {

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object ext) {
        if (action != Action.CLICK)
            return State.WAIT;
        if (!(ext instanceof String))
            return State.WAIT;

        // 点击成功，进入下一步
        return State.SUCCESS;

//        String str = (String) ext;
//        if (StringUtils.isNotBlank(config.getExt()) && config.getExt().equals(str))
//            return State.SUCCESS;
//        if (JSONValidator.from(config.getExt()).validate()) {
//            // 加解密操作
//        }
//        return State.WAIT;
    }
}