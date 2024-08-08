package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.entity.LuckyAwardItem;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 抽奖活动表
 *
 * @author 陌溪
 */
@Data
public class LuckyActivityVO extends BaseVO<LuckyActivityVO> {

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 是否发布(1:是，0:否)
     */
    private int isPublish;

    /**
     * 奖励项列表
     */
    private List<LuckyAwardItem> luckyAwardItemList;

    /**
     * 抽奖活动ID列表
     */
    private List<String> luckyActivityUidList;

    /**
     * 赞助商列表
     */
//    private List<SponsorItem> sponsorList;
    private String sponsorList;

}
