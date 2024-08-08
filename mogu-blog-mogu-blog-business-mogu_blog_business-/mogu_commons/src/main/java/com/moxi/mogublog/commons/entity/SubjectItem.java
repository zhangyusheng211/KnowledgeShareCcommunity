package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 专题Item表
 * </p>
 *
 * @author 陌溪
 * @since 2020年8月22日21:56:18
 */
@Data
@TableName("t_subject_item")
public class SubjectItem extends SuperEntity<SubjectItem> {

    private static final long serialVersionUID = 1L;

    /**
     * 专题UID
     */
    private String subjectUid;
    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;

    /**
     * 开启试读
     */
    private String tryRead;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 博客
     */
    @TableField(exist = false)
    private Blog blog;
}
