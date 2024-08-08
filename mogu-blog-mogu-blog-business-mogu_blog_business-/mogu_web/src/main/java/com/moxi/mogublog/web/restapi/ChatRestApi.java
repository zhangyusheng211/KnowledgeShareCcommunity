package com.moxi.mogublog.web.restapi;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.entity.Room;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.HttpRequestUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.chat.holder.SessionManager;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.ImService;
import com.moxi.mogublog.xo.service.RoomService;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.commons.vo.ImMessageVO;
import com.moxi.mogublog.commons.vo.RoomVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.MessageConstant;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.Insert;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auth: wly
 * @date: 2022/1/13
 * @descriptor:
 */
@RequestMapping("/chat")
@RestController
@Slf4j
public class ChatRestApi {

    @Autowired
    private ImService imService;

    @Autowired
    private RoomService roomService;

    @Resource
    private UserService userService;
    @Resource
    private SysParamsService sysParamsService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @SubmitVerify
    @PostMapping("/newRoom")
    public String newRoom(@RequestBody Room room) {
        String userUid = RequestHolder.getUserUid();
        // 谁发的消息
        User user = userService.getById(userUid);

        if (user.getStatus() != EStatus.ENABLE) {
            log.error("[newRoom] 用户不存在， userUid: {}", userUid);
            return ResultUtil.errorWithMessage("用户不存在");
        }
        // 不能私信自己
        if (user.getUid().equals(room.getReceiveId())) {
            return ResultUtil.errorWithMessage("不能私信自己");
        }

        // 查重
        Room repeat = roomService.lambdaQuery().
                eq(Room::getBelongUserId, user.getUid()).
                eq(Room::getReceiveId, room.getReceiveId()).last("limit 1").one();

        boolean isDelete = false;
        boolean isToDelete = false;
        boolean isRepeatCreate = false;

        if (repeat != null) {
            // 原来删除过，直接还原
            if (repeat.getStatus() == EStatus.DISABLED) {
                repeat.setStatus(EStatus.ENABLE);
                // 通知更新一下接收方的头像
                String receiveUserId = repeat.getReceiveId();
                if (StringUtils.isNotEmpty(receiveUserId)) {
                    // 获取接收方信息
                    User toUser = userService.getById(receiveUserId);
                    if (toUser != null) {
                        toUser = userService.setUserAvatar(toUser);
                        repeat.setAvatar(toUser.getPhotoUrl());
                    }
                }
                repeat.updateById();
                isDelete = true;
            } else {
                isRepeatCreate = true;
            }
        }

        // 在看看对方的房间是否创建了
        Room toRepeat = roomService.lambdaQuery().
                eq(Room::getReceiveId, user.getUid()).
                eq(Room::getBelongUserId, room.getReceiveId()).last("limit 1").one();
        if (toRepeat != null) {
            isToDelete = true;
            // 原来删除过，直接还原
            if (toRepeat.getStatus() == EStatus.DISABLED) {
                toRepeat.setStatus(EStatus.ENABLE);
                // 通知更新一下接收方的头像
                String receiveUserId = toRepeat.getReceiveId();
                if (StringUtils.isNotEmpty(receiveUserId)) {
                    // 获取接收方信息
                    User toUser = userService.getById(receiveUserId);
                    if (toUser != null) {
                        toUser = userService.setUserAvatar(toUser);
                        toRepeat.setAvatar(toUser.getPhotoUrl());
                    }
                }
                toRepeat.updateById();
            }
        }
        // 判断是否重复创建
        if (isRepeatCreate) {
            return ResultUtil.errorWithMessage("重复创建");
        }
        // 判断是否删除过房间
        if (isDelete || isToDelete) {
            return ResultUtil.successWithData(repeat);
        }


        // 生成会话消息记录id
        String sessionId = StringUtils.getUUID();
        // 设置用户头像
        user = userService.setUserAvatar(user);
        // 没有则创建会话
        room.setBelongUserId(user.getUid());
        room.setSessionId(sessionId);
        roomService.save(room);

        // 给对方也创建一个聊天会话
        Room room1 = Room.initRoom(MessageConstant.MESSAGE_TYPE_PRIVATE, room.getReceiveId(), user.getUid(), user.getAvatar(), user.getNickName());
        room1.setSessionId(sessionId);
        roomService.save(room1);

        return ResultUtil.successWithData(room);
    }

    @SubmitVerify
    @PostMapping("/deleteRoom")
    public String deleteRoom(@Validated({Delete.class}) @RequestBody RoomVO roomVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("[deleteRoom] 删除房间");
        return roomService.deleteRoom(roomVO);
    }

    /**
     * 撤回消息校验
     * @param imMessageVO
     * @return
     */
    @SubmitVerify
    @PostMapping("/withdrawMsg")
    public String withdrawMsg(@RequestBody ImMessage imMessageVO) {
        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage("用户不存在");
        }

        ImMessage imMessage = imService.getById(imMessageVO.getUid());
        if (imMessage == null) {
            return ResultUtil.errorWithMessage("消息撤回失败");
        }
        // 不能撤回别人的消息
        if (userUid == null || !userUid.equals(imMessage.getFromUserId())) {
            return ResultUtil.errorWithMessage("您无法撤回别人消息");
        }
        Date now = new Date();
        // 两个时间相差的秒数
        int second = DateUtils.getSecondByTwoDay(now, imMessage.getCreateTime());

        String json = sysParamsService.getSysParamsValueByKey(SysConf.MESSAGE_WITHDRAW_TIME);
        int secondConfig = Integer.parseInt(json);
        // 是否大于系统默认撤回时间
        if (second > secondConfig) {
            return ResultUtil.errorWithMessage("消息超过规定时长，撤回失败");
        }
        // 设置消息撤回
        imMessage.setIsWithdraw(Constants.NUM_ONE);
        imMessage.updateById();

        // 发布撤回时间
        domainEventUtil.publishEvent(EventAction.CHAT_CANCEL, imMessage);

        return ResultUtil.successWithMessage("消息撤回成功");
    }

    @PostMapping("/history")
    public String historyMsg(@RequestBody ImMessage imMessage) {
        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage("用户不存在");
        }

        IPage<ImMessage> imMessageIPage = imService.pageHistory(imMessage);

        // 标记已读
        imService.update().eq("room_id", imMessage.getRoomId()).eq("to_user_id", user.getUid()).set("is_read", true).update();
        return ResultUtil.successWithData(imMessageIPage);
    }

    @GetMapping("/roomList")
    public String roomList() {
        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage("用户不存在");
        }
        LambdaQueryWrapper<Room> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Room::getBelongUserId, user.getUid()).or().eq(Room::getBelongUserId, MessageConstant.MESSAGE_BELONG_PUBLIC);
        queryWrapper.eq(Room::getStatus, EStatus.ENABLE);
        List<Room> list = roomService.queryList(user.getUid(), queryWrapper);
        List<Room> resultList = new ArrayList<>();
        for (Room room : list) {
            if (room.getStatus() == EStatus.ENABLE) {
                resultList.add(room);
            }
        }
        return ResultUtil.successWithData(resultList);
    }
}
