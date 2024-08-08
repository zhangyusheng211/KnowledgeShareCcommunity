package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 表情包表
 *
 * @author 陌溪
 * @since 2020年3月16日08:35:26
 */
@Data
@TableName("t_emoticon")
public class Emoticon extends SuperEntity<Emoticon> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 表情包文件uid
     */
    private String fileUid;

    /**
     * 是否精选
     */
    private Integer isSelection;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 表情包图片
     */
    @TableField(exist = false)
    private String photoUrl;

}
