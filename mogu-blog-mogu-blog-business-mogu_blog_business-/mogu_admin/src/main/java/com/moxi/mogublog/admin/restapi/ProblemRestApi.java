package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.ProblemService;
import com.moxi.mogublog.commons.vo.ProblemVO;
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
 * 问题表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月7日22:37:39
 */
@Api(value = "问题相关接口", tags = {"问题相关接口"})
@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemRestApi {

    @Autowired
    private ProblemService problemService;

    @AuthorityVerify
    @ApiOperation(value = "获取问题列表", notes = "获取问题列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ProblemVO problemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问题列表");
        return ResultUtil.result(SysConf.SUCCESS, problemService.getPageList(problemVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加问题")
    @ApiOperation(value = "增加问题", notes = "增加问题", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody ProblemVO problemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加问题");
        return problemService.addProblem(problemVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑问题")
    @ApiOperation(value = "编辑问题", notes = "编辑问题", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody ProblemVO problemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑问题");
        return problemService.editProblem(problemVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除问题")
    @ApiOperation(value = "批量删除问题", notes = "批量删除问题", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<ProblemVO> problemVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除问题");
        return problemService.deleteBatchProblem(problemVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "审核问题")
    @ApiOperation(value = "审核问题", notes = "审核问题", response = String.class)
    @PostMapping("/audit")
    public String audit(@RequestBody ProblemVO problemVO) {
        return problemService.auditProblem(problemVO);
    }


}

