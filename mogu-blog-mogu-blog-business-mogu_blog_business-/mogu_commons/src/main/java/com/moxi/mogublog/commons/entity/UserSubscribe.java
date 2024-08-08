package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 用户订阅表；代表用户订阅了什么资源【用于后续资源更新推送消息】
 *
 * @author 陌溪
 * @date 2023年12月23日10:11:40
 */
@Data
@TableName("t_user_subscribe")
public class UserSubscribe extends SuperEntity<UserSubscribe> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 资源UID
     */
    private String resourceUid;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 备注
     */
    private String remark;
}
