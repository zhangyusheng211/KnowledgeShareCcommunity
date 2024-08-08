package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 勋章表
 * @author 陌溪
 * @date 2022年12月27日09:10:09
 */
@Data
@TableName("t_medal")
public class Medal extends SuperEntity<Medal> {

    private static final long serialVersionUID = 1L;

    /**
     * 勋章文件url
     */
    private String fileUrl;

    /**
     * 勋章分类uid
     */
    private String medalClassifyUid;

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
     * 排序字段
     */
    private Integer sort;

    // 以下字段不存入数据库

    /**
     * 勋章获取时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date gainTime;

    /**
     * 勋章分类
     */
    @TableField(exist = false)
    private MedalClassify medalClassify;


    /**
     * 获得频率
     */
    @TableField(exist = false)
    private String gainProbability;


}
