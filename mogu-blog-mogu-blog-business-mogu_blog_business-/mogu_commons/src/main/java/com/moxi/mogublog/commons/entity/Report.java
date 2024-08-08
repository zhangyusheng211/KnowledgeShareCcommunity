package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 举报记录表
 * @author 遇见
 */
@Data
@TableName("t_report")
public class Report extends SuperEntity<Report> {

    /**
     * 举报类型
     */
    private String reportType;
    /**
     * 举报人Uid
     */
    private String userUid;
    /**
     * 被举报人Uid
     */
    private String reportUserUid;
    /**
     * 被举报稿件Uid
     */
    private String reportContentUid;
    /**
     * 被举报稿件内容/标题
     */
    private String reportContent;
    /**
     * 违规类型
     */
    private String violationType;

    /**
     * 举报信息
     */
    private String content;

    /**
     * 流程
     */
    private Integer progress;

    /**
     * 举报人
     */
    @TableField(exist = false)
    private User user;

    /**
     * 被举报人
     */
    @TableField(exist = false)
    private User reportUser;
}
