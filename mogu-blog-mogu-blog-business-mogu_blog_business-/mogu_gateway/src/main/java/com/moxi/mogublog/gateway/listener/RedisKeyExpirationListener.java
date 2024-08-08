package com.moxi.mogublog.gateway.listener;


import com.moxi.mogublog.gateway.config.IpBloomFilterConfig;
import com.moxi.mogublog.gateway.filter.IpFilterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis key过期监听
 *
 * @author 遇见
 */
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private IpFilterManager ipFilterManager;

    /**
     * Redis过期监听
     *
     * @param listenerContainer
     * @param ipFilterManager
     */
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, IpFilterManager ipFilterManager) {
        super(listenerContainer);
        this.ipFilterManager = ipFilterManager;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 获取过期的key
        String expireKey = message.toString();
        if (expireKey.contains(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST)) {
            ipFilterManager.reload();
        }
    }

}
