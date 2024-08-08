package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.HotSerchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 热搜表 RestApi
 *
 * @author
 * @date 2022年6月17日22:37:39
 */
@Api(value = "热搜接口", tags = {"热搜接口"})
@RestController
@RequestMapping("/hotSearch")
@Slf4j
public class HotSearchApi {

    @Autowired
    private HotSerchService hotSearchService;


    @ApiOperation(value = "获取热搜列表", notes = "获取热搜列表", response = String.class)
    @PostMapping("/getHotSearchList")
    public String getHotSearchList() {
        return ResultUtil.result(SysConf.SUCCESS, hotSearchService.getHotSearchList());
    }

    @ApiOperation(value = "添加搜若内容至热搜表", notes = "添加搜若内容至热搜表")
    @GetMapping("/addHotSearch")
    @AvoidRepeatableCommit
    public String addHotSearch(@ApiParam(name = "keyword", value = "搜索内容", required = true) @RequestParam(name = "keyword", required = true) String keyword) {

        return ResultUtil.successWithData(hotSearchService.addHotSearch(keyword));
    }

    @ApiOperation(value = "获取外部热搜列表【知乎、微博】", notes = "获取热搜列表", response = String.class)
    @GetMapping("/getOutsideHotSearch")
    public String getOutsideHotSearch(@ApiParam(name = "type", value = "热搜类型", required = true) @RequestParam(name = "type", required = true) String type) {
        return ResultUtil.result(SysConf.SUCCESS, hotSearchService.getOutsideHotSearch(type));
    }
}
