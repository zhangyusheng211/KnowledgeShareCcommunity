package com.moxi.mogublog.web.restapi;


import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MediaTagService;
import com.moxi.mogublog.commons.vo.MediaTagVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list")
    @ApiOperation(value = "获取媒资标签列表", notes = "获取媒资标签列表", response = String.class)
    @PostMapping("/list")
    public String list(MediaTagVO mediaTagVO) {
        return ResultUtil.successWithData(mediaTagService.getPageList(mediaTagVO));
    }


    /**
     * 获取媒资标签详细信息
     */
    @ApiOperation(value = "获取媒资标签详细信息", notes = "获取媒资标签详细信息", response = String.class)
    @GetMapping(value = "/{uid}")
    public String getInfo(@PathVariable("uid") String uid) {
        return ResultUtil.successWithData(mediaTagService.getById(uid));
    }
}
