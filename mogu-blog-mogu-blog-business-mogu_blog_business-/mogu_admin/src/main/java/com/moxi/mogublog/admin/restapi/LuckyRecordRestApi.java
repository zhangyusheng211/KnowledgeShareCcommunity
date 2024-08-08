package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.LuckyRecordService;
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
 * 抽奖活动表 RestApi
 *
 * @author 陌溪
 * @date 2023年7月16日15:31:42
 */
@Api(value = "抽奖活动相关接口", tags = {"抽奖活动相关接口"})
@RestController
@RequestMapping("/luckyRecord")
@Slf4j
public class LuckyRecordRestApi {

    @Resource
    private LuckyRecordService luckyRecordService;

    @AuthorityVerify
    @ApiOperation(value = "获取抽奖活动列表", notes = "获取抽奖活动列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody LuckyRecordVO luckyRecordVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取抽奖活动列表");
        return ResultUtil.result(SysConf.SUCCESS, luckyRecordService.getPageList(luckyRecordVO));
    }


    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加抽奖活动")
    @ApiOperation(value = "增加抽奖活动", notes = "增加抽奖活动", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody LuckyRecordVO luckyRecordVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加抽奖活动");
        return luckyRecordService.addLuckyRecord(luckyRecordVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑抽奖活动")
    @ApiOperation(value = "编辑抽奖活动", notes = "编辑抽奖活动", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody LuckyRecordVO luckyRecordVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑抽奖活动");
        return luckyRecordService.editLuckyRecord(luckyRecordVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除抽奖活动")
    @ApiOperation(value = "批量删除抽奖活动", notes = "批量删除抽奖活动", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<LuckyRecordVO> luckyRecordVOList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除抽奖活动");
        return luckyRecordService.deleteBatchLuckyRecord(luckyRecordVOList);
    }
}

