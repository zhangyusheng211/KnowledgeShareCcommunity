package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.LuckyAwardItemVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.LuckyAwardItemService;
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
 * 奖励项表 RestApi
 *
 * @author 陌溪
 * @date 2023年7月16日15:31:42
 */
@Api(value = "奖励项相关接口", tags = {"奖励项相关接口"})
@RestController
@RequestMapping("/luckyAwardItem")
@Slf4j
public class LuckyAwardItemRestApi {

    @Resource
    private LuckyAwardItemService luckyAwardItemService;

    @AuthorityVerify
    @ApiOperation(value = "获取奖励项列表", notes = "获取奖励项列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody LuckyAwardItemVO luckyAwardItemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取奖励项列表");
        return ResultUtil.result(SysConf.SUCCESS, luckyAwardItemService.getPageList(luckyAwardItemVO));
    }


    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加奖励项")
    @ApiOperation(value = "增加奖励项", notes = "增加奖励项", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody LuckyAwardItemVO luckyAwardItemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加奖励项");
        return luckyAwardItemService.addLuckyAwardItem(luckyAwardItemVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑奖励项")
    @ApiOperation(value = "编辑奖励项", notes = "编辑奖励项", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody LuckyAwardItemVO luckyAwardItemVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑奖励项");
        return luckyAwardItemService.editLuckyAwardItem(luckyAwardItemVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除奖励项")
    @ApiOperation(value = "批量删除奖励项", notes = "批量删除奖励项", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<LuckyAwardItemVO> luckyAwardItemVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除奖励项");
        return luckyAwardItemService.deleteBatchLuckyAwardItem(luckyAwardItemVOList);
    }
}

