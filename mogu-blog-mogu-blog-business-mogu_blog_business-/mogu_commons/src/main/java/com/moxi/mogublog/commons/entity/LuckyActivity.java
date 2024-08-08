package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 抽奖活动表
 *
 * @author 陌溪
 */
@Data
@TableName("t_lucky_activity")
public class LuckyActivity extends SuperEntity<LuckyActivity> {

    /**
     * 管理员uid
     */
    private String adminUid;

    /**
     * 活动图uid
     */
    private String fileUid;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动描述
     */
    private String summary;

    /**
     * 抽奖规则
     */
    private String luckyRuleUid;

    /**
     * 赞助配置
     */
    private String sponsorConfig;

    /**
     * 活动状态( 0:未开始，1:进行中，2:已结束)
     */
    private int activityStatus;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 是否发布(1:是，0:否)
     */
    private String isPublish;

    /**
     * 赞助商列表
     */
    private String sponsorList;

    /**
     * 封面图
     */
    @TableField(exist = false)
    private String photoUrl;

    /**
     * 奖品名称
     */
    @TableField(exist = false)
    private List<LuckyAwardItem> luckyAwardItemList;

    /**
     * 抽奖规则
     */
    @TableField(exist = false)
    private LuckyRule luckyRule;
}
