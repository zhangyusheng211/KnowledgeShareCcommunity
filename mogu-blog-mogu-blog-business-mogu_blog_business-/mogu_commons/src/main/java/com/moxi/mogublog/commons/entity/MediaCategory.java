package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 媒资分类对象 t_media_category
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_category")
public class MediaCategory extends SuperEntity<MediaCategory>
{

    /** 分类内容 */
    private String name;

    /** 分类简介 */
    private String content;

    /** 排序字段 */
    private Integer sort;

    /** 创建人 */
    private String createByUid;

    /** 更新人 */
    private String updateByUid;

    /** 备注 */
    private String remark;
}
