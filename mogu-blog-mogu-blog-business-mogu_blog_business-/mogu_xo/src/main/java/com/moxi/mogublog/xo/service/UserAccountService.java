package com.moxi.mogublog.xo.service;

import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mougblog.base.service.SuperService;

/**
 * 用户账号管理
 *
 * @author 遇见
 */
public interface UserAccountService extends SuperService<UserAccount> {
    /**
     * 根据来源编号与第三方uid查询
     *
     * @param source
     * @param uuid
     * @return
     */
    UserAccount queryRelation(String source, String uuid);
}
