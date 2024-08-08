package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.LuckyRecord;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
import com.moxi.mogublog.commons.vo.LuckyRuleVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 抽奖记录表 服务类
 *
 * @author 陌溪
 * @date 2023年7月16日14:47:36
 */
public interface LuckyRecordService extends SuperService<LuckyRecord> {
    /**
     * 获取抽奖记录列表
     *
     * @param luckyRecordVO
     * @return
     */
    IPage<LuckyRecord> getPageList(LuckyRecordVO luckyRecordVO);

    /**
     * 新增抽奖记录
     *
     * @param luckyRecordVO
     */
    String addLuckyRecord(LuckyRecordVO luckyRecordVO);

    /**
     * 编辑抽奖记录
     *
     * @param luckyRecordVO
     */
    String editLuckyRecord(LuckyRecordVO luckyRecordVO);

    /**
     * 批量删除抽奖记录
     *
     * @param luckyRecordVOList
     */
    String deleteBatchLuckyRecord(List<LuckyRecordVO> luckyRecordVOList);


    /**
     *
     * @param luckyRecordVO
     * @return
     */
    int getLuckyRecordCount(LuckyRecordVO luckyRecordVO);
}
