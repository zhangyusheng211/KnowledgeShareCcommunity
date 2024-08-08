package com.moxi.mogublog.sms.task.server;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.sms.global.SQLConf;
import com.moxi.mogublog.sms.task.dto.Button;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.entity.TaskConfig;
import com.moxi.mogublog.sms.task.entity.UserTaskRecord;
import com.moxi.mogublog.sms.task.enums.LimitType;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.mapper.TaskMapper;
import com.moxi.mogublog.sms.task.mapper.UserTaskRecordMapper;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.commons.vo.TaskConfigVO;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.ETaskSign;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
@Log4j2
public class TaskService {
    private static Map<String, TaskConfig> baseTask = new HashMap<>();
    public static Map<String, TaskConfig> taskMap = Collections.unmodifiableMap(baseTask);
    private static Map<String, List<TaskConfig>> taskList = new HashMap<>();
    private static List<TaskConfig> allList = new ArrayList<>();

    @Autowired
    Map<String, Step> step;

    @Autowired
    private TaskMapper taskMapper;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserTaskRecordMapper userTaskRecordMapper;


    public List<TaskConfig> getTaskByName(String marketName) {
        return taskList.get(marketName);
    }

    public List<TaskConfig> getTaskAll() {
        return allList;
    }

    public TaskConfig getTask(String key) {
        if (taskMap.containsKey(key))
            return taskMap.get(key);
        if (taskMap.isEmpty())
            loadTask();
        if (taskMap.containsKey(key))
            return taskMap.get(key);
        throw new BusinessException("NotFound taskConfig, key=" + key);
    }

    // 100 * 60秒刷新一次任务
    @Scheduled(fixedRate = 100 * 60 * 1000)
    public synchronized void loadTask() {
        // TODO from redis
        // from db
        QueryWrapper<TaskConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
//        queryWrapper.lt("start_time", new Date());
//        queryWrapper.gt("end_time", new Date());
        List<TaskConfig> list = taskMapper.selectList(queryWrapper);
        if (list == null)
            return;
        for (TaskConfig task : list) {
            // 组装task步骤
            for (StepConfig stepConf : task.getStepConfigList()) {
                Step step1 = step.get(stepConf.getType());
                task.getSteps().add(step1);
            }
        }

        Map<String, TaskConfig> baseTask = new HashMap<>();
        list.forEach(task -> {
            baseTask.put(task.getUid(), task);
        });
        TaskService.baseTask = baseTask;
        TaskService.taskMap = Collections.unmodifiableMap(baseTask);
        Map<String, List<TaskConfig>> taskList = new HashMap<>();
        list.forEach(task -> {
            if (taskList.containsKey(task.getMarketName()))
                taskList.get(task.getMarketName()).add(task);
            else {
                List<TaskConfig> l = new ArrayList<>();
                l.add(task);
                taskList.put(task.getMarketName(), l);
            }
        });
        TaskService.taskList = taskList;
        List<TaskConfig> allList = new ArrayList<>();
        list.forEach(task -> {
            allList.add(task);
        });
        TaskService.allList = allList;
        log.info("tasks refresh!");
    }


    /**
     * 分页获取任务配置
     *
     * @param taskConfigVO
     * @return
     */
    public IPage<TaskConfig> getPageList(TaskConfigVO taskConfigVO) {
        QueryWrapper<TaskConfig> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(taskConfigVO.getName())) {
            queryWrapper.eq(SQLConf.NAME, taskConfigVO.getName());
        }

        if (StringUtils.isNotEmpty(taskConfigVO.getMarketName())) {
            queryWrapper.eq("market_name", taskConfigVO.getMarketName());
        }

        if (StringUtils.isNotEmpty(taskConfigVO.getLimitType())) {
            queryWrapper.eq("limit_type", taskConfigVO.getLimitType());
        }

        Page<TaskConfig> page = new Page<>();
        page.setCurrent(taskConfigVO.getCurrentPage());
        page.setSize(taskConfigVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(taskConfigVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(taskConfigVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(taskConfigVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(taskConfigVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(com.moxi.mogublog.xo.global.SQLConf.SORT, com.moxi.mogublog.xo.global.SQLConf.CREATE_TIME);
        }
        IPage<TaskConfig> pageList = taskMapper.selectPage(page, queryWrapper);
        return pageList;
    }

    /**
     * 新增任务配置
     *
     * @param taskConfigVO
     * @return
     */
    public String addTaskConfig(TaskConfigVO taskConfigVO) {
        TaskConfig taskConfig = this.vo2Entity(taskConfigVO);
        boolean isSave = taskConfig.insert();
        if (isSave) {
            // 配置修改后，重新刷新配置
            this.loadTask();
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    /**
     * 编辑任务配置
     *
     * @param taskConfigVO
     * @return
     */
    public String editTaskConfig(TaskConfigVO taskConfigVO) {
        if (StringUtils.isEmpty(taskConfigVO.getUid())) {
            throw new UpdateException(MessageConf.PARAM_INCORRECT);
        }

        TaskConfig tempTaskConfig = taskMapper.selectById(taskConfigVO.getUid());
        if (tempTaskConfig == null) {
            throw new UpdateException(MessageConf.ENTITY_NOT_EXIST);
        }

        TaskConfig taskConfig = this.vo2Entity(taskConfigVO);
        boolean isSave = taskConfig.updateById();

        // 修改了任务配置，需要将该任务对应的完成用户记录给清除
        if (StringUtils.isNotEmpty(taskConfig.getStepConfig()) && !taskConfig.getStepConfig().equals(tempTaskConfig.getStepConfig())) {
            // 如果是成就，就需要将用户的最终状态，修改为WAIT【同时应该做好幂等性，防止重复发奖】
            if (ETaskSign.ACHIEVE.equals(taskConfig.getMarketName())) {
                // 查询所有已完成的成就，将其刷新为等待中【可能新加任务】
                QueryWrapper<UserTaskRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(SQLConf.TASK_ID, taskConfig.getUid());
                queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
                queryWrapper.eq(SQLConf.STATE, State.FINISH);
                List<UserTaskRecord> userTaskList = userTaskRecordMapper.selectList(queryWrapper);
                for (UserTaskRecord userTaskRecord : userTaskList) {
                    // 将所有的任务，修改为wait
                    userTaskRecord.setState(State.WAIT);
                    userTaskRecord.updateById();
                    // 清空redis中，该条任务记录
                    redisUtil.delete("userTask" + StrUtil.COLON + userTaskRecord.getUserId() + StrUtil.COLON + userTaskRecord.getTaskId());
                }

            }
        }

        if (isSave) {
            // 配置修改后，重新刷新配置
            this.loadTask();
        }


        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    /**
     * 删除任务配置
     *
     * @param taskConfigUidList
     * @return
     */
    public String deleteBatchTaskConfig(List<String> taskConfigUidList) {
        if (taskConfigUidList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<TaskConfig> taskConfigs = taskMapper.selectBatchIds(taskConfigUidList);
        taskConfigs.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
            taskMapper.updateById(item);
        });
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    private TaskConfig vo2Entity(TaskConfigVO taskConfigVO) {
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.setUid(taskConfigVO.getUid());
        taskConfig.setIsPublish(taskConfigVO.getIsPublish());
        taskConfig.setMarketName(taskConfigVO.getMarketName());
        taskConfig.setGroupId(taskConfigVO.getGroupId());
        taskConfig.setName(taskConfigVO.getName());
        taskConfig.setDescription(taskConfigVO.getDescription());
        taskConfig.setRule(taskConfigVO.getRule());
        taskConfig.setIcon(taskConfigVO.getIcon());
        taskConfig.setStepConfig(taskConfigVO.getStepConfig());
        taskConfig.setStartTime(taskConfigVO.getStartTime());
        taskConfig.setEndTime(taskConfigVO.getEndTime());
        taskConfig.setSort(taskConfigVO.getSort());
        LimitType limitType = LimitType.valueOf(taskConfigVO.getLimitType());
        taskConfig.setLimitType(limitType);
        taskConfig.setLimitCron(taskConfigVO.getLimitCron());
        taskConfig.setGroupId(taskConfigVO.getLimitType());

        // 如果开始时间为空，那么生效时间为当前时间
        if (taskConfigVO.getStartTime() == null) {
            taskConfig.setStartTime(new Date());
        } else {
            taskConfig.setStartTime(taskConfigVO.getStartTime());
        }
        // 判断结束时间是否为空
        if (taskConfigVO.getEndTime() == null) {
            // 设置30年后
            taskConfig.setEndTime(DateUtils.getNextDateTime(30 * 100));
        } else {
            taskConfig.setEndTime(taskConfigVO.getEndTime());
        }
        // 设置按钮样式
        if (StringUtils.isNotEmpty(taskConfigVO.getButton())) {
            Button button = JsonUtils.jsonToPojo(taskConfigVO.getButton(), Button.class);
            taskConfig.setButton(button);
        }
        taskConfig.setLimitCron(taskConfigVO.getLimitCron());
        return taskConfig;
    }


}