package com.moxi.mogublog.sms.task.server;

import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;

public interface Step {
    State execute(UserTask task, Action action, StepConfig config, Object ext);
}


