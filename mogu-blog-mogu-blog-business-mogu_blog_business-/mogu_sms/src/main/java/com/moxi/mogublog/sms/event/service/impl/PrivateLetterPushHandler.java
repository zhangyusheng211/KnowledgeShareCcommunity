package com.moxi.mogublog.sms.event.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.entity.Room;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.sms.event.annotation.MessagePushType;
import com.moxi.mogublog.sms.event.service.AbstractMessagePushHandler;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EPushMethod;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.MessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私信推送事件处理器
 */
@Slf4j
@Component
@MessagePushType({EPushMethod.PRIVATE_LETTER})
public class PrivateLetterPushHandler extends AbstractMessagePushHandler {

    @Override
    public void pushHandler(MessagePush messagePush, List<User> userList) {
        // 站内信推送
        log.error("开始推送站内信触达，标题：{}", messagePush.getTitle());

        // 获取系统通知用户
        Map<String, User> robotUserMap = userService.getRobotUserList();
        // 获取一个包含问答的机器人【通知机器人】
        User noticeUser = null;
        for (User user: robotUserMap.values()) {
            // TODO 获取一个名称中包含通知的机器人【暂时这样设计】
            if (StringUtils.isNotEmpty(user.getNickName()) && user.getNickName().contains("通知")) {
                noticeUser = user;
            }
        }

        if (noticeUser == null) {
            log.error("未查询到通知机器人，无法进行私信触达到");
            return;
        }

        noticeUser = userService.setUserAvatar(noticeUser);

        // 开始批量发送站内信
        for (User user : userList) {

            // 判断是否存在聊天房间
            Room room = addRoom(noticeUser, user);

            // 构建机器人回复消息
            ImMessage message = new ImMessage();
            message.setMessage(messagePush.getContent());
            message.setToUserId(user.getUid());
            message.setToUserNickName(user.getNickName());
            message.setFromUserId(noticeUser.getUid());
            message.setFormUserAvatar(noticeUser.getPhotoUrl());
            message.setFormUserAvatarUid(noticeUser.getAvatar());
            message.setFromUserNickName(noticeUser.getNickName());
            message.setUserTag(noticeUser.getUserTag());
            message.setSendTime(new Date());
            message.setRoomId(room.getSessionId());
            message.setMessageLevel(MessageConstant.MESSAGE_TYPE_READ);
            message.setCategory(MessageConstant.MESSAGE_CATEGORY_DEFAULT);
            message.setMessageType(MessageConstant.MESSAGE_TYPE_PRIVATE);
            message.setCredits(noticeUser.getCredits());
            // 持久化机器人回复的消息
            imService.save(message);
        }
        log.error("站内信触达推送完成");
    }

    /**
     * 添加房间
     */
    public Room addRoom(User user, User toUser) {
        // 在看看对方的房间是否创建了
        Room toRepeat = roomService.lambdaQuery().
                eq(Room::getReceiveId, user.getUid()).
                eq(Room::getBelongUserId, toUser.getUid()).last("limit 1").one();
        if (toRepeat != null) {
            // 原来删除过，直接还原
            if (toRepeat.getStatus() == EStatus.DISABLED) {
                toRepeat.setStatus(EStatus.ENABLE);
                // 通知更新一下接收方的头像
                if (StringUtils.isNotEmpty(user.getPhotoUrl())) {
                    toRepeat.setAvatar(user.getPhotoUrl());
                }
                toRepeat.updateById();
            }
            return toRepeat;
        }
        // 创建房间
        // 生成会话消息记录id
        String sessionId = StringUtils.getUUID();
        Room room = new Room();
        room.setRoomType(MessageConstant.MESSAGE_TYPE_PRIVATE);
        room.setBelongUserId(toUser.getUid());
        room.setReceiveId(user.getUid());
        room.setAvatar(user.getPhotoUrl());
        room.setTitle(user.getNickName());
        room.setSessionId(sessionId);
        roomService.save(room);
        return room;
    }
}
