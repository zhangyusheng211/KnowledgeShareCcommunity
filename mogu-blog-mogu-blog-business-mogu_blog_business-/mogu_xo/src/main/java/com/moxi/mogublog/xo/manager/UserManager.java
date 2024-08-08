package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.LoginRequest;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mougblog.base.enums.EResourceType;

import java.util.List;

/**
 * 用户模块
 *
 * @author 陌溪
 */
public interface UserManager {

    /**
     * 获取点赞用户列表
     * @param resourceUid
     * @param resourceType
     * @return
     */
    List<User> getPraiseUserList(String resourceUid, EResourceType resourceType);
}
