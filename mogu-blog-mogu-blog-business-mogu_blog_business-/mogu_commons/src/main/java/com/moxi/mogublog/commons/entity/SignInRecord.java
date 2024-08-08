package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户签到表
 * @author 陌溪
 * @since 2021年11月27日16:34:27
 */
@Data
@TableName("t_sign_in_record")
public class SignInRecord extends SuperEntity<SignInRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 上次签到时间, 格式：20211127
     */
    private String signDate;

    /**
     * 签到类型(0:每日签到，1:补签)
     */
    private Integer signType;


    // 以下字段不存入数据库
    @TableField(exist = false)
    private Integer isSignIn;


    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;

    public SignInRecord(String userUid, String signDate,Integer type) {
        this.userUid = userUid;
        this.signDate =signDate;
        this.signType =type;
        this.setCreateTime(new Date());
    }

    public SignInRecord(){

    }
}
