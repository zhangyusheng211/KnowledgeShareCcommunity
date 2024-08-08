package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.UserMomentService;
import com.moxi.mogublog.commons.vo.UserMomentVO;
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
 * 用户动态表 RestApi
 *
 * @author 陌溪
 * @date 2021年12月26日08:25:36
 */
@Api(value = "用户动态相关接口", tags = {"用户动态相关接口"})
@RestController
@RequestMapping("/userMoment")
@Slf4j
public class UserMomentRestApi {

    @Resource
    private UserMomentService userMomentService;

    @AuthorityVerify
    @ApiOperation(value = "获取用户动态列表", notes = "获取用户动态列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取用户动态列表");
        return ResultUtil.result(SysConf.SUCCESS, userMomentService.getPageList(userMomentVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加用户动态")
    @ApiOperation(value = "增加用户动态", notes = "增加用户动态", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加用户动态");
        return userMomentService.addUserMoment(userMomentVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑用户动态")
    @ApiOperation(value = "编辑用户动态", notes = "编辑用户动态", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody UserMomentVO userMomentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑用户动态");
        return userMomentService.editUserMoment(userMomentVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除用户动态")
    @ApiOperation(value = "批量删除用户动态", notes = "批量删除用户动态", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<UserMomentVO> userMomentVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除用户动态");
        return userMomentService.deleteBatchUserMoment(userMomentVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "审核问题")
    @ApiOperation(value = "审核问题", notes = "审核问题", response = String.class)
    @PostMapping("/audit")
    public String audit(@RequestBody UserMomentVO userMomentVO) {
        return userMomentService.auditMoment(userMomentVO);
    }

}

