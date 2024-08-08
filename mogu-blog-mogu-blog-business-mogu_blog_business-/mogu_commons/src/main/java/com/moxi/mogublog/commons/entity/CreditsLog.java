package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 积分日志表
 * @author 陌溪
 * @since 2021年11月27日16:34:27
 */
@Data
@TableName("t_credits_log")
public class CreditsLog extends SuperEntity<CreditsLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String adminUid;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 操作的对象uid
     */
    private String resourceUid;

    /**
     * 动作码
     */
    private String actionCode;

    /**
     * 动作名称
     */
    private String actionName;

    /**
     * 旧的积分数
     */
    private Integer oldCredits;

    /**
     * 新的积分数
     */
    private Integer newCredits;
    /**
     * 变更的积分
     */
    private Integer changeCredits;

    /**
     * 备注
     */
    private String remark;

    // 以下字段不存入数据库
    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 积分合计
     */
    @TableField(exist = false)
    private String sumCredits;
}
