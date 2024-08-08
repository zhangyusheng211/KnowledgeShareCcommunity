package com.moxi.mogublog.xo.mapper;

import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户三方账号管理
 *
 * @author 遇见
 */
@Mapper
public interface UserAccountMapper extends SuperMapper<UserAccount> {
}
