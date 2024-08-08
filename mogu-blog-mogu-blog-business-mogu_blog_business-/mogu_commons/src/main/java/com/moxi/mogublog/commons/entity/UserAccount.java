package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户账号管理
 * @author 遇见
 */
@Data
@TableName("t_user_account")
public class UserAccount extends SuperEntity<UserAccount> {
    /**
     * 系统用户Uid
     */
    private String userUid;
    /**
     * 第三方账号id
     */
    private String accountId;
    /**
     * 第三方账号名称
     */
    private String userName;

    /**
     * 第三方用户昵称
     */
    private String nickName;
    /**
     * 第三方账号类型
     */
    private String source;

    /**
     * 绑定时间
     */
    private Date bindTime;

    /**
     * 解绑时间
     */
    private Date unbindTime;

    /**
     * 头型
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别
     */
    private String gender;

    /**
     * 简介
     */
    private String summary;

    /**
     * 账号来源描述
     */
    @TableField(exist = false)
    private String sourceDesc;
}
