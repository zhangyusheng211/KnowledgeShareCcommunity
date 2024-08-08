package com.moxi.mogublog.web.restapi;


import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaCategoryService;
import com.moxi.mogublog.commons.vo.MediaCategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list")
    @ApiOperation(value = "获取媒资分类列表", notes = "获取媒资分类列表", response = String.class)
    @PostMapping("/list")
    public String list(MediaCategoryVO mediaCategoryVO) {
        return ResultUtil.successWithData(mediaCategoryService.getPageList(mediaCategoryVO));
    }


    /**
     * 获取媒资分类详细信息
     */
    @ApiOperation(value = "获取媒资分类详细信息", notes = "获取媒资分类详细信息", response = String.class)
    @GetMapping(value = "/{uid}")
    public String getInfo(@PathVariable("uid") String uid) {
        return ResultUtil.successWithData(mediaCategoryService.getById(uid));
    }
}
