package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.MedalService;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mogublog.commons.vo.MedalVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Insert;
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

/**
 * 勋章
 *
 * @author 陌溪
 * @date 2022年12月31日18:06:29
 */
@RestController
@RequestMapping("/medal")
@Api(value = "勋章接口", tags = {"勋章接口"})
@Slf4j
public class MedalRestApi {

    @Resource
    MedalService medalService;

    @ApiOperation(value = "查询当前用户最近获取的勋章【查询未读的】", notes = "查询当前用户最近获取的勋章【查询未读的】")
    @PostMapping("/getMedalByRecent")
    public String getMedalByRecent(@Validated({Insert.class}) @RequestBody MedalVO medalVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("查询用户最近获取的勋章");
        return ResultUtil.result(SysConf.SUCCESS, medalService.getMedalByRecent(medalVO));
    }

    @ApiOperation(value = "查询用户展示的勋章", notes = "查询用户展示的勋章")
    @PostMapping("/getUserShowMedalList")
    public String getUserShowMedalList(@Validated({Insert.class}) @RequestBody MedalVO medalVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("查询用户最近获取的勋章");
        return ResultUtil.result(SysConf.SUCCESS, medalService.getMedalByRecent(medalVO));
    }

    @ApiOperation(value = "查询用户获得的全部勋章", notes = "查询用户获得的全部勋章")
    @PostMapping("/getUserMedalList")
    public String getUserMedalList(@Validated({Insert.class}) @RequestBody MedalClassifyVO medalClassifyVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("查询用户最近获取的勋章");
        return ResultUtil.result(SysConf.SUCCESS, medalService.getUserMedalList(medalClassifyVO));
    }
}
