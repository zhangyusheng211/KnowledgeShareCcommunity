package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.LuckyAwardItem;
import com.moxi.mogublog.commons.vo.LuckyAwardItemVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 抽奖项表 服务类
 *
 * @author 陌溪
 * @date 2023年7月16日14:47:36
 */
public interface LuckyAwardItemService extends SuperService<LuckyAwardItem> {
    /**
     * 获取抽奖项列表
     *
     * @param luckyAwardItemVO
     * @return
     */
    IPage<LuckyAwardItem> getPageList(LuckyAwardItemVO luckyAwardItemVO);

    /**
     * 新增抽奖项
     *
     * @param luckyAwardItemVO
     */
    String addLuckyAwardItem(LuckyAwardItemVO luckyAwardItemVO);

    /**
     * 编辑抽奖项
     *
     * @param luckyAwardItemVO
     */
    String editLuckyAwardItem(LuckyAwardItemVO luckyAwardItemVO);

    /**
     * 批量删除抽奖项
     *
     * @param luckyAwardItemVOList
     */
    String deleteBatchLuckyAwardItem(List<LuckyAwardItemVO> luckyAwardItemVOList);
}
