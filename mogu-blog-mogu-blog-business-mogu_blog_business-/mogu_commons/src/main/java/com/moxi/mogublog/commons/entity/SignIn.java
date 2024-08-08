package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 用户签到表
 * @author 陌溪
 * @since 2021年11月27日16:34:27
 */
@Data
@TableName("t_sign_in")
public class SignIn extends SuperEntity<SignIn> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 上次签到时间, 格式：20211127
     */
    private String latestSignDate;

    /**
     * 连续签到时间
     */
    private int consecutiveSignCount;

    // 以下字段不存入数据库

    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;
}
