package com.moxi.mogublog.gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 遇见
 */
@Configuration
@Data
public class IpLimiter {
    /**
     * 限流时间(单位：分钟)
     */
    @Value(value = "${IpLimiter.expireTime:1}")
    private Integer expireTime;

    /**
     * 限流次数
     */
    @Value(value = "${IpLimiter.limitTimes:100}")
    private Integer limitTimes;

    /**
     * 进入全局黑名单次数
     */
    @Value(value = "${IpLimiter.limitBlackCount:10}")
    private Integer limitBlackCount;

}
