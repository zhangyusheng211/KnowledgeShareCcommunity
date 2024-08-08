package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 试卷表
 * @author 陌溪
 * @date 2022年4月13日21:42:26
 */
@Data
@TableName("t_exam")
public class Exam extends SuperEntity<Exam> {

    private static final long serialVersionUID = 1L;
    /**
     * 唯一oid，自动递增
     */
    private Integer oid;
    /**
     * 用户id
     */
    private String userUid;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 点击数
     */
    private Integer clickCount;
    /**
     * 收藏数
     */
    private Integer collectCount;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 问题标签uid列表
     */
    private String examTagUid;
    /**
     * 是否公开
     */
    private Integer isOpen;

}
