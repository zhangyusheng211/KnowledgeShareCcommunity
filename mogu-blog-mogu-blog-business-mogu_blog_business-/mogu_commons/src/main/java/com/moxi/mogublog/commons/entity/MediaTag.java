package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 媒资标签对象 t_media_tag
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_tag")
public class MediaTag extends SuperEntity<MediaTag>
{

    /** 标签内容 */
    private String content;

    /** 标签点击数 */
    private Integer clickCount;

    /** 排序 */
    private Integer sort;

    /** 创建人 */
    private String createByUid;

    /** 更新人 */
    private String updateByUid;

    /** 是否默认（Y是 N否） */
    private String isDefault;

    /** 样式属性（其他样式扩展） */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 备注 */
    private String remark;
}
