package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.commons.entity.MediaTag;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaTagService;
import com.moxi.mogublog.commons.vo.MediaTagVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 媒资标签Controller
 *
 * @author thh
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/media/tag")
public class MediaTagRestApi {
    @Autowired
    private MediaTagService mediaTagService;

    /**
     * 查询媒资标签列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "获取媒资标签列表", notes = "获取媒资标签列表", response = String.class)
    public String list(@RequestBody MediaTagVO mediaTagVO) {
        return ResultUtil.successWithData(mediaTagService.getPageList(mediaTagVO));
    }


    /**
     * 获取媒资标签详细信息
     */
    @ApiOperation(value = "获取媒资标签详细信息", notes = "获取媒资标签详细信息", response = String.class)
    @PostMapping("/info")
    public String getInfo(@RequestBody MediaTagVO mediaTagVO) {
        return ResultUtil.successWithData(mediaTagService.getById(mediaTagVO.getUid()));
    }

    /**
     * 新增媒资标签
     */
    @PostMapping("/add")
    @ApiOperation(value = "新增媒资标签详细信息", notes = "新增媒资标签详细信息", response = String.class)
    public String add(@RequestBody MediaTagVO mediaTagVO) {
        MediaTag mediaTag = new MediaTag();
        BeanUtils.copyProperties(mediaTagVO, mediaTag);
        return ResultUtil.successWithData(mediaTagService.save(mediaTag));
    }

    /**
     * 修改媒资标签
     */
    @PostMapping("/edit")
    @ApiOperation(value = "更新媒资标签详细信息", notes = "更新媒资标签详细信息", response = String.class)
    public String edit(@RequestBody MediaTagVO mediaTagVO) {
        MediaTag mediaTag = new MediaTag();
        BeanUtils.copyProperties(mediaTagVO, mediaTag);
        return ResultUtil.successWithData(mediaTagService.saveOrUpdate(mediaTag));
    }

    /**
     * 删除媒资标签
     */
    @ApiOperation(value = "删除媒资标签详细信息", notes = "删除媒资标签详细信息", response = String.class)
    @PostMapping("/deleteBatch")
    public String deteleBatch(@RequestBody List<String> uids) {
        return ResultUtil.successWithData(mediaTagService.removeByIds(uids));
    }
}
