package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MedalClassifyService;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
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
 * 勋章分类表 RestApi
 *
 * @author 陌溪
 * @date 2022年12月28日09:27:03
 */
@Api(value = "勋章分类相关接口", tags = {"勋章分类相关接口"})
@RestController
@RequestMapping("/medalClassify")
@Slf4j
public class MedalClassifyRestApi {

    @Resource
    private MedalClassifyService medalClassifyService;

    @AuthorityVerify
    @ApiOperation(value = "获取勋章分类列表", notes = "获取勋章分类列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody MedalClassifyVO medalClassifyVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取勋章分类列表");
        return ResultUtil.result(SysConf.SUCCESS, medalClassifyService.getPageList(medalClassifyVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加勋章分类")
    @ApiOperation(value = "增加勋章分类", notes = "增加勋章分类", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody MedalClassifyVO medalClassifyVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加勋章分类");
        return medalClassifyService.addMedalClassify(medalClassifyVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑勋章分类")
    @ApiOperation(value = "编辑勋章分类", notes = "编辑勋章分类", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody MedalClassifyVO medalClassifyVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑勋章分类");
        return medalClassifyService.editMedalClassify(medalClassifyVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除勋章分类")
    @ApiOperation(value = "批量删除勋章分类", notes = "批量删除勋章分类", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<MedalClassifyVO> medalClassifyVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除勋章分类");
        return medalClassifyService.deleteBatchMedalClassify(medalClassifyVOList);
    }
}

