package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.ImService;
import com.moxi.mogublog.commons.vo.ImMessageVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
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
 * 聊天消息表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月19日14:46:53
 */
@Api(value = "聊天消息接口", tags = {"聊天消息相关接口"})
@RestController
@RequestMapping("/imMessage")
@Slf4j
public class ImMessageRestApi {

    @Autowired
    private ImService imService;

    @AuthorityVerify
    @ApiOperation(value = "获取聊天记录列表", notes = "获取用户访问列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody ImMessageVO imMessageVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, imService.getPageList(imMessageVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除聊天记录")
    @ApiOperation(value = "批量删除聊天记录", notes = "批量删除聊天记录", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<ImMessageVO> imMessageVOVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除标签");
        return imService.deleteBatchImMessage(imMessageVOVOList);
    }
}

