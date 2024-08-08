package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 抽奖记录表
 * @author 陌溪
 */
@Data
public class LuckyRecordVO extends BaseVO<LuckyRecordVO> {
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
    private Integer luckyStatus;

    /**
     * 开始时间
     */
    private Integer exchangeStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
