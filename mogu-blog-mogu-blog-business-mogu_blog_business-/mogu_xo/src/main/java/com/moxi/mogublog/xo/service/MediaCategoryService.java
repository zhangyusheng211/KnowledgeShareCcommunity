package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MediaCategory;
import com.moxi.mogublog.commons.vo.MediaCategoryVO;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 媒资分类Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaCategoryService extends SuperService<MediaCategory> {
    IPage<MediaCategory> getPageList(MediaCategoryVO mediaCategoryVO);
}
