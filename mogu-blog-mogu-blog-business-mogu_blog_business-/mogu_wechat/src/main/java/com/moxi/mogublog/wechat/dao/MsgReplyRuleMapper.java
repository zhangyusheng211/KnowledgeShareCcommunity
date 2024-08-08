package com.moxi.mogublog.wechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moxi.mogublog.wechat.entity.MsgReplyRule;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface MsgReplyRuleMapper extends BaseMapper<MsgReplyRule> {
}
