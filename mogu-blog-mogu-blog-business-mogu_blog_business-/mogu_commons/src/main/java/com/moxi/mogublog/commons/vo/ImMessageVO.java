package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
public class ImMessageVO extends BaseVO<ImMessageVO> {

    // 聊天室会话id
    private String roomId;

    // 消息发送人
    private String fromUserId;

    // 发送人昵称
    private String fromUserNickName;

    // 发送人头像uid
    private String formUserAvatarUid;

    // 消息级别(聊天消息/公告/通知/警告)
    private Integer messageLevel;

    // 消息内容
    private String message;

    // 消息类别(文本消息、语音消息、图片消息)
    private Integer category;

    // 语音长度(秒)
    private long duration;

    // 消息发送时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    // 是否已读
    private boolean isRead;

    // 消息接收人
    private String toUserId;

    // 消息类别(私聊/群聊/回执)
    private Integer messageType;

    // 发送人头像
    private String formUserAvatar;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;
}
