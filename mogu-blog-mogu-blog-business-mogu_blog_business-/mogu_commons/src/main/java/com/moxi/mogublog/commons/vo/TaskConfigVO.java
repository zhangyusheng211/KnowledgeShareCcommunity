package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 任务配置VO
 */
@Data
public class TaskConfigVO extends BaseVO<TaskConfigVO> {
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
     * 任务简介(如: 上传1张照片完成任务,获取X空间)
     */
    private String description;

    /**
     * 任务规则: <div>任务指引: 1.点击xx按钮 ...</div>
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
     * 排序,负的永远在正的前面,已完成在未完成后面,其他情况按从小到大排序
     */
    private int sort;

    /**
     * 页面按钮参数
     */
    private String button;

    /**
     * 限制类型
     */
    private String limitType;

    /**
     * 型为cron时自定义限制
     */
    private String limitCron;

    /**
     * 页面附加参数,前端提供
     */
    private String pageShow;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 是否发布(1:是，0:否)
     */
    private String isPublish;
}
