package com.moxi.mogublog.sms.task.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.sms.task.dto.Button;
import com.moxi.mogublog.sms.task.dto.StepConfig;
import com.moxi.mogublog.sms.task.enums.LimitType;
import com.moxi.mogublog.sms.task.server.Step;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务配置表
 *
 * @author 陌溪
 * @since 2022年12月17日21:17:01
 */
@Data
@TableName("t_task_config")
@Slf4j
public class TaskConfig extends SuperEntity<TaskConfig> implements Comparable<TaskConfig> {

    private static final long serialVersionUID = 1L;
    /**
     * 活动id
     */
    private String marketName;

    /**
     * 分组,用于返回页面
     */
    private String groupId;

    /**
     * 任务名称,用于页面(如: 任务1:上传任务)
     */
    private String name;

    /**
     * 任务简介 (如: 上传1张照片完成任务,获取X空间)
     */
    private String description;

    /**
     * 任务规则, hmtl格式，用于展示规则； <div>任务指引: 1.点击xx按钮 ...</div>
     */
    private String rule;

    /**
     * 任务图标,url, http://www.baidu.com/a.jpg
     */
    private String icon;

    /**
     * 任务步骤配置
     */
    private String stepConfig;

    /**
     * 解析后的任务步骤配置
     */
    @TableField(exist = false)
    private List<StepConfig> stepConfigList;

    /**
     * 任务开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 任务结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 排序，负的永远在正的前面,已完成在未完成后面,其他情况按从小到大排序
     */
    private int sort;

    /**
     * 页面按钮参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Button button;

    /**
     * 任务类型
     */
    private LimitType limitType;

    /**
     * 类型为cron时自定义限制
     */
    private String limitCron;

    /**
     * 是否发布(1:是，0:否)
     */
    private String isPublish;

    /**
     * 页面附加参数,前端提供
     */
    @TableField(jdbcType = JdbcType.VARCHAR, typeHandler = JacksonTypeHandler.class)
    private JSONObject pageShow;

    // 以下字段不存入数据库

    /**
     * 任务步骤列表,无需处理
     */
    @TableField(exist = false)
    private List<Step> steps;

    /**
     * 无参构造
     */
    public TaskConfig() {

    }

    public TaskConfig(TaskConfig taskConfig) {
        BeanUtils.copyProperties(taskConfig, this);

    }

    @Override
    public int compareTo(TaskConfig o) {
        return sort - o.sort;
    }

    public List<Step> getSteps() {
        if (steps == null)
            steps = new ArrayList<>();
        return steps;
    }

    public List<StepConfig> getStepConfigList() {
        if (stepConfigList != null) {
            return stepConfigList;
        }
        try {
            stepConfigList = JSONObject.parseObject(this.stepConfig, new TypeReference<List<StepConfig>>() {
            });
        } catch (Exception e) {
            log.error("任务配置步骤解析失败, marketname={},taskid={}", marketName, getUid());
            throw e;
        }
        return stepConfigList;
    }
}
