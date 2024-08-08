package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.ResourceService;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资源相关 RestApi
 *
 * @author 陌溪
 * @date 2023年3月23日09:09:40
 */
@RestController
@RequestMapping("/resource")
@Api(value = "资源相关接口", tags = {"资源相关接口"})
@Slf4j
public class ResourceRestApi {

    @Autowired
    private ResourceService studyVideoService;

    @AuthorityVerify
    @ApiOperation(value = "获取资源列表", notes = "获取资源列表", response = String.class)
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ResourceVO resourceVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取资源列表: {}", resourceVO);
        return ResultUtil.successWithData(studyVideoService.getPageList(resourceVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加资源")
    @ApiOperation(value = "增加资源", notes = "增加资源", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ResourceVO resourceVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加资源: {}", resourceVO);
        return studyVideoService.addResource(resourceVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑资源")
    @ApiOperation(value = "编辑资源", notes = "编辑资源", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody ResourceVO resourceVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑资源: {}", resourceVO);
        return studyVideoService.editResource(resourceVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除资源")
    @ApiOperation(value = "删除资源", notes = "删除资源", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<ResourceVO> resourceVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除资源: {}", resourceVOList);
        return studyVideoService.deleteBatchResource(resourceVOList);
    }

}

