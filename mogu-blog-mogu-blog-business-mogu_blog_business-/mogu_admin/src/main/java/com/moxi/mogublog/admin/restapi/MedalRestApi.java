package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MedalService;
import com.moxi.mogublog.commons.vo.MedalVO;
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

import javax.annotation.Resource;
import java.util.List;

/**
 * 勋章表 RestApi
 *
 * @author 陌溪
 * @date 2021年4月26日22:59:09
 */
@Api(value = "勋章相关接口", tags = {"勋章相关接口"})
@RestController
@RequestMapping("/medal")
@Slf4j
public class MedalRestApi {

    @Resource
    private MedalService medalService;

    @AuthorityVerify
    @ApiOperation(value = "获取勋章列表", notes = "获取勋章列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody MedalVO medalVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取勋章列表");
        return ResultUtil.result(SysConf.SUCCESS, medalService.getPageList(medalVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加勋章")
    @ApiOperation(value = "增加勋章", notes = "增加勋章", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody MedalVO medalVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加勋章");
        return medalService.addMedal(medalVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑勋章")
    @ApiOperation(value = "编辑勋章", notes = "编辑勋章", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody MedalVO medalVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑勋章");
        return medalService.editMedal(medalVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除勋章")
    @ApiOperation(value = "批量删除勋章", notes = "批量删除勋章", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<MedalVO> medalVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除勋章");
        return medalService.deleteBatchMedal(medalVOList);
    }
}

