package com.moxi.mogublog.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moxi.mogublog.wechat.common.utils.PageUtils;
import com.moxi.mogublog.wechat.entity.WxAccount;

import java.util.Map;

/**
 * 公众号账号
 *
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
public interface WxAccountService extends IService<WxAccount> {
    /**
     * 分页查询用户数据
     *
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    @Override
    boolean save(WxAccount entity);
//
//    @Override
//    boolean removeByIds(Collection<?> idList);
}

