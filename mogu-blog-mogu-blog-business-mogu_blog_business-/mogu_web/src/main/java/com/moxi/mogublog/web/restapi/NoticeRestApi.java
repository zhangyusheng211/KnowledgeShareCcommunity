package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知 RestApi
 *
 * @author 陌溪
 * @date 2021年8月8日15:11:47
 */
@RestController
@RequestMapping("/notice")
@Api(value = "通知相关接口", tags = {"通知相关接口"})
@Slf4j
public class NoticeRestApi {

    @Autowired
    NoticeService noticeService;

    @BussinessLog(value = "获取通知列表", behavior = EBehavior.GET_NOTICE)
    @ApiOperation(value = "获取通知列表", notes = "获取通知列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        noticeVO.setUserUid(RequestHolder.getUserUid());
        noticeVO.setIsBlack(0); // 前台通知
        return ResultUtil.successWithData(noticeService.getPageList(noticeVO));
    }

    @BussinessLog(value = "删除通知", behavior = EBehavior.DELETE_NOTICE)
    @ApiOperation(value = "删除通知", notes = "删除通知", response = String.class)
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody NoticeVO noticeVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.deleteNotice(noticeVO);
    }

    @BussinessLog(value = "删除选中通知", behavior = EBehavior.DELETE_BATCH_NOTICE)
    @ApiOperation(value = "删除选中通知", notes = "删除选中通知", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody List<NoticeVO> noticeVoList, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return noticeService.deleteBatchNotice(noticeVoList);
    }


    @ApiOperation(value = "获取用户收到的通知数", notes = "获取用户收到的通知数")
    @GetMapping("/getUserReceiveNoticeCount")
    public String getUserReceiveNoticeCount() {
        log.info("获取用户收到的评论回复数");
        return noticeService.getUserReceiveNoticeCount();
    }

    @ApiOperation(value = "阅读用户接收的通知数", notes = "阅读用户接收的通知数")
    @PostMapping("/readUserReceiveNoticeCount")
    public String readUserReceiveNoticeCount() {
        log.info("阅读用户接收的评论数");
        return noticeService.readUserReceiveNoticeCount();
    }

}

