package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.commons.schema.TransformMediaRequest;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaInfoService;
import com.moxi.mogublog.commons.vo.MediaInfoVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @ApiOperation(value = "获取媒资信息列表", notes = "获取媒资信息列表", response = String.class)
    @PostMapping("/list")
    public String list(@Validated({GetList.class}) @RequestBody MediaInfoVO MediaInfoVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(mediaInfoService.getPageList(MediaInfoVO));
    }

    /**
     * 获取媒资信息
     */
    @ApiOperation(value = "获取媒资信息", notes = "获取媒资信息", response = String.class)
    @PostMapping(value = "/info")
    public String getInfo(@RequestBody MediaInfoVO mediaInfoVO) {
        MediaInfo mediaInfo = mediaInfoService.getMediaInfo(mediaInfoVO.getUid());
        if (mediaInfo != null) {
            return ResultUtil.successWithData(mediaInfo);
        }
        return ResultUtil.errorWithMessage("获取的媒资信息已被删除！");
    }

    @AvoidRepeatableCommit
    @OperationLogger(value = "增加媒资信息")
    @ApiOperation(value = "增加媒资信息", notes = "增加媒资信息", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody MediaInfoVO mediaInfoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        // 后台上架，都是审批通过
        mediaInfoVO.setStatus(EStatus.ENABLE);
        return mediaInfoService.addMediaInfo(mediaInfoVO);
    }

    @OperationLogger(value = "编辑媒资信息")
    @ApiOperation(value = "编辑媒资信息", notes = "编辑媒资信息", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody MediaInfoVO mediaInfoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return mediaInfoService.editMediaInfo(mediaInfoVO);
    }

    @OperationLogger(value = "删除选中媒资信息")
    @ApiOperation(value = "删除选中媒资信息", notes = "删除选中媒资信息", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@RequestBody List<String> ids) {
        return ResultUtil.successWithData(mediaInfoService.removeByIds(ids));
    }

    @OperationLogger(value = "转化媒资视频")
    @ApiOperation(value = "转化媒资视频【将mp4视频转成m3u8】", notes = "转化媒资视频转化媒资视频【将mp4视频转成m3u8】", response = String.class)
    @PostMapping("/transform")
    public String transform(@RequestBody TransformMediaRequest transformMediaRequest) {
        return mediaInfoService.transformMedia(transformMediaRequest);
    }

}
