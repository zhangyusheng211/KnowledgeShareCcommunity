package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 抽奖奖励项表
 * @author 陌溪
 */
@Data
public class LuckyAwardItemVO extends BaseVO<LuckyAwardItemVO> {

    /**
     * 抽奖活动uid
     */
    private String luckyActivityUid;

    /**
     * 奖品uid
     */
    private String awardProductUid;

    /**
     * 奖项数量
     */
    private int total;

    /**
     * 名称：一等奖、二等奖
     */
    private String name;

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

    /**
     * 活动列表
     */
    private List<String> luckyActivityUidList;
}
