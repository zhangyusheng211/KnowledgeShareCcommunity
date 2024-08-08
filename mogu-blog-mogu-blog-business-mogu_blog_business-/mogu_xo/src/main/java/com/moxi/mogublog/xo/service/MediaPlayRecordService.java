package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MediaPlayRecord;
import com.moxi.mogublog.commons.vo.MediaPlayRecordVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 播放记录Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaPlayRecordService extends SuperService<MediaPlayRecord> {
    /**
     * 查询播放记录列表
     *
     * @param mediaPlayRecordVO 播放记录
     * @return 播放记录集合
     */
    IPage<MediaPlayRecord> getPageList(MediaPlayRecordVO mediaPlayRecordVO);

    /**
     * 新增和更新播放记录
     *
     * @param mediaPlayRecordVO 播放记录
     * @return 结果
     */
    String insertOrUpdateMediaPlayRecord(MediaPlayRecordVO mediaPlayRecordVO);


    /**
     * @param videoId 视频id  userId用户id
     * @return 结果
     */
    MediaPlayRecord selectPlayRecordsByVideoId(String videoId);
}
