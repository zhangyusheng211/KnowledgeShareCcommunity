package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.entity.UserSubscribe;
import com.moxi.mogublog.commons.schema.ProductRequest;
import com.moxi.mogublog.commons.vo.UserSubscribeVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.xo.service.UserSubscribeService;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户订阅相关
 *
 * @author 陌溪
 * @date 2022年6月16日22:57:34
 */
@RestController
@RefreshScope
@RequestMapping("/subscribe")
@Api(value = "用户订阅相关", tags = {"用户订阅相关"})
@Slf4j
public class UserSubscribeRestApi {

    @Resource
    private UserSubscribeService userSubscribeService;

    @AvoidRepeatableCommit(timeout = 5000)
    @ApiOperation(value = "新增订阅", notes = "新增订阅")
    @RequestMapping(value = "/addUserSubscribe", method = RequestMethod.POST)
    public String addUserSubscribe(@Validated({Insert.class}) @RequestBody UserSubscribeVO userSubscribeVO, BindingResult result) {
        log.info("[deleteUserSubscribe] 新增订阅");
        ThrowableUtils.checkParamArgument(result);
        return userSubscribeService.addUserSubscribe(userSubscribeVO);
    }

    @AvoidRepeatableCommit(timeout = 5000)
    @ApiOperation(value = "删除订阅", notes = "删除订阅")
    @RequestMapping(value = "/deleteUserSubscribe", method = RequestMethod.POST)
    public String deleteUserSubscribe(@Validated({Insert.class}) @RequestBody UserSubscribeVO userSubscribeVO, BindingResult result) {
        log.info("[deleteUserSubscribe] 删除订阅");
        ThrowableUtils.checkParamArgument(result);
        return userSubscribeService.deleteUserSubscribe(userSubscribeVO);
    }

    @ApiOperation(value = "校验用户是否订阅", notes = "校验用户是否订阅")
    @RequestMapping(value = "/checkUserSubscribe", method = RequestMethod.POST)
    public String checkUserSubscribe(@Validated({Insert.class}) @RequestBody UserSubscribeVO userSubscribeVO, BindingResult result) {
        log.info("[deleteUserSubscribe] 校验用户是否订阅");
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(userSubscribeService.checkUserSubscribe(userSubscribeVO));
    }

}
