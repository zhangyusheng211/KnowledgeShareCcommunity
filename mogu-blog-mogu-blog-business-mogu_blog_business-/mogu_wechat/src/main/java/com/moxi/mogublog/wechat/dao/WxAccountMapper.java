package com.moxi.mogublog.wechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxi.mogublog.wechat.entity.WxAccount;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号账号
 *
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface WxAccountMapper extends BaseMapper<WxAccount> {

}
