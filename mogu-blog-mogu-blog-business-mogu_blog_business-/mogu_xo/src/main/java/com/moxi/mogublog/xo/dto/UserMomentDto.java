package com.moxi.mogublog.xo.dto;

import com.moxi.mogublog.commons.entity.UserMoment;
import lombok.Data;

/**
 * 用户动态对象
 *
 * @author 遇见
 */
@Data
public class UserMomentDto extends UserMoment {
    /**
     * 回复数
     */
    private Integer replyCount;
    /**
     * 点赞数
     */
    private Integer praiseCount;
    /**
     * 点踩数
     */
    private Integer opposeCount;
    /**
     * 收藏数
     */
    private Integer collectCount;
    /**
     * 转发数
     * todo 暂未统计
     */
    private Integer relayCount;

}
