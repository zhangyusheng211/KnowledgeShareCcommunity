package com.moxi.mogublog.gateway.filter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.moxi.mogublog.gateway.config.IpBloomFilterConfig;
import com.moxi.mogublog.gateway.config.IpLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于布隆过滤器判断黑名单用户
 * 该类用于维持网关与缓存访问之前的IP恶意攻击判断过滤
 * 解决问题：
 * 解决即使IP被加入黑名单,该IP任能访问系统redis资源造成频繁的IO查询
 *
 * @author 遇见
 */
@Slf4j
public class IpFilterManager {

    private RedisTemplate redisTemplate;

    private IpLimiter ipLimiter;

    /**
     * IP黑名单长久对象
     * expectedInsertions 期望添加的数据个数 目前使用1000
     * 后续数据做大 需扩容
     */
    private static BloomFilter<String> blackFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000, 0.01);


    public IpFilterManager(RedisTemplate redisTemplate, IpLimiter limiter) {
        this.redisTemplate = redisTemplate;
        this.ipLimiter = limiter;
    }

    /**
     * 初始化
     */
    public void init() {
        Set<String> blackList = redisTemplate.keys(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST.concat(IpBloomFilterConfig.PATTERN));
        log.info("init IpFilterManager");
        if (blackList != null) {
            blackList.forEach(key -> {
                String ip = key.replace(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST, "");
                Integer times = (Integer) redisTemplate.opsForValue().get(key);
                if (times >= ipLimiter.getLimitBlackCount()) {
                    log.info(ip);
                    blackFilter.put(ip);
                }
            });
        }
    }

    /**
     * 重新加载
     */
    public void reload() {
        blackFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 1000, 0.01);
        this.init();
    }

    /**
     * 移除黑名单
     *
     * @param ip
     */
    public void removeIp(String ip) {
        redisTemplate.opsForValue().decrement(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST.concat(ip));
        this.reload();
    }

    /**
     * 新增黑名单
     *
     * @param ip         ip
     * @param value      值
     * @param expireTime 超时时间
     */
    public void addIp(String ip, Integer value, Long expireTime) {
        log.warn("加入黑名单{}", ip);
        redisTemplate.opsForValue().set(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST.concat(ip), value, expireTime, TimeUnit.MINUTES);
        this.reload();
    }

    /**
     * 使用布隆过滤器检测 存在与否
     *
     * @param ip
     * @return
     */
    public Boolean checkIp(String ip) {
        return blackFilter.mightContain(ip);
    }

    /**
     * 检测并新增
     */
    public void checkAndAddBlackIp(String ip) {
        // 判断用户是否在全局黑名单中
        Object blackList = redisTemplate.opsForValue().get(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST + ip);
        if (blackList != null) {
            Integer limitCount = (Integer) blackList;
            if (limitCount >= ipLimiter.getLimitBlackCount()) {
                blackFilter.put(ip);
            }
            redisTemplate.opsForValue().increment(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST + ip);
        } else {
            redisTemplate.opsForValue().set(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST + ip, 1, 30, TimeUnit.MINUTES);
        }
    }
}
