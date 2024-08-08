package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mogublog.commons.schema.LoginRequest;
import me.zhyd.oauth.model.AuthUser;

import java.util.List;

/**
 * 用户第三方登录模块
 *
 * @author 遇见
 */
public interface AuthUserLoginManager {
    /**
     * 第三方用户登录接口
     * 根据用户授权数据获取绑定的用户
     * 无绑定用户则使用第三方用户注册默认用户
     *
     * @param source   第三方登录来源
     * @param authUser 第三方用户信息
     * @return
     */
    User login(String source, AuthUser authUser);

    /**
     * 第三方用户绑定接口，用于绑定平台已有的账号
     *
     * @param source
     * @param authUser
     * @param userUid
     * @return
     */
    User bind(String source, AuthUser authUser, String userUid);

    /**
     * 获取第三方用户绑定列表
     *
     * @return
     */
    List<UserAccount> getUserAccountList();

    /**
     * 通过openID获取用户信息
     *
     * @return
     */
    User getUserByOpenID(String openID);
}
