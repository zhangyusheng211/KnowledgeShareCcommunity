package com.moxi.mogublog.commons.vo;

import lombok.Data;

/**
 * 用户连续操作【连续发文、连续发评论等。。。】
 *
 * @author 陌溪
 * @since 2023年1月2日23:14:27
 */
@Data
public class ContinuousDayVO {

    /**
     * 用户Uid
     */
    private String userUid;
    /**
     * 最早时间
     */
    private String minDate;
    /**
     * 截止时间
     */
    private String maxDate;
    /**
     * 连续天数
     */
    private Integer continuousDays;
}
