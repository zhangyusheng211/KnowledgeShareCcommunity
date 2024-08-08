package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.service.RoomService;
import com.moxi.mogublog.commons.vo.RoomVO;
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
 * 聊天室表 RestApi
 *
 * @author 陌溪
 * @date 2022年3月19日20:51:12
 */
@Api(value = "聊天室接口", tags = {"聊天室相关接口"})
@RestController
@RequestMapping("/room")
@Slf4j
public class RoomRestApi {

    @Resource
    private RoomService roomService;

    @AuthorityVerify
    @ApiOperation(value = "获取聊天室列表", notes = "获取聊天室访问列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody RoomVO roomVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, roomService.getPageList(roomVO));
    }

    @AuthorityVerify
    @ApiOperation(value = "新增聊天室", notes = "新增聊天室")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody RoomVO roomVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, roomService.addRoom(roomVO));
    }

    @AuthorityVerify
    @ApiOperation(value = "编辑聊天室", notes = "编辑聊天室")
    @PostMapping("/edit")
    public String edit(@RequestBody RoomVO roomVO) {
        return ResultUtil.result(SysConf.SUCCESS, roomService.editRoom(roomVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除聊天室")
    @ApiOperation(value = "批量删除聊天室", notes = "批量删除聊天室记录", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<RoomVO> roomVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除聊天室");
        return roomService.deleteBatchRoom(roomVOList);
    }
}

