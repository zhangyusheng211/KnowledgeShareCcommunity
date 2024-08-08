package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 用户权益记录表；包括：VIP特权、签到卡、兑换的资源；
 * @author 陌溪
 * @since 2021年12月18日23:06:48
 */
@Data
@TableName("t_user_equity_record")
public class UserEquityRecord extends SuperEntity<UserEquityRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 权益UID（如对应资源的uid）
     */
    private String equityUid;

    /**
     * 权益类型(1:VIP特权, 2:签到卡, 3:兑换的资源)
     */
    private String EquityType;

    /**
     * 是否永久生效（0：否，1：是）
     */
    private String isPermanent;

    /**
     * 使用状态（0：未使用，1：已使用，2：已过期）
     */
    private String useStatus;

    /**
     * 权益生效时间
     */
    private Date startTime;

    /**
     * 权益截止时间
     */
    private Date endTime;

    // 以下字段不存入数据库
    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;


}
