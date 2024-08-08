package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.SubjectSortService;
import com.moxi.mogublog.commons.vo.SubjectSortVO;
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
 * 专题分类表 RestApi
 *
 * @author 陌溪
 * @date 2022年9月25日22:12:12
 */
@Api(value = "专题分类相关接口", tags = {"专题分类相关接口"})
@RestController
@RequestMapping("/subjectSort")
@Slf4j
public class SubjectSortRestApi {

    @Autowired
    private SubjectSortService subjectSortService;

    @AuthorityVerify
    @ApiOperation(value = "获取专题分类列表", notes = "获取专题分类列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SubjectSortVO subjectSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取专题分类列表");
        return ResultUtil.result(SysConf.SUCCESS, subjectSortService.getPageList(subjectSortVO));
    }


    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加专题分类")
    @ApiOperation(value = "增加专题分类", notes = "增加专题分类", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody SubjectSortVO subjectSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加专题分类");
        return subjectSortService.addSubjectSort(subjectSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑专题分类")
    @ApiOperation(value = "编辑专题分类", notes = "编辑专题分类", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody SubjectSortVO subjectSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑专题分类");
        return subjectSortService.editSubjectSort(subjectSortVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除专题分类")
    @ApiOperation(value = "批量删除专题分类", notes = "批量删除专题分类", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<SubjectSortVO> subjectSortVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除专题分类");
        return subjectSortService.deleteBatchSubjectSort(subjectSortVOList);
    }
}

