package com.moxi.mogublog.xo.service;


import com.moxi.mogublog.commons.entity.SignInRecord;
import com.moxi.mogublog.commons.vo.SignInRecordVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 用户签到表 服务类
 *
 * @author 陌溪
 * @date 2021年11月27日16:38:41
 */
public interface SignInRecordService extends SuperService<SignInRecord> {
    /**
     * 签到 并返回签到后连续的时间段
     *
     * @param userUid
     * @param retroactiveDate
     * @return
     */
    Integer retroactive(String userUid, List<String> retroactiveDate);

    /**
     * 补签
     *
     * @param signInVO
     * @return
     */
    String retroactive(SignInRecordVO signInVO);

    /**
     * 根据用户uid查询用户当月签到情况
     *
     * @param userUid   用户Uid
     * @param hasSignIn 已签
     * @param dateKey
     * @return
     */
    List<SignInRecordVO> userSignRecordList(String userUid, Boolean hasSignIn, String dateKey);

    /**
     * 用户签到
     *
     * @param userUid 用户Uid
     * @return
     */
    String userSignIn(String userUid);

    /**
     * 获取连续签到次数
     *
     * @param userUid
     * @return
     */
    Integer getSignInCount(String userUid);


    /**
     * 获取用户累积签到数
     *
     * @param userUid
     * @return
     */
    Integer getSignInSumCount(String userUid);


    /**
     * 用户当月签到信息
     *
     * @param refresh
     * @param dateStr
     * @return
     */
    String signDataByUserUid(Boolean refresh, String dateStr);
}
