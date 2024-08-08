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
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.commons.vo.BlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 博客相关事件
 */
@Service
@Slf4j
public class Blog implements Step {

    @Resource
    private BlogService blogService;

    @Override
    public State execute(UserTask task, Action action, StepConfig config, Object o) {
        if (action != Action.BLOG) {
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
            switch (type) {
                case "publish": {
                    // 获取用户发表的博客数量
                    BlogVO blogVO = new BlogVO();
                    blogVO.setUserUid(task.getUserUid());
                    process = blogService.getBlogCount(blogVO);
                }
                break;

                case "serial": {
                    // 获取连续发文的时间
                    process = blogService.getContinuousPublishBlogCount(task.getUserUid());
                }
                break;
            }
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