package com.moxi.mogublog.sms.task.server;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.sms.global.SysConf;
import com.moxi.mogublog.sms.task.config.RedisLock;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.entity.TaskConfig;
import com.moxi.mogublog.sms.task.entity.UserTaskRecord;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.LimitType;
import com.moxi.mogublog.sms.task.enums.MarketNameEnum;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.golbal.Constant;
import com.moxi.mogublog.sms.task.mapper.UserTaskRecordMapper;
import com.moxi.mogublog.utils.CronUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务
 */
@Service
public class UserTaskService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisLock redisLock;

    @Async("taskExecutor")
    public void syncDoTask(Action action, String id, Object o, String phone) {
        try {
            doTask(action, id, o, ThreadLocalUtil.getUserUid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionAsync(Action action, String id, Object o, String phone) {
        if (id == null) {
            List<TaskConfig> list = taskService.getTaskAll();
            if (CollectionUtil.isEmpty(list))
                return;
            list.forEach(task -> {
                syncDoTask(action, task.getUid(), o, phone);
            });
        } else {
            doTask(action, id, o, ThreadLocalUtil.getUserUid());
        }
    }

    /**
     * 操作事件
     *
     * @param action 事件种类
     * @param id     任务id(为空则所有任务)
     * @param o      额外参数
     * @return 单个任务返回任务状态, 多个任务返回null
     */
    public void action(Action action, String id, Object o) {
        if (id == null) {
            List<TaskConfig> list = taskService.getTaskAll();
            if (CollectionUtil.isEmpty(list)) {
                // 如果任务为空，再加载一下任务
                taskService.loadTask();
                list = taskService.getTaskAll();
            }
            if (CollectionUtil.isEmpty(list))
                return;

            for (TaskConfig task : list) {
                doTask(action, task.getUid(), o, ThreadLocalUtil.getUserUid());
            }
        } else {
            doTask(action, id, o, ThreadLocalUtil.getUserUid());
        }
    }

    /**
     * 执行某人的任务
     *
     * @param action
     * @param id
     * @param userUid
     * @param o
     */
    public void action(Action action, String id, Object o, String userUid) {
        if (id == null) {
            List<TaskConfig> list = taskService.getTaskAll();
            if (CollectionUtil.isEmpty(list)) {
                // 如果任务为空，再加载一下任务
                taskService.loadTask();
                list = taskService.getTaskAll();
            }
            if (CollectionUtil.isEmpty(list))
                return;

            for (TaskConfig task : list) {
                doTask(action, task.getUid(), o, userUid);
            }
        } else {
            doTask(action, id, o, userUid);
        }
    }

    public void doTask(Action action, String id, Object o, String userUid) {
        String lockKey = Constant.TASK_SERVER_KEY + StrUtil.COLON + "action2" + id + StrUtil.COLON + userUid;
        try {
            redisLock.lock(lockKey, 1, 5);
            UserTask utask = getUserTask(id, userUid);
            int step = utask.getCurrentStep();
            long process = utask.getProcess();
            if (utask.getState() == State.FINISH)
                return;
            utask.nextStep(action, o);
            if (step != utask.getCurrentStep() || !NumberUtil.equals(process, utask.getProcess()))
                userTaskRecordMapper.updateById(UserTaskRecord.from(utask));
            saveUserTaskCache(utask, userUid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock(lockKey);
        }
    }

    /**
     * 页面获取用户任务列表
     */
    public List<UserTask> getUserTaskAll(MarketNameEnum marketName) {
        return getUserTaskAll(marketName, null);
    }

    /**
     * 页面获取用户任务列表
     *
     * @param groupId 组别
     */
    public List<UserTask> getUserTaskAll(MarketNameEnum marketName, String groupId) {
        List<TaskConfig> list = taskService.getTaskByName(marketName.getCode());
        if (list == null)
            return null;
        List<UserTask> ulist = new ArrayList<>();
        list.forEach(task -> {
            if (groupId != null && !task.getGroupId().equals(groupId))
                return;
            try {
                // 地区专属任务
                //if (task.getProv() != null && !"0".equals(task.getProv()) && Phone.getInfo() != null  && !task.getProv().contains(Phone.getInfo().getProvCode()))
                //	return;

                // 是否是cron表达式的定时任务
                if (task.getLimitType() == LimitType.CRON) {
                    boolean isMatch = CronUtil.isMatch(task.getLimitCron());
                    if (!isMatch) {
                        return;
                    }
                }
            } catch (Exception e) {
                return;
            }
            ulist.add(StringUtils.isEmpty(ThreadLocalUtil.getUserUid()) ? new UserTask(task) : getUserTask(task.getUid()));
        });
        return ulist;
    }

    /**
     * 获取用户的任务列表
     *
     * @param marketName
     * @param userUid
     * @return
     */
    public List<UserTask> getUserTaskAllByUserUid(MarketNameEnum marketName, String userUid) {
        List<TaskConfig> list = taskService.getTaskByName(marketName.getCode());
        if (list == null)
            return null;
        List<UserTask> ulist = new ArrayList<>();
        list.forEach(task -> {
            ulist.add(StringUtils.isEmpty(userUid) ? new UserTask(task) : getUserTask(task.getUid()));
        });
        return ulist;
    }

    /**
     * 获取用户单个任务状态
     */
    public UserTask getUserTask(String id) {
        return getUserTask(id, ThreadLocalUtil.getUserUid());
    }

    public UserTask getUserTask(String id, String userUid) {
        String lockKey = Constant.TASK_SERVER_KEY + StrUtil.COLON + "getState" + id + StrUtil.COLON + userUid;
        try {
            redisLock.lock(lockKey, 1, 5);
            TaskConfig taskConfig = taskService.getTask(id);
            UserTask userTask = getUserTaskCache(id, userUid);
            if (userTask != null) {
                // 过期重新初始化
                if (taskConfig.getLimitType().exipre(userTask, userTask.getLastUpdate()))
                    userTask = null;
            } else {
                // 查库
                UserTaskRecord userTaskRecord = userTaskRecordMapper.selectOne(new QueryWrapper<UserTaskRecord>().lambda().eq(UserTaskRecord::getMarketName, taskConfig.getMarketName())
                        .eq(UserTaskRecord::getTaskId, taskConfig.getUid())
                        .eq(UserTaskRecord::getUserId, userUid)
                        .last(SysConf.LIMIT_ONE)
                        .orderByDesc(UserTaskRecord::getCreateTime));

                if (userTaskRecord != null) {
                    // 判断任务是否过期
                    if (!taskConfig.getLimitType().exipre(userTask, userTaskRecord.getLastUpdate())) {
                        // 未过期,返回结果存入缓存
                        userTask = userTaskRecord.toUserTask(taskConfig);
                        // 补全任务步骤
                        saveUserTaskCache(userTask, userUid);
                    }
                }
            }
            if (userTask == null) {
                // 初始化
                userTask = new UserTask(taskConfig);
            }
            userTask.setUserUid(userUid);
            if (StringUtils.isEmpty(userTask.getRecordId())) {
                UserTaskRecord record = UserTaskRecord.from(userTask);
                if (userTask.getUserUid() != null) {
                    userTaskRecordMapper.insert(record);
                }
                userTask = record.toUserTask(taskConfig);
            }
            saveUserTaskCache(userTask, userUid);
            return userTask;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock(lockKey);
        }
        return null;
    }

    /**
     * 用户任务状态存入缓存
     *
     * @param userTask
     */
    @SuppressWarnings("unchecked")
    private void saveUserTaskCache(UserTask userTask, String userUid) {
        redisTemplate.opsForValue().set("userTask" + StrUtil.COLON + userUid + StrUtil.COLON + userTask.getUid(),
                userTask.forCache(), 12, TimeUnit.HOURS);

    }

    /**
     * 获取用户任务状态缓存对象
     * 已补全任务步骤
     *
     * @param id
     */
    private UserTask getUserTaskCache(String id, String userUid) {
        String account = userUid;
        if (account == null) {
            throw new QueryException("用户未登录");
        }
        UserTaskRecord userTaskRecord = (UserTaskRecord) redisTemplate.opsForValue()
                .get("userTask" + StrUtil.COLON + account + StrUtil.COLON + id);
        if (userTaskRecord != null) {
            TaskConfig task = taskService.getTask(id);
            UserTask userTask = userTaskRecord.toUserTask(task);
            BeanUtils.copyProperties(task, userTask);
            return userTask;
        }
        return null;
    }
}