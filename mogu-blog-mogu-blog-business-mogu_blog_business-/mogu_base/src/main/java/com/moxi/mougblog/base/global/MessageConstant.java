package com.moxi.mougblog.base.global;

public class MessageConstant {

    // 公共所属
    public static final int MESSAGE_BELONG_PUBLIC = 10000;
    // 私聊消息
    public static final int MESSAGE_TYPE_PRIVATE = 10001;
    // 群聊消息
    public static final int MESSAGE_TYPE_GROUP = 10002;
    // 已读回执
    public static final int MESSAGE_TYPE_READ = 10003;
    // 通话
    public static final int MESSAGE_TYPE_VIDEO_CALL = 10008;
    // 新会话通知
    public static final int MESSAGE_TYPE_NEW_ROOM = 10010;
    // 撤回消息通知
    public static final int MESSAGE_TYPE_WITHDRAW_MESSAGE = 10011;

    // 聊天类别-文本消息
    public static final int MESSAGE_CATEGORY_DEFAULT = 20001;
    // 聊天类别-语音消息
    public static final int MESSAGE_CATEGORY_AUDIO = 20002;
    // 聊天类别-图片消息
    public static final int MESSAGE_CATEGORY_IMAGE = 20003;
    // 聊天类别-视频通话
    public static final int MESSAGE_CATEGORY_CALL = 20004;
    // 聊天类别-语音通话
    public static final int MESSAGE_CATEGORY_VEDIO = 20005;

    // 操作类型 接听 拒接 挂断 未接听 对方已取消
    public static final int MESSAGE_OPERATOR_ACCEPT = 30001;
    public static final int MESSAGE_OPERATOR_REJECT = 30002;
    public static final int MESSAGE_OPERATOR_HANG_UP = 30003;
    public static final int MESSAGE_OPERATOR_NO_ACCEPT = 30004;
    public static final int MESSAGE_OPERATOR_CANCEL = 30005;

    // 消息级别-聊天消息
    public static final int MESSAGE_LEVEL_DEFAULT = 40001;
    // 消息级别-公告
    public static final int MESSAGE_LEVEL_ANNOUNCEMENT = 40002;
    // 消息级别-通知
    public static final int MESSAGE_LEVEL_NOTICE = 40003;
    // 消息级别-警告
    public static final int MESSAGE_LEVEL_WARNING = 40004;
    // 消息级别-错误
    public static final int MESSAGE_LEVEL_DANGER = 40005;

    // 消息级别-领域事件消息
    public static final int MESSAGE_LEVEL_DOMAIN_EVENT = 40010;


    // 检查在线
    public static final String CALL_TYPE_CHECK = "check-online";
    // 通话申请
    public static final String CALL_TYPE_CALL = "call";
    // 拒接
    public static final String CALL_TYPE_REJECT = "reject";
    // 接受
    public static final String CALL_TYPE_ACCEPT = "accept";
    // 挂断
    public static final String CALL_TYPE_HANG_UP = "hang-up";
    // 取消
    public static final String CALL_TYPE_CANCEL = "cancel";
    public static final String CALL_TYPE_ICE = "ice";
    public static final String CALL_TYPE_OFFER = "offer";
    public static final String CALL_TYPE_ANSWER = "answer";
    public static final String CALL_TYPE_ID = "id";

}
