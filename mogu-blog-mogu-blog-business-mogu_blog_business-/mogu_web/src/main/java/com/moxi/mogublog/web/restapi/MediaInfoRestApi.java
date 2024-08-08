package com.moxi.mogublog.web.restapi;

import cn.hutool.core.collection.CollectionUtil;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.commons.entity.MediaVideo;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.xo.service.MediaInfoService;
import com.moxi.mogublog.xo.service.MediaVideoService;
import com.moxi.mogublog.commons.vo.MediaInfoVO;
import com.moxi.mogublog.commons.vo.MediaVideoVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒资信息表 RestApi
 *
 * @author thh
 * @date 2021-12-14
 */

@RestController
@RequestMapping("/media/info")
@Api(value = "媒资信息相关接口", tags = {"媒资信息相关接口"})
@Slf4j
public class MediaInfoRestApi {

    @Resource
    private MediaInfoService mediaInfoService;

    @Autowired
    private MediaVideoService mediaVideoService;

    @ApiOperation(value = "获取媒资信息列表", notes = "获取媒资信息列表", response = String.class)
    @PostMapping("/list")
    public String list(@Validated({GetList.class}) @RequestBody MediaInfoVO mediaInfoVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        mediaInfoVO.setStatus(1);
        return ResultUtil.successWithData(mediaInfoService.getPageList(mediaInfoVO));
    }


    /**
     * 获取媒资信息
     */
    @ApiOperation(value = "获取媒资信息", notes = "获取媒资信息", response = String.class)
    @GetMapping(value = "/{uid}")
    public String getInfo(@PathVariable("uid") String uid) {
        MediaInfo mediaInfo = mediaInfoService.getMediaInfo(uid);
        if (mediaInfo != null) {
            return ResultUtil.successWithData(mediaInfo);
        }
        return ResultUtil.errorWithMessage("获取的媒资信息已被删除！");
    }


    /**
     * 获取视频信息
     */
    @BussinessLog(value = "点击媒咨详情", behavior = EBehavior.MEDIA_VIDEO)
    @ApiOperation(value = "获取视频信息", notes = "获取视频信息", response = String.class)
    @PostMapping(value = "/video")
    public String getMediaVideoDetail(@RequestBody MediaVideoVO mediaVideoVO) {
        String videoId = mediaVideoVO.getUid();
        String mediaInfoUid = mediaVideoVO.getMediaInfoUid();
        MediaInfo mediaInfo;
        MediaVideo video = null;
        // 1.infoUid 不为空 videoId为空
        if (StringUtils.isNotEmpty(mediaInfoUid) && StringUtils.isEmpty(videoId)) {
            mediaInfo = mediaInfoService.getMediaInfo(mediaInfoUid);
        } else if (StringUtils.isNotEmpty(mediaInfoUid) && StringUtils.isNotEmpty(videoId)) {
            // 2.infoUid 不为空 videoId不为空
            mediaInfo = mediaInfoService.getMediaInfo(mediaInfoUid);
        } else if (StringUtils.isEmpty(mediaInfoUid) && StringUtils.isNotEmpty(videoId)) {
            //3.infoUid为空 video不为空
            video = mediaVideoService.getById(videoId);
            mediaInfoUid = video.getMediaInfoUid();
            mediaInfo = mediaInfoService.getMediaInfo(mediaInfoUid);
        } else {
            throw new BusinessException("数据异常");
        }

        // 加载媒资视频列表
        List<MediaVideo> mediaVideoList = mediaInfo.getVideoList();
        for (MediaVideo mediaVideo : mediaVideoList) {
            if (mediaVideo.getUid().equals(videoId)) {
                video = mediaVideo;
            }
        }
        if (video == null && CollectionUtil.isNotEmpty(mediaVideoList)) {
            video = mediaVideoList.get(0);
        }

        Map<String, Object> data = new HashMap<>(10);
        data.put("video", video);
        data.put("mediaInfo", mediaInfo);
        return ResultUtil.successWithData(data);
    }

}
