package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MediaTag;
import com.moxi.mogublog.commons.vo.MediaTagVO;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 媒资标签Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaTagService extends SuperService<MediaTag> {
    IPage<MediaTag> getPageList(MediaTagVO mediaTagVO);
}
