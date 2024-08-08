package com.moxi.mogublog.admin.restapi;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.WebUtils;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.vo.FileVO;
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
 * 素材管理
 *
 * @author 陌溪
 * @date 2022年10月23日21:33:01
 */
@Api(value = "素材管理相关接口", tags = {"素材管理相关接口"})
@RestController
@RequestMapping("/file")
@Slf4j
public class FileRestApi {

    @Resource
    private PictureFeignClient pictureFeignClient;

    @AuthorityVerify
    @ApiOperation(value = "获取素材列表", notes = "获取图片分类列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody FileVO fileVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取图片分类列表: {}", fileVO);
        return pictureFeignClient.getFileList(fileVO);
    }

    @AuthorityVerify
    @ApiOperation(value = "批量删除素材列表", notes = "批量删除素材列表", response = String.class)
    @PostMapping(value = "/batchDeleteFile")
    public String batchDeleteFile(@RequestBody List<String> fileUidList) {
        log.info("批量删除");
        return pictureFeignClient.batchDeleteFile(fileUidList);
    }
}

