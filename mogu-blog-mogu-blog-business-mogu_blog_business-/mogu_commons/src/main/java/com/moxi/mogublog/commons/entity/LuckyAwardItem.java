package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;

/**
 * 抽奖奖励项表
 * @author 陌溪
 */
@Data
@TableName("t_lucky_award_item")
public class LuckyAwardItem extends SuperEntity<LuckyAwardItem> {

    /**
     * 抽奖活动uid
     */
    private String luckyActivityUid;

    /**
     * 奖品uid
     */
    private String awardProductUid;

    /**
     * 奖励发放的个数【默认是1，如果涉及金额类，或者积分类，可以设置多个】
     */
    private int count;

    /**
     * 奖项数量【本次活动累积投入的数量】
     */
    private int total;

    /**
     * 剩余数量【抽取后还剩余的数量】
     */
    private int residueTotal;

    /**
     * 名称：一等奖、二等奖
     */
    private String name;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 中奖后弹窗
     */
    private int openWindow;

    /**
     * 是否放回【抽奖后该项是否放回奖池中】
     */
    private int putBack;

    /**
     * 是否允许重复中奖
     */
    private int allowRepetition;

    @TableField(exist = false)
    private AwardProduct awardProduct;
}
