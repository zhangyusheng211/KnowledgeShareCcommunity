package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.commons.schema.TransformMediaRequest;
import com.moxi.mogublog.commons.vo.MediaInfoVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 媒资信息Service接口
 *
 * @author thh
 * @date 2021-12-14
 */
public interface MediaInfoService extends SuperService<MediaInfo> {
    /**
     * 获取媒资信息
     *
     * @param mediaInfoVO
     * @return
     */
    public IPage<MediaInfo> getPageList(MediaInfoVO mediaInfoVO);

    /**
     * 新增反馈
     *
     * @param mediaInfoVO
     */
    public String addMediaInfo(MediaInfoVO mediaInfoVO);

    /**
     * 编辑反馈
     *
     * @param mediaInfoVO
     */
    public String editMediaInfo(MediaInfoVO mediaInfoVO);

    /**
     * 批量删除反馈
     *
     * @param mediaInfoVOList
     */
    public String deleteBatchMediaInfo(List<MediaInfoVO> mediaInfoVOList);

    /**
     * 获取媒资明细信息
     *
     * @param uid
     * @return
     */
    MediaInfo getMediaInfo(String uid);

    /**
     * 转化媒资信息
     *
     * @param transformMediaRequest
     * @return
     */
    String transformMedia(TransformMediaRequest transformMediaRequest);
}
