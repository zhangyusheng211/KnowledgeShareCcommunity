package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.HotSearch;
import com.moxi.mogublog.commons.schema.OutsideHotSearchItem;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 热搜
 *
 * @author 陌溪
 * @date 2022年6月17日22:23:22
 */
public interface HotSerchService extends SuperService<HotSearch> {

    /**
     * 获取热搜列表
     *
     * @return
     */
    List<HotSearch> getHotSearchList();

    /**
     * 获取外部热搜列表
     *
     * @return
     */
    List<OutsideHotSearchItem> getOutsideHotSearch(String type);

    /**
     * 新增热搜
     *
     * @param keyword
     * @return
     */
    String addHotSearch(String keyword);
}
