package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 用户签到表
 *
 * @author 陌溪
 * @since 2021年11月27日16:34:27
 */
@Data
public class SignInRecordVO extends BaseVO<SignInRecordVO> {

    private static final long serialVersionUID = 1L;
    /**
     * 签到日期
     */
    private String signDate;
    /**
     * 是否签到
     */
    private Integer isSignIn;
    /**
     * 今日是否签到
     */
    private Boolean todayIsSignIn;
    /**
     * 用户Uid
     */
    private String userUid;
    /**
     * 最早连签时间
     */
    private String minDate;
    /**
     * 截止时间
     */
    private String maxDate;
    /**
     * 连续签到天数
     */
    private Integer continuousDays;
    /**
     * 已签到天
     */
    private List<String> signedDataList;
    /**
     * 补签卡数量
     */
    private Integer retroactiveCard;
    /**
     * 补签date
     */
    private List<String> retroactiveDate;
    /**
     * 万历年节假日信息
     */
    private List<DayVO> calendarList;

}
