package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * 用户权益记录表VO
 *
 * @author 陌溪
 * @since 2021年12月18日23:06:48
 */
@Data
public class UserEquityRecordVO extends BaseVO<UserEquityRecordVO> {

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

    /**
     * 权益发放的个数
     */
    private int count;
}
