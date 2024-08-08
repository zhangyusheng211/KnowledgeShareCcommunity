package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.schema.UpdateCreditsRequest;
import com.moxi.mogublog.commons.vo.LuckyActivityVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.CreditsManager;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.xo.mapper.CreditsLogMapper;
import com.moxi.mogublog.xo.service.CreditsLogService;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.GetList;
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
 * 用户积分管理
 *
 * @author 陌溪
 * @date 2020年1月4日21:29:09
 */
@RestController
@Api(value = "用户相关接口", tags = {"用户相关接口"})
@RequestMapping("/credits")
@Slf4j
public class CreditsRestApi {

    @Resource
    CreditsManager creditsManager;
    @Resource
    CreditsLogService creditsLogService;


    @OperationLogger(value = "用户积分情况")
    @ApiOperation(value = "用户积分情况", notes = "用户积分情况", response = String.class)
    @PostMapping("/queryCredits")
    public String queryCredits(@RequestBody UserVO userVO) {
        return creditsManager.queryCredits(userVO);
    }

    @OperationLogger(value = "手动补偿用户积分")
    @ApiOperation(value = "手动补偿用户积分", notes = "手动补偿用户积分", response = String.class)
    @PostMapping("/compensation")
    public String compensation(@RequestBody CreditsLogVO creditsLogVO) {
        return creditsManager.compensation(creditsLogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "分页查询积分流水")
    @ApiOperation(value = "分页查询积分流水", notes = "分页查询积分流水", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody CreditsLogVO creditsLogVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("分页查询积分流水");
        return ResultUtil.result(SysConf.SUCCESS, creditsLogService.getPageList(creditsLogVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "更新用户积分")
    @ApiOperation(value = "更新用户积分", notes = "更新用户积分", response = String.class)
    @PostMapping("/updateCredits")
    public String updateCredits(@RequestBody UpdateCreditsRequest updateCreditsRequest) {
        log.info("分页查询积分流水");
        return ResultUtil.result(SysConf.SUCCESS, creditsManager.updateCredit(updateCreditsRequest));
    }
}
