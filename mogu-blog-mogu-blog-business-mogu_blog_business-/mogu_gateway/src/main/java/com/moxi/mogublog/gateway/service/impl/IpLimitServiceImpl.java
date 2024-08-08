package com.moxi.mogublog.gateway.service.impl;

import com.moxi.mogublog.gateway.filter.IpFilterManager;
import com.moxi.mogublog.gateway.service.IpLimitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 遇见
 */
@Service
public class IpLimitServiceImpl implements IpLimitService {

    @Resource
    IpFilterManager ipFilterManager;

    @Override
    public void addBlackIp(String ip, Long time) {
        ipFilterManager.addIp(ip, 1, time);
    }

    @Override
    public void removeBlackIp(String ip) {
        ipFilterManager.removeIp(ip);
    }


}
