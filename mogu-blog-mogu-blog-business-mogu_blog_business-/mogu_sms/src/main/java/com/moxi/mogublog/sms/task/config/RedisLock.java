package com.moxi.mogublog.sms.task.config;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;

/**
 * redis 锁
 */
public class RedisLock {
    private static String name = ManagementFactory.getRuntimeMXBean().getName();
    private static String pid = name.split("@")[0];

    private RedisTemplate<String, String> redisTemplate;
    private static final String LOCK_LUA = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('expire', KEYS[1], ARGV[2]) return 'true' else return 'false' end";
    private static final String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) end return 'true' ";
    private RedisScript<String> lockRedisScript;
    private RedisScript<String> unLockRedisScript;
    private RedisSerializer<String> argsSerializer;
    private RedisSerializer<String> resultSerializer;

    /**
     * 初始化lua 脚本
     */
    public void init(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        argsSerializer = new StringRedisSerializer();
        resultSerializer = new StringRedisSerializer();
        lockRedisScript = RedisScript.of(LOCK_LUA, String.class);
        unLockRedisScript = RedisScript.of(UNLOCK_LUA, String.class);
    }

    private boolean lock(String lock, String val, int second) {
        List<String> keys = Collections.singletonList(lock);
        String flag = redisTemplate.execute(lockRedisScript, argsSerializer, resultSerializer, keys, val,
                String.valueOf(second));
        return Boolean.valueOf(flag);
    }

    /**
     * 默认等待2秒,10秒自动解锁
     */
    public boolean lock(String key) {
        return lock(key, 2);
    }

    /**
     * 默认10秒自动解锁
     */
    public boolean lock(String key, int wait) {
        return lock(key, wait, 10);
    }

    public boolean lock(String key, int wait, int releaseSeconds) {
        int failCount = 0;
        String val = pid + ":" + Thread.currentThread().getName();
        while (failCount <= wait * 10) {
            // 等待100ms重试
            if (lock(key, val, releaseSeconds)) {
                // 加锁成功
                return true;
            } else {
                failCount++;
            }
            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new BusinessException("RedisLock fail:" + val);
    }

    public void unlock(String lock) {
        unlock(lock, pid + ":" + Thread.currentThread().getName());
    }

    private void unlock(String key, String val) {
        List<String> keys = Collections.singletonList(key);
        redisTemplate.execute(unLockRedisScript, argsSerializer, resultSerializer, keys, val);
    }
}