package com.moxi.mogublog.web.chat.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @auth: wly
 * @date: 2022/1/13
 * @descriptor:
 */
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private WebsocketServer websocketServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent().getParent() == null) {
            try {
                // 新建一个子线程，不阻塞主线程
                new Thread(() -> {
                    try {
                        websocketServer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}