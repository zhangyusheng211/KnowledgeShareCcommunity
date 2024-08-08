package com.moxi.mogublog.gateway.config;

import com.moxi.mogublog.gateway.filter.IpFilterManager;
import com.moxi.mogublog.gateway.listener.RedisKeyExpirationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * @author 遇见
 */
@Configuration
public class IpBloomFilterConfig {

    public static String SIGN = ":";

    public static String IP_LIMIT_FILTER = "IP_LIMIT_FILTER:";

    /**
     * ip请求黑名单
     */
    public static String IP_LIMIT_BLACK_LIST = "IP_LIMIT_BLACK_LIST:";

    public static String PATTERN = "*";

    /**
     * redis 消息监听容器
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    /**
     * 初始化IP过滤管理器
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public IpFilterManager ipFilterManager(RedisTemplate redisTemplate, IpLimiter limiter) {
        IpFilterManager ipFilterManager = new IpFilterManager(redisTemplate, limiter);
        ipFilterManager.init();
        return ipFilterManager;
    }

    /**
     * 重写redis过期监听
     *
     * @param listenerContainer
     * @return
     */
    @Bean
    public RedisKeyExpirationListener initListener(RedisMessageListenerContainer listenerContainer, IpFilterManager ipFilterManager) {
        return new RedisKeyExpirationListener(listenerContainer, ipFilterManager);
    }
}
