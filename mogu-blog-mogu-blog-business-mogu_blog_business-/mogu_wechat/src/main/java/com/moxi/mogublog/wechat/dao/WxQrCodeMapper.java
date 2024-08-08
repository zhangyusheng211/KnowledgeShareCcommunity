package com.moxi.mogublog.wechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxi.mogublog.wechat.entity.WxQrCode;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号带参二维码
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2020-01-02 11:11:55
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface WxQrCodeMapper extends BaseMapper<WxQrCode> {

}