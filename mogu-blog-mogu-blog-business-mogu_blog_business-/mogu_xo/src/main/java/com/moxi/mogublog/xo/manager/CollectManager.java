package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.vo.CollectVO;

import java.util.Map;

/**
 * 收藏模块
 *
 * @author 陌溪
 * @date 2022年3月27日23:20:32
 */
public interface CollectManager {
    /**
     * 获取收藏数
     *
     * @param collectVo
     * @return
     */
    Map<String, Object> getCollectCount(CollectVO collectVo);
}
