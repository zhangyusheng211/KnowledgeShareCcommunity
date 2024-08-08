package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 勋章分类表
 * @author 陌溪
 * @date 2022年12月28日09:02:23
 */
@Data
@TableName("t_medal_classify")
public class MedalClassify extends SuperEntity<MedalClassify> {

    private static final long serialVersionUID = 1L;

    /**
     * 父uid
     */
    private String parentUid;

    /**
     * 勋章名称
     */
    private String name;

    /**
     * 勋章简介
     */
    private String summary;


    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 类型【创作，活跃，成就，限定】
     */
    private Integer type;

    /**
     * 排序字段
     */
    private Integer sort;

    // 以下字段不存入数据库

    /**
     * 分类下的勋章列表
     */
    @TableField(exist = false)
    private List<Medal> medalList;
}
