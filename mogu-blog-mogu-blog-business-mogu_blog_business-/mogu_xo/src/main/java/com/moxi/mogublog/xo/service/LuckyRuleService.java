package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.LuckyRule;
import com.moxi.mogublog.commons.vo.LuckyRuleVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 抽奖规则表 服务类
 *
 * @author 陌溪
 * @date 2023年7月16日14:47:36
 */
public interface LuckyRuleService extends SuperService<LuckyRule> {
    /**
     * 获取抽奖规则列表
     *
     * @param luckyRuleVO
     * @return
     */
    IPage<LuckyRule> getPageList(LuckyRuleVO luckyRuleVO);

    /**
     * 新增抽奖规则
     *
     * @param luckyRuleVO
     */
    String addLuckyRule(LuckyRuleVO luckyRuleVO);

    /**
     * 编辑抽奖规则
     *
     * @param luckyRuleVO
     */
    String editLuckyRule(LuckyRuleVO luckyRuleVO);

    /**
     * 批量删除抽奖规则
     *
     * @param luckyRuleVOList
     */
    String deleteBatchLuckyRule(List<LuckyRuleVO> luckyRuleVOList);
}
