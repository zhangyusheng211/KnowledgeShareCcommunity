package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.schema.VipExtraConfig;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 会员配置表
 * @author 陌溪
 * @since 2024年5月26日08:24:56
 */
@Data
@TableName("t_vip_config")
public class VipConfig extends SuperEntity<VipConfig> {

    // 会员名称
    private String name;

    // 会员简介
    private String summary;

    // 价格（分）
    private int price;

    // 折扣价格（分）
    private int discountPrice;

    // 会员生效天数
    private int effectDay;

    // 收费模式 1:免费 2:付费 3:折扣价 4:会员免费
    private Integer chargeType;

    // 支付类型：1:积分支付，2: 现金支付
    private int payType;

    // 折扣开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountStartTime;

    // 折扣结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountEndTime;

    // 额外配置
    private String extra;

    // 备注
    private String remark;

    // 排序字段
    private int sort;

    // 是否发布(1:是，0:否)
    private int isPublish;

    /**
     * 用户标签
     */
    private int userTag;

    // 以下字段不存入数据库

    /**
     * 额外配置项
     */
    @TableField(exist = false)
    private VipExtraConfig vipExtraConfig;

}
