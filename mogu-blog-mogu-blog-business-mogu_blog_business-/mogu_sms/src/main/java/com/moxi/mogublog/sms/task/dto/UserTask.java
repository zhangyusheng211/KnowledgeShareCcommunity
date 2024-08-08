package com.moxi.mogublog.sms.task.dto;

import com.moxi.mogublog.sms.task.entity.TaskConfig;
import com.moxi.mogublog.sms.task.entity.UserTaskRecord;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 用户任务状态
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Slf4j
public class UserTask extends TaskConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userUid;
    /**
     * 状态
     */
    private State state = State.WAIT;
    /**
     * 当前步骤,仅读取
     */
    private int currentStep = 0;
    /**
     * 当前步骤进度
     */
    private long process = 0;
    /**
     * 最后更新时间,无需处理,用于判定任务状态过期
     */
    private long lastUpdate = System.currentTimeMillis();
    /**
     * 对应任务记录id
     */
    private String recordId;

    /**
     * 填入任务配置和初始化任务步骤
     */
    public UserTask(TaskConfig taskConfig) {
        super(taskConfig);
        state = State.WAIT;
        currentStep = 0;
        process = 0;
        lastUpdate = System.currentTimeMillis();
    }

    public UserTaskRecord forCache() {
        UserTaskRecord userTaskRecord = new UserTaskRecord();
        userTaskRecord.setState(this.state);
        userTaskRecord.setCurrentStep(this.currentStep);
        userTaskRecord.setCurrentProcess(this.process);
        userTaskRecord.setLastUpdate(System.currentTimeMillis());
        userTaskRecord.setMarketName(this.getMarketName());
        userTaskRecord.setUserId(ThreadLocalUtil.getUserUid());
        userTaskRecord.setTaskId(this.getUid());
        userTaskRecord.setUid(this.recordId);
        return userTaskRecord;
    }

    public UserTask forShow() {
        setDescription(getDescription());
        setName(getName());
        setButton(getButton());
        setUserUid(null);
        setSteps(null);
        setStepConfig(null);
        setStepConfigList(null);
        return this;
    }

    public State nextStep(Action action, Object o) {
        try {
            while (currentStep < getSteps().size()) {
                List<Step> stepList = getSteps();
                Step step = stepList.get(currentStep);
                if (step == null) {
                    log.error("[nextStep] 任务配置存在异常，taskUid: {}", this.recordId);
                    throw new QueryException("任务配置存在异常，taskUid: " + this.recordId);
                }
                StepConfig config = getStepConfigList().get(currentStep);
                State nextState = step.execute(this, action, config, o);
                log.debug("nextStep:{},{},{},{}", getUserUid(), getUid(), getCurrentStep(), nextState);
                if (nextState == State.FINISH)
                    // 中途退出
                    break;
                switch (nextState) {
                    case BACK:
                        // 返回上一步
                        action = null;
                        o = null;
                        currentStep--;
                        continue;
                    case FINISH:
                        // 中途退出
                        break;
                    case SUCCESS:
                        currentStep++;
                        process = 0;
                        break;
                    default:
                        lastUpdate = System.currentTimeMillis();
                        return nextState;
                }
            }
        } catch (BusinessException e) {
            log.info("nextStep businessException, userUid:{}, taskId:{}, currentStep:{}", getUserUid(), getUid(), getCurrentStep(), e);
            return State.ERROR;
        } catch (NullPointerException e) {
            log.error("UserTask nextStep NullPointerException, userUid:{}, taskId:{}, currentStep:{}", getUserUid(), getUid(), getCurrentStep(), e);
            return State.ERROR;
        } catch (Exception e) {
            log.error("UserTask nextStep exception, userUid:{}, taskId:{}, currentStep:{}", getUserUid(), getUid(), getCurrentStep(), e);
            return State.ERROR;
        }
        lastUpdate = System.currentTimeMillis();
        state = State.FINISH;
        return State.FINISH;
    }

    @Override
    public Button getButton() {
        if (getStepConfigList() != null) {
            int currstep = this.currentStep;
            if (getStepConfigList().size() <= currstep)
                currstep = getStepConfigList().size() - 1;
            for (int i = currstep; i >= 0; i--) {
                if (getStepConfigList().get(i) != null) {
                    Button button = getStepConfigList().get(i).getButton();
                    if (button != null)
                        return button;
                }
            }
        }
        return super.getButton();
    }

    @Override
    public String getName() {
        if (getStepConfigList() != null) {
            int currstep = this.currentStep;
            if (getStepConfigList().size() <= currstep)
                currstep = getStepConfigList().size() - 1;
            for (int i = currstep; i >= 0; i--)
                if (getStepConfigList().get(i) != null)
                    if (StringUtils.isNotBlank(getStepConfigList().get(i).getName()))
                        return getStepConfigList().get(i).getName();
        }
        return super.getName();
    }

    @Override
    public String getDescription() {
        String desc = null;
        if (getStepConfigList() != null) {
            int currstep = this.currentStep;
            if (getStepConfigList().size() <= currstep)
                currstep = getStepConfigList().size() - 1;
            for (int i = currstep; i >= 0; i--) {
                if (getStepConfigList().get(i) != null)
                    if (StringUtils.isNotBlank(getStepConfigList().get(i).getDesc())) {
                        desc = getStepConfigList().get(i).getDesc();
                        break;
                    }
            }
        }
        if (desc == null) {
            desc = super.getDescription();
        }
        if (desc != null && desc.contains("%s")) {
            if (state == State.FINISH) {
                desc = String.format(desc, "Max");
            } else {
                desc = String.format(desc, process);
            }
        }
        return desc;
    }

    public long increaseProcess() {
        process++;
        return process;
    }

    public long increaseProcess(long p) {
        process += p;
        return process;
    }

    public static void main(String[] args) {
        String desc = "奖励10积分（进度：%s/2）";
        if (desc != null && desc.contains("%s")) {
            desc = String.format(desc, 10);
        }
        System.out.println(desc);

    }
}