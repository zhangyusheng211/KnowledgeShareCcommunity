package com.moxi.mogublog.commons.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("t_room")
public class Room extends SuperEntity<Room> {

    // 类型 群组/个人
    private int roomType;

    // 会话所属用户
    private String belongUserId;

    // 接收消息方
    private String receiveId;

    // 会话头像
    private String avatar;

    // 会话标题
    private String title;

    // 会话消息记录id
    private String sessionId;

    // 最后一条消息的发送人
    @TableField(exist = false)
    private String fromUserNickName;

    // 显示最后一条消息
    @TableField(exist = false)
    private String subtitle;

    // 时间
    @TableField(exist = false)
    private Date time;

    @TableField(exist = false)
    private Integer unread;


    /**
     * 归属的用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 接收的用户
     */
    @TableField(exist = false)
    private User toUser;

    /**
     * @param roomType 类型 1v1 / 群聊
     * @param belongUserId  所属用户
     * @param receiveUserId  接收用户信息
     * @return
     */
    public static Room initRoom(int roomType, String belongUserId, String receiveUserId, String receiveUserAvatar, String title) {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setBelongUserId(belongUserId);
        room.setReceiveId(receiveUserId);
        room.setAvatar(receiveUserAvatar);
        room.setTitle(title);
        return room;
    }

}
