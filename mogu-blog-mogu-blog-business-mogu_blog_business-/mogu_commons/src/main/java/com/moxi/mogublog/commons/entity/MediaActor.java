package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 演员对象 t_media_actor
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_actor")
public class MediaActor extends SuperEntity<MediaActor>
{

    /** 姓名 */
    private String name;

    /** 头像 */
    private String avatar;

    /** 简述 */
    private String description;

    /** 奖项 */
    private String awards;

    /** 标签  */
    private String label;

    /** 创建人 */
    private String createByUid;

    /** 更新人 */
    private String updateByUid;

    /** 收藏数 */
    private Integer collectCount;

    /** 点击数 */
    private Integer clickCount;

    /** 备注 */
    private String remark;

}
