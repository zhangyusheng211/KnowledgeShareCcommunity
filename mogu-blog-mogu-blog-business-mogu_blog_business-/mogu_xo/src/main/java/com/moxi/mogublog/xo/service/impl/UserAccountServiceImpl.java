package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mogublog.xo.mapper.UserAccountMapper;
import com.moxi.mogublog.xo.service.UserAccountService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户三方账号管理表
 *
 * @author 遇见
 */
@Service
public class UserAccountServiceImpl extends SuperServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Override
    public UserAccount queryRelation(String source, String uuid) {
        return getOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getSource, source)
                        .eq(UserAccount::getAccountId, uuid)
                        .eq(UserAccount::getStatus, EStatus.ENABLE)
        );
    }
}
