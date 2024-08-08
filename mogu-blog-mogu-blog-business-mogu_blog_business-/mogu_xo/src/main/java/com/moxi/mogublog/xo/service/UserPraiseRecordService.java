package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.UserPraiseRecord;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Map;

/**
 * 用户点赞记录表
 *
 * @author 遇见
 */
public interface UserPraiseRecordService extends SuperService<UserPraiseRecord> {
    /**
     * 有效点赞/踩数
     *
     * @return
     */
    Map<String, Object> praisedCount(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 一周内点赞量
     *
     * @param userUid
     * @return
     */
    Integer getPraiseCountByWeek(String userUid);

    /**
     * 获取点赞数
     *
     * @param userPraiseRecordVO
     * @return
     */
    Integer getPraiseCount(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 刷数
     *
     * @return
     */
    String refreshPraise();


}
