package com.moxi.mogublog.web.chat.netty;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.entity.Room;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatGPTSetting;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatResponse;
import com.moxi.mogublog.commons.schema.ChatResponse.Choice;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.chat.holder.SessionManager;
import com.moxi.mogublog.web.chat.utils.RequestParamUtils;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.SensitiveUtils;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.MessageConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @auth: wly
 * @date: 2022/1/13
 * @descriptor:
 */
@Slf4j
public class WebsocketSimpleChannelHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private UserService userService;
    private RedisUtil redisUtils;
    private ImService imService;
    private SensitiveUtils sensitiveUtils;
    private RoomService roomService;
    private SysParamsService sysParamsService;
    private DomainEventUtil domainEventUtil;
    private NoticeService noticeService;
    private SmsFeignClient smsFeignClient;

    {
        userService = SpringUtils.getBean(UserService.class);
        redisUtils = SpringUtils.getBean(RedisUtil.class);
        imService = SpringUtils.getBean(ImService.class);
        sensitiveUtils = SpringUtils.getBean(SensitiveUtils.class);
        roomService = SpringUtils.getBean(RoomService.class);
        sysParamsService = SpringUtils.getBean(SysParamsService.class);
        domainEventUtil = SpringUtils.getBean(DomainEventUtil.class);
        noticeService = SpringUtils.getBean(NoticeService.class);
        smsFeignClient = SpringUtils.getBean(SmsFeignClient.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("-------------------------");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            httpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof TextWebSocketFrame) {
            websocketFrame(ctx, (TextWebSocketFrame) msg);
        } else if (msg instanceof CloseWebSocketFrame) {
            // 断开请求
            closeChannel(ctx);
        }
    }

    /**
     * 数据库插入一条通话申请
     *
     * @param ctx
     * @param imMessage
     */
    private void initCallMessage(ChannelHandlerContext ctx, ImMessage imMessage) {
        // 聊天级别消息
        imMessage.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DEFAULT);
        // 持久化私聊消息到数据库
        imService.save(imMessage);
    }

    /**
     * 更新通话消息状态
     *
     * @param imMessage
     * @param operatorType
     */
    private void updateCallMessageType(ImMessage imMessage, Integer operatorType) {
        imService.update().eq("uid", imMessage.getUid()).set("operator_type", operatorType).set("is_read", imMessage.isRead()).update();
    }

    /**
     * 更新通话时长
     *
     * @param imMessage
     */
    private void updateCallMessageDuration(ImMessage imMessage) {
        imService.update().eq("uid", imMessage.getUid()).set("duration", imMessage.getDuration()).update();
    }


    /**
     * 处理消息
     *
     * @param ctx
     * @param socketFrame
     */
    private void websocketFrame(ChannelHandlerContext ctx, TextWebSocketFrame socketFrame) {
        String text = socketFrame.text();

        // 封装一个错误返回消息
        ImMessage errMessage = new ImMessage();
        errMessage.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DANGER);
        errMessage.setCategory(MessageConstant.MESSAGE_CATEGORY_DEFAULT);
        try {
            // 解析消息
            ImMessage receiveMessage = JSON.parseObject(text, ImMessage.class);

            // 视频通话传输，直接转发
            if (MessageConstant.MESSAGE_TYPE_VIDEO_CALL == receiveMessage.getMessageType()) {
                // 消息发送人
                User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
                receiveMessage.initSendInfo(user);
                if (SessionManager.channelMap.containsKey(receiveMessage.getToUserId())) {
                    switch (receiveMessage.getType()) {
                        // 检查是否在线
                        case MessageConstant.CALL_TYPE_CHECK:
                            if (!SessionManager.channelMap.containsKey(receiveMessage.getToUserId())) {
                                // 对方不在线
                                receiveMessage.setMessage("offline");
                            } else {
                                receiveMessage.setMessage("online");
                            }
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(receiveMessage)));
                            return;
                        // 发起视频通话
                        case MessageConstant.CALL_TYPE_CALL:
                            initCallMessage(ctx, receiveMessage);
                            // 回执消息id，方便后续处理
                            ImMessage imMessage = imService.getById(receiveMessage.getUid());
                            imMessage.setMessageType(receiveMessage.getMessageType());
                            imMessage.setType(MessageConstant.CALL_TYPE_ID);
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));
                            break;
                        case MessageConstant.CALL_TYPE_ACCEPT:
                            // 接听
                            receiveMessage.setRead(true);
                            updateCallMessageType(receiveMessage, MessageConstant.MESSAGE_OPERATOR_ACCEPT);
                            break;
                        case MessageConstant.CALL_TYPE_REJECT:
                            // 拒接
                            receiveMessage.setRead(true);
                            updateCallMessageType(receiveMessage, MessageConstant.MESSAGE_OPERATOR_REJECT);
                            break;
                        case MessageConstant.CALL_TYPE_CANCEL:
                            // 发起方取消
                            receiveMessage.setRead(false);
                            updateCallMessageType(receiveMessage, MessageConstant.MESSAGE_OPERATOR_CANCEL);
                            break;
                        case MessageConstant.CALL_TYPE_HANG_UP:
                            // 挂断，更新通话时长
                            updateCallMessageDuration(receiveMessage);
                            break;
                    }

                    if (MessageConstant.CALL_TYPE_HANG_UP.equals(receiveMessage.getType()) || MessageConstant.CALL_TYPE_REJECT.equals(receiveMessage.getType()) || MessageConstant.CALL_TYPE_CANCEL.equals(receiveMessage.getType())) {
                        ImMessage imMessage = imService.getById(receiveMessage.getUid());
                        imMessage.setMessageType(receiveMessage.getMessageType());
                        imMessage.setType(receiveMessage.getType());
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));
                        Channel channel = SessionManager.channelMap.get(receiveMessage.getToUserId());
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));
                    } else {
                        Channel channel = SessionManager.channelMap.get(receiveMessage.getToUserId());
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(receiveMessage)));
                    }
                } else {
                    // 对方不在线
//                    ImMessage notifyMessage = ImMessage.notifyMessage("对方不在线");
                    errMessage.setMessage("对方不在线");
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                }

                return;
            }

            // 敏感消息内容过滤
            Map<String, String> sensitiveMap = sensitiveUtils.filter(receiveMessage.getMessage(),false, SysConf.SYS_SENSITIVE_WORD);
            if (sensitiveMap != null) {
                receiveMessage.setMessage(sensitiveMap.get(SysConf.CONTENT));
            }

            // 消息发送人
            User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
            // 获取用户激活状态
            Boolean userActiveStatue = userService.getUserActiveStatus(user.getUid());
            if (!userActiveStatue) {
                errMessage.setMessage("抱歉，您账号已封禁无法操作");
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                return;
            }


            // 如果是群组或者私聊的消息，需要加入的频控
            switch (receiveMessage.getMessageType()) {
                // 私聊消息
                case MessageConstant.MESSAGE_TYPE_PRIVATE:
                case MessageConstant.MESSAGE_TYPE_GROUP: {

                    String authCode = EAuthCode.PRIVATE_CHAT.getCode();
                    if (receiveMessage.getMessageType() == MessageConstant.MESSAGE_TYPE_GROUP) {
                        authCode = EAuthCode.PUBLIC_CHAT.getCode();
                    }
                    // 校验是否有对应的权限Code【没有对应的权限Code】
                    if(!user.getAuthCodeList().contains(authCode)) {
                        errMessage.setMessage("抱歉，您权限不足无法操作");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                        return;
                    }

                    String blockJson = redisUtils.get(RedisConf.CHAT_FREQUENTLY_BLOCK + Constants.SYMBOL_COLON + user.getUid());
                    int blockCount = 0;
                    if (StringUtils.isNotEmpty(blockJson)) {
                        blockCount = Integer.parseInt(blockJson);
                        if (blockCount >= 3) {
                            errMessage.setMessage("你操作过于频繁，已触达系统封禁，请稍后再试");
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                            return;
                        }
                    }

                    // 限流校验
                    String countJson = redisUtils.get(RedisConf.CHAT_FREQUENTLY + Constants.SYMBOL_COLON + user.getUid());
                    int count = 0;
                    if (StringUtils.isNotEmpty(countJson)) {
                        count = Integer.parseInt(countJson);
                        if (count > 20) {
                            blockCount++;
                            redisUtils.setEx(RedisConf.CHAT_FREQUENTLY_BLOCK + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(blockCount), 2, TimeUnit.HOURS);
                            errMessage.setMessage("你操作过于频繁，请稍后再试");
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                            return;
                        }
                    }
                    count ++;
                    redisUtils.setEx(RedisConf.CHAT_FREQUENTLY + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(count), 2, TimeUnit.MINUTES);

                } break;
            }


            // 判断聊天是否关闭
            String openChat = redisUtils.get(RedisConf.WEB_CONFIG + Constants.SYMBOL_COLON + RedisConf.OPEN_CHAT);
            if (!Constants.STR_ONE.equals(openChat)) {
                errMessage.setMessage("后台未开启聊天功能");
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                return;
            }

            // 判断类型消息类型，判断是否开启对应功能
            switch (receiveMessage.getCategory()) {
                // 是否语音功能
                case MessageConstant.MESSAGE_CATEGORY_AUDIO: {
                    String openRecord = redisUtils.get(RedisConf.WEB_CONFIG + Constants.SYMBOL_COLON + RedisConf.OPEN_RECORD);
                    if (!Constants.STR_ONE.equals(openRecord)) {
                        errMessage.setMessage("后台未开启语音功能");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                        return;
                    }
                }
                break;

                // 是否发送图片功能
                case MessageConstant.MESSAGE_CATEGORY_IMAGE: {
                    String openPicture = redisUtils.get(RedisConf.WEB_CONFIG + Constants.SYMBOL_COLON + RedisConf.OPEN_PICTURE);
                    if (!Constants.STR_ONE.equals(openPicture)) {
                        errMessage.setMessage("后台未开启图片功能");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                        return;
                    }
                }
                break;

                // 是否开启通话功能
                case MessageConstant.MESSAGE_CATEGORY_CALL: {
                    String openCall = redisUtils.get(RedisConf.WEB_CONFIG + Constants.SYMBOL_COLON + RedisConf.OPEN_CALL);
                    if (!Constants.STR_ONE.equals(openCall)) {
                        errMessage.setMessage("后台未开启通话功能");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                        return;
                    }
                }
                break;

                // 是否开启视频功能
                case MessageConstant.MESSAGE_CATEGORY_VEDIO: {
                    String openVedio = redisUtils.get(RedisConf.WEB_CONFIG + Constants.SYMBOL_COLON + RedisConf.OPEN_VEDIO);
                    if (!Constants.STR_ONE.equals(openVedio)) {
                        errMessage.setMessage("后台未开启视频功能");
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
                        return;
                    }
                }
                break;
            }

            switch (receiveMessage.getMessageType()) {
                // 私聊消息
                case MessageConstant.MESSAGE_TYPE_PRIVATE:
                    sendPrivateMessage(ctx, receiveMessage);
                    break;
                // 群组消息
                case MessageConstant.MESSAGE_TYPE_GROUP:
                    sendGroupMessage(ctx, receiveMessage);
                    break;
                // 阅读消息
                case MessageConstant.MESSAGE_TYPE_READ:
                    markRead(receiveMessage);
                    break;
                // 新回话通知
                case MessageConstant.MESSAGE_TYPE_NEW_ROOM:
                    // 新会话
                    newRoom(ctx, receiveMessage);
                    break;

                case MessageConstant.MESSAGE_TYPE_WITHDRAW_MESSAGE:
                    // 撤回消息
                    withdrawMsgHandle(ctx, receiveMessage);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public String simpleUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 新会话
     *
     * @param ctx
     * @param imMessage
     */
    private void newRoom(ChannelHandlerContext ctx, ImMessage imMessage) {
        // 封装一个错误返回消息
        ImMessage errMessage = new ImMessage();
        errMessage.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DANGER);
        errMessage.setCategory(MessageConstant.MESSAGE_CATEGORY_DEFAULT);

        User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
        if (user.getStatus() != EStatus.ENABLE) {
            errMessage.setMessage("用户不存在");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        // 不能私信自己
        if (user.getUid().equals(imMessage.getToUserId())) {
            errMessage.setMessage("不能私信自己");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }

        // 查重
        Room repeat = roomService.lambdaQuery().
                eq(Room::getBelongUserId, user.getUid()).
                eq(Room::getReceiveId, imMessage.getToUserId()).last("limit 1").one();

        Boolean isDelete = false;
        Boolean isToDelete = false;
        Boolean isRepeatCreate = false;

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
                eq(Room::getBelongUserId, imMessage.getToUserId()).last("limit 1").one();
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
//            return ResultUtil.errorWithMessage("重复创建");
            errMessage.setMessage("接入成功");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        // 判断是否删除过房间
        if (isDelete || isToDelete) {
            // 更新聊天室列表消息
            ImMessage updateMessage = ImMessage.notifyMessage(null);
            updateMessage.initSendInfo(user);
            updateMessage.setMessageType(MessageConstant.MESSAGE_TYPE_NEW_ROOM);
            updateMessage.setToUserId(imMessage.getToUserId());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(updateMessage)));
            // 对方是否在线
            if (SessionManager.channelMap.containsKey(imMessage.getToUserId())) {
                Channel channel = SessionManager.channelMap.get(imMessage.getToUserId());
                // 给对方发一条通知消息，更新聊天室列表
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(updateMessage)));
            }
            return;
        }

        // 生成会话消息记录id
        String sessionId = StringUtils.getUUID();
        // 设置用户头像
        user = userService.setUserAvatar(user);
        // 没有则创建会话
        Room room = Room.initRoom(MessageConstant.MESSAGE_TYPE_PRIVATE, user.getUid(), imMessage.getToUserId(), imMessage.getToUserAvatar(), imMessage.getToUserNickName());
        room.setBelongUserId(user.getUid());
        room.setSessionId(sessionId);
        roomService.save(room);

        // 给对方也创建一个聊天会话
        Room room1 = Room.initRoom(MessageConstant.MESSAGE_TYPE_PRIVATE, imMessage.getToUserId(), user.getUid(), user.getPhotoUrl(), user.getNickName());
        room1.setSessionId(sessionId);
        roomService.save(room1);

        imMessage.setRoomId(sessionId);
        imMessage.setFromUserId(user.getUid());
        imMessage.setFromUserNickName(user.getNickName());
        imMessage.setSendTime(new Date());
        imMessage.setFormUserAvatarUid(user.getAvatar());
        // 生成一条打招呼消息写入数据库
        imService.save(imMessage);

        // 更新聊天室列表消息
        ImMessage updateMessage = ImMessage.notifyMessage(null);
        updateMessage.setRoomId(sessionId);
        updateMessage.initSendInfo(user);
        updateMessage.setMessageType(MessageConstant.MESSAGE_TYPE_NEW_ROOM);
        updateMessage.setToUserId(imMessage.getToUserId());

        // 给发送人推一条，方便前端展示
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(updateMessage)));

        // 对方是否在线
        if (SessionManager.channelMap.containsKey(imMessage.getToUserId())) {
            Channel channel = SessionManager.channelMap.get(imMessage.getToUserId());
            // 给对方发一条通知消息，更新聊天室列表
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(updateMessage)));
        }
    }

    private void withdrawMsgHandle(ChannelHandlerContext ctx, ImMessage imMessageVO) {

        // 封装一个错误返回消息
        ImMessage errMessage = new ImMessage();
        errMessage.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DANGER);
        errMessage.setCategory(MessageConstant.MESSAGE_CATEGORY_DEFAULT);

        User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
        if (user.getStatus() != EStatus.ENABLE) {
            errMessage.setMessage("用户不存在");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }

        ImMessage imMessage = imService.getById(imMessageVO.getUid());
        if (imMessage == null) {
            errMessage.setMessage("消息撤回失败");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        // 不能撤回别人的消息
        String userUid = user.getUid();
        if (userUid == null || !userUid.equals(imMessage.getFromUserId())) {
            errMessage.setMessage("无法撤回别人消息");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        // 判断是否已经被撤回
        if(imMessage.getIsWithdraw() == Constants.NUM_ONE) {
            errMessage.setMessage("该消息已经撤回");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        Date now = new Date();
        // 两个时间相差的秒数
        int second = DateUtils.getSecondByTwoDay(now, imMessage.getCreateTime());

        String json = sysParamsService.getSysParamsValueByKey(SysConf.MESSAGE_WITHDRAW_TIME);
        Integer secondConfig = Integer.parseInt(json);
        // 是否大于系统默认撤回时间
        if (second > secondConfig) {
            errMessage.setMessage("消息发送过久，无法撤回");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(errMessage)));
            return;
        }
        // 设置消息撤回
        imMessage.setIsWithdraw(Constants.NUM_ONE);
        imMessage.updateById();

        // 设置消息类型为撤回
        imMessage.setMessageType(MessageConstant.MESSAGE_TYPE_WITHDRAW_MESSAGE);
        imMessage.setMessage("消息已撤回");

        // 发送群聊消息
        SessionManager.channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));
    }

    /**
     * 标记已读
     *
     * @param imMessage
     */
    private void markRead(ImMessage imMessage) {
        imService.update().eq("room_id", imMessage.getRoomId()).eq("to_user_id", imMessage.getToUserId()).set("is_read", true).update();
    }

    /**
     * 发送私聊消息
     *
     * @param imMessage
     */
    private void sendPrivateMessage(ChannelHandlerContext ctx, ImMessage imMessage) {
        String toUserId = imMessage.getToUserId();
        // 未指定接收人，肯定是前端的bug
        if (StrUtil.isEmpty(toUserId)) {
            return;
        }

        // 消息发送人
        User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
        imMessage.initSendInfo(user);

        // 持久化私聊消息到数据库
        imService.save(imMessage);

        // 给发送人推一条，方便前端展示
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));

        // 接收人是否在线
        if (SessionManager.channelMap.containsKey(toUserId)) {
            Channel channel = SessionManager.channelMap.get(toUserId);
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));
        } else {
            // 接收人不在线，往通知栏发小红点
            String redisKey = RedisConf.USER_RECEIVE_MESSAGE_COUNT + Constants.SYMBOL_COLON + toUserId;
            String count = redisUtils.get(redisKey);
            if (StringUtils.isNotEmpty(count)) {
                redisUtils.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtils.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
        }

        // 发送聊天类领域事件
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(SysConf.USER_UID, user.getUid());
        domainEventUtil.publishEvent(EventAction.CHAT_ADD, imMessage, false, extraMap);

        // 判断发送人是否在线
        if (SessionManager.channelMap.containsKey(user.getUid())) {
            Channel channel = SessionManager.channelMap.get(user.getUid());
            imMessage.setAtUserUid(toUserId);
            // 判断是否需要进行机器人回复
            robotHandler(imMessage, channel);
        }
    }

    /**
     * 发送群聊消息
     *
     * @param imMessage
     */
    private void sendGroupMessage(ChannelHandlerContext ctx, ImMessage imMessage) {

        // 消息发送人
        User user = SessionManager.channelUserMap.get(ctx.channel().id().asLongText());
        imMessage.initSendInfo(user);
        // 持久化群聊消息
        imService.save(imMessage);

        // 发送群聊消息
        SessionManager.channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)));

        // 删除redis中所有的小红点记录
        Set<String> messageKeySet = redisUtils.keys(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT + Constants.SYMBOL_STAR);
        redisUtils.delete(messageKeySet);

        // Redis插入群组通知消息
        redisUtils.setEx(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT, Constants.STR_ONE, 7, TimeUnit.DAYS);

        // 判断有哪些用户在线，往Redis增加一层小红点过滤
        Map<String, User> userMap = SessionManager.channelUserMap;
        for (String key : userMap.keySet()) {
            User item = userMap.get(key);
            if (item == null) {
                continue;
            }
            // 过滤掉自己发布的
            if (item.getUid().equals(imMessage.getFromUserId())) {
                continue;
            }
            // 添加群通知
            String redisKey = RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT + Constants.SYMBOL_COLON + item.getUid();
            redisUtils.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
        }

        // 发送领域事件
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put(SysConf.USER_UID, user.getUid());
        domainEventUtil.publishEvent(EventAction.CHAT_ADD, imMessage, false, extraMap);

        // 机器人处理Handler
        robotHandler(imMessage, null);
    }

    /**
     * 机器人处理Handler
     * @param imMessage
     */
    private void robotHandler(ImMessage imMessage, Channel privateChannel) {

        boolean isGroupChat = (privateChannel == null);

        if (StringUtils.isEmpty(imMessage.getAtUserUid())) {
            return;
        }
        Map<String, User> robotUserMap = userService.getRobotUserList();

        // 获取机器人对象
        User user = robotUserMap.get(imMessage.getAtUserUid());
        if (user != null) {
            log.info("{} 处理AT消息", user.getNickName());

            // 获取ChatGPT配置
            ChatGPTSetting chatGPTSetting = userService.checkRobotReply(imMessage.getFromUserId(), imMessage.getUserTag());

            String regex = "<a[^>]*>[^<]*<\\/a>";
            String result = imMessage.getMessage().replaceAll(regex, "");
            Document doc = Jsoup.parse(result);
            String question = doc.text();

            String atText = "<a href='" + userService.getUserCenterUrl(imMessage.getFromUserId()) + "' target='_blank' style='display: inline;'>&nbsp;@" + imMessage.getFromUserNickName() + "&nbsp;</a>";

            String chatGPTResult = "";
            // 是否开启
            if (chatGPTSetting == null || !chatGPTSetting.isOpenRobotReply()) {
                chatGPTResult = "抱歉，后台暂未开启智能问答，请联系管理员";
            } else {
                // 开启了问答，校验是否还有提问次数
                if (chatGPTSetting.isBlock()) {
                    chatGPTResult = "抱歉，今日机器人回复您的次数已达到上限，暂时无法提供问答服务";
                } else {
                    // chatGPTResult = HttpRequestUtil.askOpenAi(question, chatGPTSetting.getApiUrl(), chatGPTSetting.getApiKey(), imMessage.getFromUserId());
                    // chatGPTResult = chatGPTHandler(question, chatGPTSetting);
                    chatGPTResult = smsFeignClient.getTextAnswer(question);

                    // 没有回复内容，可能内部系统存在问题
                    if (StringUtils.isEmpty(chatGPTResult)) {
                        chatGPTResult = "抱歉，网络出现问题，请稍后再试";
                    } else {
                        chatGPTResult = chatGPTResult.replaceFirst("\\n", "").replaceFirst("\\n", "");

                        // redis计数器增加
                        redisUtils.setEx(RedisConf.CHATGPT_REPLY_COUNT + Constants.SYMBOL_COLON + imMessage.getFromUserId(), JsonUtils.objectToJson(chatGPTSetting.getLimitCount() + 1), 12, TimeUnit.HOURS);
                    }
                }
            }

            // 群聊需要加AT
            if (isGroupChat) {
                chatGPTResult = atText + chatGPTResult;
            }

            // 将markdown替换
            chatGPTResult = FileUtils.markdownToHtml(chatGPTResult);

            // 构建机器人回复消息
            ImMessage robotImMessage = new ImMessage();
            BeanUtil.copyProperties(imMessage, robotImMessage, SysConf.UID, SysConf.CREATE_TIME, SysConf.UPDATE_TIME);
            robotImMessage.setMessage(chatGPTResult);
            robotImMessage.setFromUserId(imMessage.getAtUserUid());
            robotImMessage.setToUserId(imMessage.getFromUserId());
            robotImMessage.setToUserNickName(imMessage.getFromUserNickName());
            robotImMessage.setFormUserAvatar(user.getPhotoUrl());
            robotImMessage.setFormUserAvatarUid(user.getAvatar());
            robotImMessage.setFromUserNickName(user.getNickName());
            robotImMessage.setUserTag(user.getUserTag());
            robotImMessage.setSendTime(new Date());
            robotImMessage.setCredits(user.getCredits());
            // 持久化机器人回复的消息
            imService.save(robotImMessage);

            // 通过channel是否为空，来判断是私聊还是群里
            if (privateChannel == null) {
                SessionManager.channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(robotImMessage)));
            } else {
                privateChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(robotImMessage)));
            }
        }

        // 如果没有命中机器人，可能是AT其它人，需要发送通知
        // 不是机器人，可能是AT的普通用户，需要给用户发送通知
        // 记录被AT的通知
        // 私聊不发消息通知，只有群里AT的时候才发
        if (!isGroupChat) {
            NoticeVO noticeVO = new NoticeVO();
            noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
            noticeVO.setBusinessType(EBusinessType.CHAT_AT_USER.getCode());
            noticeVO.setBusinessUid(imMessage.getUid());
            noticeVO.setUserUid(imMessage.getAtUserUid());
            noticeVO.setContent(imMessage.getMessage());
            noticeVO.setIsBlack(0);
            noticeService.addNotice(noticeVO);

            // 记录通知小红点
            String redisKey = RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + imMessage.getAtUserUid();
            String count = redisUtils.get(redisKey);
            if (StringUtils.isNotEmpty(count)) {
                redisUtils.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtils.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
        }
    }

    /**
     * 处理http请求
     *
     * @param ctx
     * @param request
     */
    private void httpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        Map<String, String> connectParams = RequestParamUtils.urlSplit(request.uri());
        String token = connectParams.get("token");
        // 从token中获取用户uid
        User user = null;
        String userInfo = redisUtils.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        if (!StringUtils.isEmpty(userInfo)) {
            Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
            log.info("解析出来的用户:{}", map.get(SysConf.NICK_NAME));
            String userUid = map.get(SysConf.UID).toString();
            user = userService.getById(userUid);
            if (user == null) {
                log.info("用户不存在");
                // 没有传用户id，返回错误信息并断开连接
                sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }
            // 设置ip属地
            String ip = user.getLastLoginIp();
            if (StringUtils.isNotEmpty(ip)) {
                user.setUserIpPossession(IpUtils.getIpPossession(ip));
            }

            if (map.get("photoUrl") != null) {
                user.setPhotoUrl(map.get("photoUrl").toString());
            }
            // 判断用户是否被封号
            if (Constants.NUM_ONE == user.getCommentStatus()) {
                // 未封号
                redisUtils.setEx(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + userUid, Constants.STR_ONE, 7, TimeUnit.DAYS);
            } else {
                // 已封号
                redisUtils.setEx(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + userUid, Constants.STR_ZERO, 7, TimeUnit.DAYS);
            }
        }
        if (user == null) {
            log.info("用户没有登录，非法连接channel: {}", ctx.channel().id().asLongText());
            // 没有传用户id，返回错误信息并断开连接
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        } else {
            String wsUrl = "ws://" + request.headers().get(HttpHeaders.Names.HOST) + request.uri();
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(wsUrl, null, false);
            WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                // 开始执行握手
                handshaker.handshake(ctx.channel(), request);
                String id = user.getUid();
                // 绑定用户与channel
                SessionManager.channelMap.put(id, ctx.channel());
                SessionManager.channelUserMap.put(ctx.channel().id().asLongText(), user);
                // 保存已连接的channel，方便群发消息
                SessionManager.channelGroup.add(ctx.channel());
                // 在群消息中发送用户上线提醒
//                ImMessage imMessage = ImMessage.defaultGroupMessage("用户 <span style='font-weight: bold'>" + user.getNickName() + "</span> 已上线");
                // 通过 ChannelMatcher 过滤当前连接的channel
//                SessionManager.channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessage)), channel -> !ctx.channel().id().asLongText().equals(channel.id().asLongText()));
                log.info("用户:{} 已上线", user.getNickName());
                // TODO 可扩展  上线提醒  可以加入站内消息提醒，提示特殊关注用户已上线
            }
        }
    }

    /**
     * 服务端向客户端响应消息
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse response) {
        if (response.status().code() != 200) {
            //创建源缓冲区
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            //将源缓冲区的数据传送到此缓冲区
            response.content().writeBytes(byteBuf);
            //释放源缓冲区
            byteBuf.release();
        }
        //写入请求，服务端向客户端发送数据
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (response.status().code() != 200) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("收到客户端连接请求，channel: {}, 当前在线用户数量: {}", ctx.channel().id().asLongText(), SessionManager.channelGroup.size());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接: {}", ctx.channel().id().asLongText());
        closeChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常断开channel: {}", ctx.channel().id().asLongText());
        closeChannel(ctx);
    }

    /**
     * 关闭通道
     *
     * @param ctx
     */
    private void closeChannel(ChannelHandlerContext ctx) {
        // 移除map
        User user = SessionManager.channelUserMap.remove(ctx.channel().id().asLongText());
        if (user != null) {
            SessionManager.channelMap.remove(user.getUid());
        }
        SessionManager.channelGroup.remove(ctx.channel());
        ctx.close();
        log.info("当前在线用户数量: {}", SessionManager.channelGroup.size());
    }

}