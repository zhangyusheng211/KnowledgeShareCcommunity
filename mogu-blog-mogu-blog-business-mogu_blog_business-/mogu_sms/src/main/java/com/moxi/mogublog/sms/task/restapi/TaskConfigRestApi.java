package com.moxi.mogublog.sms.task.restapi;


import com.moxi.mogublog.sms.global.MessageConf;
import com.moxi.mogublog.sms.task.dto.UserTask;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.sms.task.enums.MarketNameEnum;
import com.moxi.mogublog.sms.task.enums.State;
import com.moxi.mogublog.sms.task.server.TaskService;
import com.moxi.mogublog.sms.task.server.UserTaskService;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.commons.vo.TaskConfigVO;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 任务相关
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskConfigRestApi {
    @Resource
    private UserTaskService userTaskService;

    @Resource
    TaskService taskService;

    /**
     * 任务列表
     */
    @GetMapping("/loadTask")
    public String loadTask() {
        log.info("[Task] 加载任务");
        taskService.loadTask();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    /**
     * 任务列表
     */
    @PostMapping("/getMyTaskListMap")
    public String getMyTaskListMap(String marketName) {
        MarketNameEnum marketNameEnum = MarketNameEnum.getMarketNameEnum(marketName);
        List<UserTask> tasks = userTaskService.getUserTaskAll(marketNameEnum);
        if (tasks == null || tasks.size() == 0) {
            taskService.loadTask();
            tasks = userTaskService.getUserTaskAll(marketNameEnum);
        }
        Map<String, List<UserTask>> m = new HashMap<>();
        if (tasks != null)
            tasks.forEach(task -> {
                String group = task.getGroupId();
                if (StringUtils.isBlank(group))
                    group = "other";
                if (!m.containsKey(group))
                    m.put(group, new ArrayList<>());
                m.get(group).add(task);
                // 设置展示信息
                task.forShow();
            });
        m.values().forEach(list -> {
            Collections.sort(list, new Comparator<UserTask>() {
                @Override
                public int compare(UserTask o1, UserTask o2) {
                    if (o1.getSort() < 0 && o2.getSort() < 0)
                        return o1.getSort() - o2.getSort();
                    if (o1.getSort() < 0)
                        return -1;
                    if (o2.getSort() < 0)
                        return 1;
                    if (o1.getState() == State.FINISH && o2.getState() == State.FINISH)
                        return o1.getSort() - o2.getSort();
                    if (o1.getState() == State.FINISH)
                        return 1;
                    if (o2.getState() == State.FINISH)
                        return -1;
                    return o1.getSort() - o2.getSort();
                }
            });
        });
        return ResultUtil.successWithData(m);
    }

    /**
     * 首页任务列表
     * @param marketName
     * @return
     */
    @PostMapping("/getMyTaskList")
    public String getMyTaskList(String marketName) {
        log.info("[Task] 获取我的任务列表");
        MarketNameEnum marketNameEnum = MarketNameEnum.getMarketNameEnum(marketName);
        List<UserTask> tasks = userTaskService.getUserTaskAll(marketNameEnum);
        if (tasks == null || tasks.size() == 0) {
            taskService.loadTask();
            tasks = userTaskService.getUserTaskAll(marketNameEnum);
        }
        List<UserTask> resultList = new ArrayList<>();
        if (tasks != null)
            tasks.forEach(task -> {
                task.forShow();
                resultList.add(task);

            });

        /**
         */
        Collections.sort(resultList, new Comparator<UserTask>() {
            @Override
            public int compare(UserTask o1, UserTask o2) {
                if (o1.getSort() < 0 && o2.getSort() < 0)
                    return o1.getSort() - o2.getSort();
                if (o1.getSort() < 0)
                    return -1;
                if (o2.getSort() < 0)
                    return 1;
                if (o1.getState() == State.FINISH && o2.getState() == State.FINISH)
                    return o1.getSort() - o2.getSort();
                if (o1.getState() == State.FINISH)
                    return 1;
                if (o2.getState() == State.FINISH)
                    return -1;
                return o1.getSort() - o2.getSort();
            }
        });

        return ResultUtil.successWithData(resultList);
    }

    @GetMapping("/info")
    public String info(String id) {
        if (id == null)
            throw new BusinessException(MessageConf.PARAM_INCORRECT);
        return ResultUtil.successWithData(userTaskService.getUserTask(id));
    }

    @GetMapping("/click")
    public String click(String id, String key) {
        if (StringUtils.isBlank(key))
            throw new BusinessException(MessageConf.PARAM_INCORRECT);
        if (id == null)
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        userTaskService.action(Action.CLICK, id, key);
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    /**
     * 获取任务列表，只允许内部访问
     *
     * @param taskConfigVO
     * @return
     */
    @PostMapping("/getPageList")
    public String getPageList(@RequestBody TaskConfigVO taskConfigVO) {
        log.info("[Task] 获取列表");
        return ResultUtil.successWithData(taskService.getPageList(taskConfigVO));
    }

    /**
     * 添加任务配置
     *
     * @param taskConfigVO
     * @return
     */
    @PostMapping("/add")
    public String addTaskConfig(@RequestBody TaskConfigVO taskConfigVO) {
        log.info("[Task] 新增任务");
        return taskService.addTaskConfig(taskConfigVO);
    }

    /**
     * 编辑任务配置
     *
     * @param taskConfigVO
     * @return
     */
    @PostMapping("/edit")
    public String editTaskConfig(@RequestBody TaskConfigVO taskConfigVO) {
        log.info("[Task] 编辑任务");
        return taskService.editTaskConfig(taskConfigVO);
    }

    /**
     * 批量删除任务配置
     *
     * @param taskConfigUidList
     * @return
     */
    @PostMapping("/deleteBatch")
    public String deleteBatchTaskConfig(@RequestBody List<String> taskConfigUidList) {
        log.info("[Task] 删除任务");
        return taskService.deleteBatchTaskConfig(taskConfigUidList);
    }
}