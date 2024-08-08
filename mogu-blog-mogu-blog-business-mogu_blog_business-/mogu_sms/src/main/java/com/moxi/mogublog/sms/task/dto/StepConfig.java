package com.moxi.mogublog.sms.task.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 步骤配置,可覆盖页面按钮状态
 */
@Data
public class StepConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 步骤类型，对应step实现类小驼峰名称
     */
    private String type;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤描述
     */
    private String desc;

    /**
     * 开始时间
     */
    private long starttime;

    /**
     * 结束时间
     */
    private long endtime;

    /**
     * 按钮
     */
    private Button button;

    /**
     * 步骤额外配置,如奖品id
     */
    private String ext;
}