package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.SignInRecord;
import com.moxi.mogublog.commons.vo.SignInRecordVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签到记录表 Mapper 接口
 *
 * @author 遇见
 */
public interface SignInRecordMapper extends SuperMapper<SignInRecord> {
    /**
     * 查询用户当月签到情况
     *
     * @param userUid 用户Uid
     * @param dateKey 时间   as    2021-10
     * @return
     */
    List<SignInRecordVO> userSignRecordList(@Param("userUid") String userUid, @Param("dateKey") String dateKey);

    /**
     * 查询连续签到天数
     *
     * @param userUid
     * @param day
     * @return
     */
    SignInRecordVO querySignContinuousDays(@Param("userUid") String userUid, @Param("day") String day);
}
