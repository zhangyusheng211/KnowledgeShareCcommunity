package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 抽奖奖励项表
 * @author 陌溪
 */
@Data
@TableName("t_lucky_rule")
public class LuckyRule extends SuperEntity<LuckyRule> {

    /**
     * 规则名称
     */
    private String name;

    /**
     * 最大可参与次数 -1不限制
     */
    private int maxLuckyCount;

    /**
     * 最大可中奖次数 -1不限制
     */
    private int maxAwardCount;

    /**
     * 每日可抽奖次数 -1不限制
     */
    private int dayLuckyCount;

    /**
     * 抽奖消耗积分数量
     */
    private int costCredits;

}
