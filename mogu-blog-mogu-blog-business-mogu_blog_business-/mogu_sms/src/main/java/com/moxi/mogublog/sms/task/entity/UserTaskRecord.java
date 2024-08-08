package com.moxi.mogublog.sms.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 用户日志记录表
 *
 * @author 陌溪
 * @since 2022年12月17日21:17:01
 */
@Data
@TableName("t_user_task_record")
public class UserTaskRecord extends SuperEntity<UserTaskRecord> implements Comparable<TaskConfig> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名
     */
    String taskId;

    /**
     * 活动名
     */
    String marketName;

    /**
     * 用戶id
     */
    String userId;
    /**
     * 状态
     */
    State state;
    /**
     * 当前步骤
     */
    int currentStep;
    /**
     * 当前步骤进度
     */
    long currentProcess;

    /**
     * 最后更新时间
     */
    long lastUpdate;

    public static UserTaskRecord from(UserTask userTask) {
        UserTaskRecord userTaskRecord = new UserTaskRecord();
        userTaskRecord.setUid(userTask.getRecordId());
        userTaskRecord.setTaskId(userTask.getUid());
        userTaskRecord.setMarketName(userTask.getMarketName());
        userTaskRecord.setUserId(userTask.getUserUid());
        userTaskRecord.setState(userTask.getState());
        userTaskRecord.setCurrentStep(userTask.getCurrentStep());
        userTaskRecord.setCurrentProcess(userTask.getProcess());
        userTaskRecord.setLastUpdate(userTask.getLastUpdate());
        return userTaskRecord;
    }

    /**
     * 已补全任务步骤
     */
    public UserTask toUserTask(TaskConfig taskConfig) {
        UserTask u = new UserTask(taskConfig);
        u.setUid(taskId);
        u.setMarketName(this.getMarketName());
        u.setUserUid(this.userId);
        u.setState(state);
        u.setCurrentStep(this.currentStep);
        u.setProcess(this.currentProcess);
        u.setLastUpdate(lastUpdate);
        u.setRecordId(getUid());
        return u;
    }

    @Override
    public int compareTo(@NotNull TaskConfig o) {
        return 0;
    }
}
