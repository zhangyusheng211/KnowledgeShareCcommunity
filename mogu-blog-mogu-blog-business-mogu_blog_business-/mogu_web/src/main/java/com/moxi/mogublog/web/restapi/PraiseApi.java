package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.manager.UserPraiseRecordManager;
import com.moxi.mogublog.xo.service.UserPraiseRecordService;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Default;
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
 * 点赞模块
 *
 * @author 遇见
 */
@RestController
@RequestMapping("/praise")
@Api(value = "点赞模块接口", tags = {"点赞模块接口"})
@Slf4j
public class PraiseApi {

    @Resource
    UserPraiseRecordManager userPraiseRecordManager;
    @Resource
    UserPraiseRecordService userPraiseRecordService;

    @ApiOperation(value = "检查是否点赞/踩", notes = "检查是否点赞")
    @PostMapping("/checkPraise")
    public String checkPraise(@Validated({Insert.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("检查是否点赞");
        return ResultUtil.result(SysConf.SUCCESS, userPraiseRecordManager.hasPraised(userPraiseRecordVO));
    }

    @ApiOperation(value = "获取点赞/踩数", notes = "点赞/踩数")
    @PostMapping("/getPraiseCount")
    public String getPraiseCount(@Validated({Insert.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("检查是否点赞");
        return ResultUtil.result(SysConf.SUCCESS, userPraiseRecordService.praisedCount(userPraiseRecordVO));
    }

    /**
     * 点赞
     *
     * @return
     */
    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "点赞", behavior = EBehavior.PRAISE)
    @ApiOperation(value = "点赞", notes = "点赞指定资源", response = String.class)
    @PostMapping("/praise")
    public String confirm(@Validated({Insert.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("点赞");
        return userPraiseRecordManager.praise(userPraiseRecordVO);
    }

    /**
     * 取消点赞
     *
     * @return
     */
    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "取消点赞", behavior = EBehavior.CANCEL_PRAISE)
    @ApiOperation(value = "取消点赞", notes = "取消点赞", response = String.class)
    @PostMapping("/cancelPraise")
    public String cancelPraise(@Validated({Default.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("取消点赞");
        return userPraiseRecordManager.cancelPraise(userPraiseRecordVO);
    }

    /**
     * 点踩
     *
     * @return
     */
    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "点踩", behavior = EBehavior.TREAD)
    @ApiOperation(value = "点踩", notes = "点赞指定资源", response = String.class)
    @PostMapping("/tread")
    public String tread(@Validated({Insert.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("点踩");
        return userPraiseRecordManager.tread(userPraiseRecordVO);
    }

    /**
     * 取消点赞
     *
     * @return
     */
    @AvoidRepeatableCommit(timeout = 2000)
    @BussinessLog(value = "取消点踩", behavior = EBehavior.CANCEL_TREAD)
    @ApiOperation(value = "取消点踩", notes = "取消点赞", response = String.class)
    @PostMapping("/cancelTread")
    public String cancelTread(@Validated({Default.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("取消点踩");
        return userPraiseRecordManager.cancelTread(userPraiseRecordVO);
    }


    @ApiOperation(value = "获取点赞用户列表", notes = "获取点赞用户列表", response = String.class)
    @PostMapping("/getPraiseUserList")
    public String getPraiseUserList(@Validated({Default.class}) @RequestBody UserPraiseRecordVO userPraiseRecordVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取点赞用户列表");
        return ResultUtil.successWithData(userPraiseRecordManager.getPraiseList(userPraiseRecordVO));
    }
}
