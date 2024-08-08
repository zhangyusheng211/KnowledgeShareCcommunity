package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.commons.entity.MediaCategory;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaCategoryService;
import com.moxi.mogublog.commons.vo.MediaCategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 媒资分类Controller
 *
 * @author thh
 * @date 2021-12-14
 */
@RestController
@RequestMapping("/media/category")
public class MediaCategoryRestApi {
    @Autowired
    private MediaCategoryService mediaCategoryService;

    /**
     * 查询媒资分类列表
     */
    @ApiOperation(value = "获取媒资分类列表", notes = "获取媒资分类列表", response = String.class)
    @PostMapping("/list")
    public String list(@RequestBody MediaCategoryVO mediaCategoryVO) {
        return ResultUtil.successWithData(mediaCategoryService.getPageList(mediaCategoryVO));
    }


    /**
     * 获取媒资分类详细信息
     */
    @ApiOperation(value = "获取媒资分类详细信息", notes = "获取媒资分类详细信息", response = String.class)
    @PostMapping(value = "/info")
    public String getInfo(@RequestBody MediaCategoryVO mediaCategoryVO) {
        return ResultUtil.successWithData(mediaCategoryService.getById(mediaCategoryVO.getUid()));
    }

    /**
     * 新增媒资分类
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增媒资分类详细信息", notes = "新增媒资分类详细信息", response = String.class)
    public String add(@RequestBody MediaCategoryVO mediaCategoryVO) {
        MediaCategory mediaCategory = new MediaCategory();
        BeanUtils.copyProperties(mediaCategoryVO, mediaCategory);
        return ResultUtil.successWithData(mediaCategoryService.save(mediaCategory));
    }

    /**
     * 修改媒资分类
     */
    @PostMapping(value = "/edit")
    @ApiOperation(value = "更新媒资分类详细信息", notes = "更新媒资分类详细信息", response = String.class)
    public String edit(@RequestBody MediaCategoryVO mediaCategoryVO) {
        MediaCategory mediaCategory = new MediaCategory();
        BeanUtils.copyProperties(mediaCategoryVO, mediaCategory);
        return ResultUtil.successWithData(mediaCategoryService.saveOrUpdate(mediaCategory));
    }

    /**
     * 删除媒资分类
     */
    @ApiOperation(value = "删除媒资分类详细信息", notes = "删除媒资分类详细信息", response = String.class)
    @PostMapping("/deleteBatch")
    public String deteleBatch(@RequestBody List<String> uids) {
        return ResultUtil.successWithData(mediaCategoryService.removeByIds(uids));
    }
}
