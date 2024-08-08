package com.moxi.mogublog.web.restapi;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.ResourceSort;
import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.LoginVerify.LoginVerify;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.ResourceSortService;
import com.moxi.mogublog.xo.service.ResourceService;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mougblog.base.enums.EStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 资源RestApi
 *
 * @author 陌溪
 * @date 2023年3月24日20:33:59
 */
@RestController
@RequestMapping("/resource")
@Api(value = "资源相关接口", tags = {"学习教程相关接口"})
@Slf4j
public class ResourceRestApi {
    @Autowired
    private ResourceService resourceService;

    @ApiOperation(value = "查询资源列表", notes = "查询资源列表")
    @PostMapping("/getResourceList")
    public String getResourceList(@RequestBody ResourceVO resourceVO) {
        log.info("[getResourceList] 通过分类来获取资源");
        return ResultUtil.successWithData(resourceService.getPageList(resourceVO));
    }

    @ApiOperation(value = "获取资源详情", notes = "获取资源详情")
    @PostMapping("/getResourceDetail")
    public String getResourceDetail(@RequestBody ResourceVO resourceVO) {
        log.info("[getResourceDetail] 获取资源详情: {}", JsonUtils.objectToJson(resourceVO));
        return ResultUtil.successWithData(resourceService.getDetail(resourceVO));
    }

    @LoginVerify
    @ApiOperation(value = "下载资源", notes = "下载资源")
    @PostMapping("/downloadResource")
    public String downloadResource(@RequestBody ResourceVO resourceVO) {
        log.info("[downloadResource] 下载资源");
        return ResultUtil.successWithData(resourceService.download(resourceVO));
    }
}

