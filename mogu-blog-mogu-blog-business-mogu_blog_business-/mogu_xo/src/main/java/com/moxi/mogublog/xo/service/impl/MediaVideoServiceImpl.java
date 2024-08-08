package com.moxi.mogublog.xo.service.impl;

import com.moxi.mogublog.commons.entity.MediaVideo;
import com.moxi.mogublog.commons.schema.FileDto;
import com.moxi.mogublog.xo.mapper.MediaVideoMapper;
import com.moxi.mogublog.xo.service.MediaVideoService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.attribute.standard.Media;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 媒资视频Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
public class MediaVideoServiceImpl extends SuperServiceImpl<MediaVideoMapper, MediaVideo> implements MediaVideoService {

    @Resource
    private FileFeignUtil fileFeignUtil;

    @Override
    public List<MediaVideo> convertMediaVideoList(Collection<MediaVideo> mediaVideos) {

        List<String> fileUidList = mediaVideos.stream().map(MediaVideo::getFileUid).collect(Collectors.toList());
        Map<String, FileDto> fileDtoMap = new HashMap<>();
        if (fileUidList.size() > 0) {
            fileDtoMap = fileFeignUtil.fileUidToFileDtoMap(fileUidList);
        }
        List<MediaVideo> mediaVideoList = new ArrayList<>();
        for (MediaVideo mediaVideo : mediaVideos) {
            FileDto fileDto = fileDtoMap.get(mediaVideo.getFileUid());
            if (fileDto != null) {
                mediaVideo.setM3u8Url(fileDto.getM3u8Url());
            }
            mediaVideoList.add(mediaVideo);
        }
        return mediaVideoList;
    }
}
