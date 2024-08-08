package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
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
 * 通知表 RestApi
 *
 * @author 陌溪
 * @date 2021年8月6日00:03:08
 */
@RestController
@Api(value = "通知相关接口", tags = {"通知相关接口"})
@RequestMapping("/notice")
@Slf4j
public class NoticeRestApi {

    @Autowired
    NoticeService noticeService;

    @AuthorityVerify
    @ApiOperation(value = "获取通知列表", notes = "获取通知列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("获取通知列表");
        return ResultUtil.successWithData(noticeService.getPageList(noticeVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加通知")
    @ApiOperation(value = "增加通知", notes = "增加通知", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.addNotice(noticeVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑通知")
    @ApiOperation(value = "编辑通知", notes = "编辑通知", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.editNotice(noticeVO);
    }

    @OperationLogger(value = "删除通知")
    @ApiOperation(value = "删除通知", notes = "删除通知", response = String.class)
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.deleteNotice(noticeVO);
    }

    @ApiOperation(value = "获取后台站内信列表", notes = "获取后台站内信列表", response = String.class)
    @PostMapping("/getBlackNoticeList")
    public String getBlackNoticeList(@RequestBody NoticeVO noticeVO, BindingResult result) {
        return ResultUtil.successWithData(noticeService.getBlackNoticeList(noticeVO));
    }

    @OperationLogger(value = "批量删除后台通知")
    @ApiOperation(value = "批量删除后台通知", notes = "批量删除后台通知", response = String.class)
    @PostMapping("/deleteBatchBlackNotice")
    public String deleteBatchBlackNotice(@Validated({Delete.class}) @RequestBody List<NoticeVO> noticeVoList, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.deleteBatchNotice(noticeVoList);
    }

    @OperationLogger(value = "删除站内信小红点提示")
    @ApiOperation(value = "删除站内信小红点提示", notes = "删除站内信小红点提示", response = String.class)
    @PostMapping("/deleteRedisBatchNotice")
    public String deleteRedisBatchNotice() {
        String adminUid = RequestHolder.getAdminUid();
        noticeService.deleteRedisBatchNotice(adminUid);
        return ResultUtil.successWithData("删除小红点提示成功");
    }

    @ApiOperation(value = "获取后台站内信消息数量", notes = "获取后台站内信消息数量", response = String.class)
    @PostMapping("/searchAllNoticeCount")
    public String searchAllNoticeCount() {
        String adminUid = RequestHolder.getAdminUid();
        return ResultUtil.successWithData(noticeService.searchAllNoticeCount(adminUid));
    }
}