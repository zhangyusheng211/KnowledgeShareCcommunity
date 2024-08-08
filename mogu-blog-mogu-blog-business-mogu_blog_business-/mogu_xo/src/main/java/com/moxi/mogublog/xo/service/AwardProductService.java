package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.AwardProduct;
import com.moxi.mogublog.commons.entity.AwardProduct;
import com.moxi.mogublog.commons.vo.AwardProductVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 奖品表 服务类
 *
 * @author 陌溪
 * @date 2023年7月16日14:47:36
 */
public interface AwardProductService extends SuperService<AwardProduct> {
    /**
     * 获取奖品列表
     *
     * @param awardProductVO
     * @return
     */
    IPage<AwardProduct> getPageList(AwardProductVO awardProductVO);

    /**
     * 新增奖品
     *
     * @param awardProductVO
     */
    String addAwardProduct(AwardProductVO awardProductVO);

    /**
     * 编辑奖品
     *
     * @param awardProductVO
     */
    String editAwardProduct(AwardProductVO awardProductVO);

    /**
     * 批量删除奖品
     *
     * @param awardProductVOList
     */
    String deleteBatchAwardProduct(List<AwardProductVO> awardProductVOList);
}
