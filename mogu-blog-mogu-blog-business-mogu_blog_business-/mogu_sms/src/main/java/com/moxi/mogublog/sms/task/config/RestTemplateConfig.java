package com.moxi.mogublog.sms.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RedisLock redisLock(RedisTemplate<String, String> redisTemplate) {
        RedisLock redisLock = new RedisLock();
        redisLock.init(redisTemplate);
        return redisLock;
    }
}