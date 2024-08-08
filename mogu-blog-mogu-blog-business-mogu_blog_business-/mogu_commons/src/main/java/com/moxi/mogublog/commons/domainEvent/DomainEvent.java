package com.moxi.mogublog.commons.domainEvent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 领域事件
 */
@Data
public class DomainEvent<T> {

    /**
     * 领域事件唯一uid
     */
    private String uid;

    /**
     * 实体类型
     */
    private EntityType entityType;

    /**
     * 事件名称
     */
    private EventAction eventAction;

    /**
     * 实体对应uid
     */
    private String EntityUid;

    /**
     * 传输过来的实体【Json序列化后的】
     */
    private T entity;

    /**
     * 是否是管理员操作【目前系统有两种用户，需要判断事件是由谁触发的，从而进行不同的流程】
     */
    private boolean isAdminOperate;

    /**
     * 额外扩展字段
     */
    private Map<String, Object> extra;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
