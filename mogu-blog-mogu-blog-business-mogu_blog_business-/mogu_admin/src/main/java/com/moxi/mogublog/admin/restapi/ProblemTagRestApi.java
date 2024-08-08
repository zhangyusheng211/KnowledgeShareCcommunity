package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.ProblemTagService;
import com.moxi.mogublog.commons.vo.ProblemTagVO;
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
 * 问题标签表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月7日22:37:39
 */
@Api(value = "问题标签相关接口", tags = {"问题标签相关接口"})
@RestController
@RequestMapping("/problemTag")
@Slf4j
public class ProblemTagRestApi {

    @Autowired
    private ProblemTagService problemTagService;

    @AuthorityVerify
    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ProblemTagVO problemTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, problemTagService.getPageList(problemTagVO));
    }

    @AuthorityVerify
    @ApiOperation(value = "获取所有的标签列表", notes = "获取所有的标签列表", response = String.class)
    @PostMapping("/getAllTagList")
    public String getAllTagList() {
        return ResultUtil.result(SysConf.SUCCESS, problemTagService.getAllList());
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加标签")
    @ApiOperation(value = "增加标签", notes = "增加标签", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ProblemTagVO problemTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加标签");
        return problemTagService.addProblemTag(problemTagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑标签")
    @ApiOperation(value = "编辑标签", notes = "编辑标签", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody ProblemTagVO problemTagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑标签");
        return problemTagService.editProblemTag(problemTagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除标签")
    @ApiOperation(value = "批量删除标签", notes = "批量删除标签", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<ProblemTagVO> problemTagVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除标签");
        return problemTagService.deleteBatchProblemTag(problemTagVOList);
    }
}

