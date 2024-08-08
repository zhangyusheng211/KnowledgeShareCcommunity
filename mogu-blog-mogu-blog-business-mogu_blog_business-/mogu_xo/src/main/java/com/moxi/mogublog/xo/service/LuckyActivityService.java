package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.LuckyActivity;
import com.moxi.mogublog.commons.vo.LuckyActivityVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 抽奖活动表 服务类
 *
 * @author 陌溪
 * @date 2023年7月16日14:47:36
 */
public interface LuckyActivityService extends SuperService<LuckyActivity> {
    /**
     * 获取抽奖活动列表
     *
     * @param luckyActivityVO
     * @return
     */
    IPage<LuckyActivity> getPageList(LuckyActivityVO luckyActivityVO);

    /**
     * 新增抽奖活动
     *
     * @param luckyActivityVO
     */
    String addLuckyActivity(LuckyActivityVO luckyActivityVO);

    /**
     * 编辑抽奖活动
     *
     * @param luckyActivityVO
     */
    String editLuckyActivity(LuckyActivityVO luckyActivityVO);

    /**
     * 批量删除抽奖活动
     *
     * @param luckyActivityVOList
     */
    String deleteBatchLuckyActivity(List<LuckyActivityVO> luckyActivityVOList);

    /**
     * 开始抽奖
     * @return
     */
    String lucky(LuckyActivityVO luckyActivityVO);
}
