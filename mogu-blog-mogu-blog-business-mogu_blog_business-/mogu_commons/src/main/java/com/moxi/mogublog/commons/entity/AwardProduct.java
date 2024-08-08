package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 抽奖活动表
 * @author 陌溪
 */
@Data
@TableName("t_award_product")
public class AwardProduct extends SuperEntity<AwardProduct> {

    /**
     * 奖品图uid
     */
    private String fileUid;

    /**
     * 奖品名称
     */
    private String name;

    /**
     * 奖品类型：签到卡、现金、VIP体验卡
     */
    private String awardType;

    /**
     * 库存数量
     */
    private int total;

    /**
     * 是否发布(1:是，0:否)
     */
    private int isPublish;

    /**
     * 封面图
     */
    @TableField(exist = false)
    private String photoUrl;
}
