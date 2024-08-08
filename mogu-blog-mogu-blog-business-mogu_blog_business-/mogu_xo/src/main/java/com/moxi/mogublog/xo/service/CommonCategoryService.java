package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.CommonCategory;
import com.moxi.mogublog.commons.vo.CommonCategoryVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 通用分类表服务类
 *
 * @author 陌溪
 * @date 2024年6月1日21:54:29
 */
public interface CommonCategoryService extends SuperService<CommonCategory> {
    /**
     * 获取通用分类列表
     *
     * @param commonCategoryVO
     * @return
     */
    IPage<CommonCategory> getPageList(CommonCategoryVO commonCategoryVO);

    /**
     * 新增通用分类
     *
     * @param commonCategoryVO
     */
    String addCommonCategory(CommonCategoryVO commonCategoryVO);

    /**
     * 编辑通用分类
     *
     * @param commonCategoryVO
     */
    String editCommonCategory(CommonCategoryVO commonCategoryVO);

    /**
     * 批量删除通用分类
     *
     * @param commonCategoryVOList
     */
    String deleteBatchCommonCategory(List<CommonCategoryVO> commonCategoryVOList);
}
