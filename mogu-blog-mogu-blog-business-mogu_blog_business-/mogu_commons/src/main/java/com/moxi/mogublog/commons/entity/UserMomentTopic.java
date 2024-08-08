package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户动态话题表
 *
 * @author 陌溪
 * @date 2021年12月25日23:47:02
 */
@Data
@TableName("t_user_moment_topic")
public class UserMomentTopic extends SuperEntity<UserMomentTopic> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 管理员
     */
    private String adminUid;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 文件uid
     */
    private String fileUid;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;

    // 以下字段不存入数据库
    /**
     * 图片信息
     */
    @TableField(exist = false)
    private String photoUrl;
}
