package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Collect;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Map;

/**
 * 收藏表 服务类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
public interface CollectService extends SuperService<Collect> {

    /**
     * 获取收藏数
     *
     * @param collectVo
     * @return
     */
    Map<String, Object> getCollectCount(CollectVO collectVo);

    /**
     * 收藏
     *
     * @param collectVo
     * @return
     */
    String addCollect(CollectVO collectVo);

    /**
     * 取消收藏
     *
     * @param collectVo
     * @return
     */
    String deleteCollect(CollectVO collectVo);

    /**
     * 获取收藏列表
     *
     * @param collectVo
     * @return
     */
    IPage<Collect> getPageList(CollectVO collectVo);

    /**
     * 收藏刷数
     *
     * @return
     */
    String refreshCollect();

    /**
     * 获取用户收藏数
     *
     * @param collectVo
     * @return
     */
    Integer getUserCollectCount(CollectVO collectVo);
}
