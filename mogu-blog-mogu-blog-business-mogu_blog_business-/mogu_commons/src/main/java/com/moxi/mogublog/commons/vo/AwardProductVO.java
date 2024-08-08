package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 抽奖奖品表
 * @author 陌溪
 */
@Data
public class AwardProductVO extends BaseVO<AwardProductVO> {

    /**
     * 奖品图uid
     */
    private String fileUid;

    /**
     * 奖品名称
     */
    private String name;

    /**
     * 权益类型：签到卡、现金、VIP体验卡
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
     *
     */
    private List<String> awardProductUidList;
}
