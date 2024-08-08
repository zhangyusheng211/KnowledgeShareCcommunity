package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.MediaVideo;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Collection;
import java.util.List;

/**
 * 媒资视频Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaVideoService extends SuperService<MediaVideo> {

    /**
     * 媒资填充视频
     * @return
     */
    List<MediaVideo> convertMediaVideoList(Collection<MediaVideo> mediaVideos);
}
