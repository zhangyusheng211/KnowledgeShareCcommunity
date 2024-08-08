package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 问题标签表
 * @author 陌溪
 * @date 2022年3月7日21:42:26
 */
@Data
@TableName("t_problem_tag")
public class ProblemTag extends SuperEntity<ProblemTag> {
    /**
     * 父uid
     */
    private String parentUid;
    /**
     * 标签名
     */
    private String name;
    /**
     * 标签简介
     */
    private String summary;
    /**
     * 标签类型（推荐、热门、语言、知识、岗位、公司、目标）
     */
    private Integer tagType;
    /**
     * 标签级别
     */
    private Integer tagLevel;
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
     * 标签试题数
     */
    private Integer problemCount;

    /**
     * 是否跳转外链
     */
    private String isJumpExternalUrl;
    /**
     * 链接
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String url;
    /**
     * 图标
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String icon;
    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 子标签列表
     */
    @TableField(exist = false)
    private List<ProblemTag> children;
}
