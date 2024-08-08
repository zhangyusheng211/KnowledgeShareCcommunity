package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 用户点赞记录表
 * @author 遇见
 */
@Data
@TableName("t_user_praise_record")
public class UserPraiseRecord  extends SuperEntity<UserPraiseRecord> {
    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;
    /**
     * 资源uid
     */
    @TableField("resource_uid")
    private String resourceUid;
    /**
     * 用户uid
     */
    @TableField("user_uid")
    private String userUid;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 被点赞/点踩用户uid
     */
    @TableField("to_user_uid")
    private String toUserUid;

    /**
     * 点赞类型
     */
    @TableField("praise_type")
    private String praiseType;
}
