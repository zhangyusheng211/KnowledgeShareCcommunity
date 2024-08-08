package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.MessagePushService;
import com.moxi.mogublog.commons.vo.MessagePushVO;
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

import java.util.List;

/**
 * 消息推送表 RestApi
 *
 * @author 陌溪
 * @date 2023年4月2日11:17:48
 */
@RestController
@RequestMapping("/messagePush")
@Api(value = "消息推送相关接口", tags = {"消息推送相关接口"})
@Slf4j
public class MessagePushRestApi {

    @Autowired
    private MessagePushService messagePushService;

    @AuthorityVerify
    @ApiOperation(value = "获取消息推送列表", notes = "获取消息推送列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody MessagePushVO messagePushVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取消息推送列表");
        return ResultUtil.successWithData(messagePushService.getPageList(messagePushVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加消息推送")
    @ApiOperation(value = "增加消息推送", notes = "增加消息推送", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody MessagePushVO messagePushVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加消息推送");
        return messagePushService.addMessagePush(messagePushVO);
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "触发消息推送")
    @ApiOperation(value = "触发消息推送", notes = "触发消息推送", response = String.class)
    @PostMapping("/push")
    public String push(@Validated({Update.class}) @RequestBody MessagePushVO messagePushVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("触发消息推送");
        return messagePushService.messagePush(messagePushVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑消息推送")
    @ApiOperation(value = "编辑消息推送", notes = "编辑消息推送", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody MessagePushVO messagePushVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑消息推送");
        return messagePushService.editMessagePush(messagePushVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除消息推送")
    @ApiOperation(value = "批量删除消息推送", notes = "批量删除消息推送", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<MessagePushVO> messagePushVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除消息推送");
        return messagePushService.deleteBatchMessagePush(messagePushVoList);
    }
}

