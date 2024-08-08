package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.UserEquityRecord;
import com.moxi.mogublog.commons.vo.UserEquityRecordVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 用户权益表 服务类
 *
 * @author 陌溪
 * @date 2021年12月18日23:21:43
 */
public interface UserEquityRecordService extends SuperService<UserEquityRecord> {

    /**
     * 获取用户权益列表（分页）
     *
     * @param userEquityRecordVO
     * @return
     */
    IPage<UserEquityRecord> getPageList(UserEquityRecordVO userEquityRecordVO);

    /**
     * 获取用户权益列表
     *
     * @param userEquityRecordVO
     * @return
     */
    List<UserEquityRecord> getList(UserEquityRecordVO userEquityRecordVO);

    /**
     * 获取用户权益数
     *
     * @param userEquityRecordVO
     * @return
     */
    Integer getCount(UserEquityRecordVO userEquityRecordVO);

    /**
     * 消耗用户签到卡
     *
     * @param userUid
     * @param cardCount 消耗签到卡数量
     */
    Boolean useSignInCards(String userUid, Long cardCount);

    /**
     * 向指定用户发放签到卡
     *
     * @param userUid
     * @param cardCount
     * @return
     */
    Boolean sendSignInCards(String userUid, int cardCount);

    /**
     * 添加用户权益资源
     * @param userEquityRecordVO
     * @return
     */
    void addUserEquityRecord(UserEquityRecordVO userEquityRecordVO);

    /**
     * 批量发放签到卡
     *
     * @return
     */
    Boolean batchSendSignInCard();


}
