package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * <p>
 * 专题分类表
 * </p>
 *
 * @author 陌溪
 * @since 2022年9月25日22:03:37
 */
@Data
@TableName("t_subject_sort")
public class SubjectSort extends SuperEntity<SubjectSort> {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类简介
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 是否精选
     */
    private String isSelection;
    /**
     * 是否上架
     */
    private String isPublish;
    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 分类图
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fileUid;

    /**
     * 图标
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String icon;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;


    /**
     * 系统预设【0 否，1 是】
     */
    private Integer systemPreinstall;

    /**
     * 预设配置
     */
    private String preinstallConfig;

    // 以下字段不存入数据库

    /**
     * 标题图
     */
    @TableField(exist = false)
    private String photoUrl;
}
