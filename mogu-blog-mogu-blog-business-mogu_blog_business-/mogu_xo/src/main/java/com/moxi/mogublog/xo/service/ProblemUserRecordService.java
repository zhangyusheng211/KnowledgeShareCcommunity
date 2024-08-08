package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.ProblemUserRecord;
import com.moxi.mogublog.commons.vo.ProblemUserRecordVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Map;

/**
 * 问题用户记录表 服务类
 *
 * @author 陌溪
 * @date 2022年3月12日09:03:31
 */
public interface ProblemUserRecordService extends SuperService<ProblemUserRecord> {

    /**
     * 增加做题记录
     *
     * @param problemUserRecordVO
     * @return
     */
    String addProblemUserRecord(ProblemUserRecordVO problemUserRecordVO);

    /**
     * 获取做题数
     * @param problemUserRecordVO
     * @return
     */
    int getProblemUserRecordsCount(ProblemUserRecordVO problemUserRecordVO);

    /**
     * 获取用户做题记录
     * @return
     */
    Map<String, Object> getUserProblemRecords();
}
