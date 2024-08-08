package com.moxi.mogublog.xo.manager;

import com.moxi.mougblog.base.enums.EResourceType;

import java.util.List;

/**
 * 通用模块模块
 *
 * @author 陌溪
 */
public interface CommonManager {

    /**
     * 通过资源类型获取用户uid
     *
     * @param resourceType
     * @param resourceUid
     * @return
     */
    String getUserUidByResource(EResourceType resourceType, String resourceUid);

    /**
     * 根据资源推送SEO
     * @param resourceType
     * @param resourceUid
     * @return
     */
    String pushSEOByResource(EResourceType resourceType, List<String> resourceUid);
}
