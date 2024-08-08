package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 抽奖奖励项表
 * @author 陌溪
 */
@Data
@TableName("t_lucky_record")
public class LuckyRecord extends SuperEntity<LuckyRecord> {

    /**
     * 抽奖活动uid
     */
    private String luckyActivityUid;

    /**
     * 奖品uid
     */
    private String awardProductUid;

    /**
     * 抽奖项UID
     */
    private String luckyAwardItemUid;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 抽奖状态(0:未中奖，1:已中奖)
     */
    private int luckyStatus;

    /**
     * 兑换状态(0:未兑换，1:兑换中，2:已兑换)
     */
    private int exchangeStatus;

    /**
     * 抽奖人
     */
    @TableField(exist = false)
    private User user;

    /**
     * 抽奖活动
     */
    @TableField(exist = false)
    private LuckyActivity luckyActivity;

    /**
     * 抽奖项
     */
    @TableField(exist = false)
    private LuckyAwardItem luckyAwardItem;

    /**
     * 奖品
     */
    @TableField(exist = false)
    private AwardProduct awardProduct;
}
