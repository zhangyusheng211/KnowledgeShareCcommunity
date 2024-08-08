package com.moxi.mogublog.web.chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @auth: wly
 * @date: 2022/1/13
 * @descriptor:
 */
@Slf4j
@Component
public class WebsocketServer {

    private final int port = 8608;

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootStrap = new ServerBootstrap();
            serverBootStrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootStrap.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            // 以块的方式来写的处理器
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));
                            ch.pipeline().addLast(new WebsocketSimpleChannelHandler());
                            ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536 * 10));
                        }
                    });
            ChannelFuture channelFuture = serverBootStrap.bind().sync();
            log.info("NettyServer启动，正在监听： {}", channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }

}