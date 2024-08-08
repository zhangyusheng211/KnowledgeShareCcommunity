package com.moxi.mogublog.web.chat.holder;

import com.moxi.mogublog.commons.entity.User;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    /**
     * 存储 channel 对象
     */
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存储用户id和channel对应关系
     */
    public static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 方便断开连接时删除关系
     */
    public static Map<String, User> channelUserMap = new ConcurrentHashMap<>();

}
