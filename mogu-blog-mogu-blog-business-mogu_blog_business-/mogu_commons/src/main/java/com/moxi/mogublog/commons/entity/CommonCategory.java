package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 商品表
 * @author 陌溪
 * @date 2024年6月1日21:38:13
 */
@TableName("t_common_category")
@Data
public class CommonCategory extends SuperEntity<CommonCategory> {

    // 业务分类：1：商品类别
    private Integer bizType;

    // 分类名称
    private String name;

    // 分类简介
    private String summary;

    // 父uid
    private String parentUid;

    // 分类icon图标
    private String icon;

    // 备注
    private String remark;

    // 排序字段
    private Integer sort;

    // 是否发布(1:是，0:否)
    private Integer isPublish;
}
