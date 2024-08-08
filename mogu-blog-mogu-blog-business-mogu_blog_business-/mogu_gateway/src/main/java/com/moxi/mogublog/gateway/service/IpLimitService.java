package com.moxi.mogublog.gateway.service;

/**
 * @author 遇见
 */

public interface IpLimitService {
    /**
     * 添加黑名单
     *
     * @param ip
     * @param time
     */
    public void addBlackIp(String ip, Long time);

    /**
     * 移除黑名单
     *
     * @param ip
     */
    public void removeBlackIp(String ip);
}
