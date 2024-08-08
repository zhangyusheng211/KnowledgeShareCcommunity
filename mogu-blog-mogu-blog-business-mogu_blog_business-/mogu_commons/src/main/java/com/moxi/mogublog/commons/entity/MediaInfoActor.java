package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 媒资演员关联对象 t_media_info_actor
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_info_actor")
public class MediaInfoActor extends SuperEntity<MediaInfoActor>
{

    /** 演员uid */
    private String actorUid;

    /** 媒资uid */
    private String mediaInfoUid;

    /** 主演actor  演员 */
    private String type;

    /** 备注 */
    private String remark;


    /** 姓名 */
    @TableField(exist = false)
    private String name;

    /** 头像 */
    @TableField(exist = false)
    private String avatar;

    /** 标签 */
    @TableField(exist = false)
    private String label;

}
