package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.enums.EUserLevel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;

import static com.moxi.mougblog.base.global.MessageConstant.*;


@Data
@TableName("t_im_message")
public class ImMessage extends SuperEntity<ImMessage> {

    // 聊天室会话id
    private String roomId;

    // 消息发送人
    private String fromUserId;

    // 发送人昵称
    private String fromUserNickName;

    // 发送人头像uid
    private String formUserAvatarUid;

    // 消息级别(聊天消息/公告/通知/警告)
    private int messageLevel;

    // 消息内容
    private String message;

    // 消息类别(文本消息、语音消息、图片消息、视频通话、语音通话)
    private int category;

    // 操作类型(已接听/拒接/已取消/未接听)
    private Integer operatorType;

    // 语音长度(秒)
    private long duration;

    /**
     * 被AT的用户
     */
    @TableField(exist = false)
    private String atUserUid;

    // 消息发送时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    // 是否已读
    @TableField("is_read")
    private boolean isRead;

    /**
     * 是否撤回，0：否，1：是
     */
    private int isWithdraw;

    // 消息接收人
    private String toUserId;

    // 消息类别(私聊/群聊/回执)
    private int messageType;

    // ============== 新会话冗余字段 ================
    @TableField(exist = false)
    private String toUserAvatar;

    @TableField(exist = false)
    private String toUserNickName;
    // ============== 新会话冗余字段 ================

    // 当前页
    @TableField(exist = false)
    private int page;

    // 页码
    @TableField(exist = false)
    private int pageSize;

    // 发送人头像
    @TableField(exist = false)
    private String formUserAvatar;

    // 发送人标签
    @TableField(exist = false)
    private Integer userTag;

    // 发送人积分
    @TableField(exist = false)
    private Integer credits;

    // 发送人ip属地
    @TableField(exist = false)
    private String userIpPossession;

    /**
     * 用户等级
     */
    @TableField(exist = false)
    private Integer userLevel;

    /**
     * 用户性别
     */
    @TableField(exist = false)
    private String gender;

    /**
     * 发送人
     */
    @TableField(exist = false)
    private User fromUser;

    /**
     * 接收人
     */
    @TableField(exist = false)
    private User toUser;

    /**
     * 默认私聊消息
     * @param message
     * @return
     */
    public static ImMessage defaultPrivateMessage(String message) {
        return buildMessage(MESSAGE_LEVEL_DEFAULT, message);
    }

    /**
     * 默认群聊消息
     * @param message
     * @return
     */
    public static ImMessage defaultGroupMessage(String message) {
        return buildMessage(MESSAGE_LEVEL_DEFAULT, message);
    }

    /**
     * 警告消息
     * @param message
     * @return
     */
    public static ImMessage warningMessage(String message) {
        return buildMessage(MESSAGE_LEVEL_WARNING, message);
    }

    private static ImMessage buildMessage(int messageLevel, String message) {
        ImMessage imMessage = new ImMessage();
        imMessage.setMessageLevel(messageLevel);
        imMessage.setMessage(message);
        return imMessage;
    }

    /**
     * 通知消息
     * @param message
     * @return
     */
    public static ImMessage notifyMessage(String message) {
        return buildMessage(MESSAGE_LEVEL_NOTICE, message);
    }

    public void initSendInfo(User user) {
        this.setFromUserId(user.getUid());
        this.setFormUserAvatar(user.getPhotoUrl());
        this.setFormUserAvatarUid(user.getAvatar());
        this.setFromUserNickName(user.getNickName());
        this.setUserTag(user.getUserTag());
        this.setUserLevel(EUserLevel.getLvByExpValue(user.getExpValue()).getLevel());
        this.setGender(user.getGender());
        this.setCredits(user.getCredits());
        this.setUserIpPossession(user.getUserIpPossession());
        this.setSendTime(new Date());
    }

    // ====================视频通话建立通道信息字段======================
    // call/accept/hang-up/ice/offer/answer
    @TableField(exist = false)
    private String type;

    // sdp -> v=0
    //o=- 4145098382694464504 3 IN IP4 127.0.0.1
    //s=-
    //t=0 0
    //a=group:BUNDLE 0 1
    //a=extmap-allow-mixed
    //a=msid-semantic: WMS h5AQ8k1Ad0BTeNiULRCNNStBDlA6OBCFzbCp
    //m=audio 32972 UDP/TLS/RTP/SAVPF 111 63 103 104 9 0 8 106 105 13 110 112 113 126
    //c=IN IP4 172.19.0.1
    //a=rtcp:9 IN IP4 0.0.0.0
    //a=candidate:4062063367 1 udp 2122260223 172.19.0.1 32972 typ host generation 0 network-id 1
    //a=candidate:4031757670 1 udp 2122194687 192.168.0.160 41685 typ host generation 0 network-id 2
    //a=candidate:3366978808 1 udp 2122124031 2001:0:c38c:c38c:ccf:25de:8ec6:ea0e 58052 typ host generation 0 network-id 3
    //a=candidate:3164461047 1 tcp 1518280447 172.19.0.1 9 typ host tcptype active generation 0 network-id 1
    //a=candidate:3201057174 1 tcp 1518214911 192.168.0.160 9 typ host tcptype active generation 0 network-id 2
    //a=candidate:2251552776 1 tcp 1518144255 2001:0:c38c:c38c:ccf:25de:8ec6:ea0e 9 typ host tcptype active generation 0 network-id 3
    //a=ice-ufrag:jxm3
    //a=ice-pwd:oOSuxjiZBn7h+taEuxGjLBKI
    //a=ice-options:t
    @TableField(exist = false)
    private String sdp;

}
