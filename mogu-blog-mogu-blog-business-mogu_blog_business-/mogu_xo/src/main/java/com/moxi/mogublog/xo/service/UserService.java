package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatGPTSetting;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.service.SuperService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 管理员表 服务类
 *
 * @author 陌溪
 * @date 2018-09-04
 */
public interface UserService extends SuperService<User> {

    /**
     * 记录用户信息
     *
     * @param response
     */
    User insertUserInfo(HttpServletRequest request, String response);

    /**
     * 通过source uuid获取用户类
     *
     * @param source
     * @param uuid
     * @return
     */
    User getUserBySourceAnduuid(String source, String uuid);

    /**
     * 获取用户数
     *
     * @param status
     * @return
     */
    Integer getUserCount(int status);

    /**
     * 设置Request相关，如浏览器，IP，IP来源
     *
     * @param user
     * @return
     */
    User serRequestInfo(User user);

    /**
     * 通过ids获取用户列表
     *
     * @param ids
     * @return
     */
    List<User> getUserListByIds(List<String> ids);

    /**
     * 通过ids获取用户列表【携带用户头像和背景图片】
     *
     * @param ids
     * @return
     */
    List<User> getUserListAndAvatarByIds(Collection<String> ids);


    /**
     * 获取带头像的用户Map
     *
     * @param ids
     * @return
     */
    Map<String, User> getUserAvatarMapByIds(List<String> ids);


    /**
     * 设置用户头像
     *
     * @return
     */
    User setUserAvatar(User user);

    /**
     * 获取用户列表
     *
     * @param userVO
     * @return
     */
    IPage<User> getPageList(UserVO userVO);

    /**
     * 新增用户
     *
     * @param userVO
     */
    String addUser(UserVO userVO);

    /**
     * 编辑用户
     *
     * @param userVO
     */
    String editUser(UserVO userVO);

    /**
     * 判断用户是否被封禁： true：正常，false：封禁
     *
     * @param userUid
     */
    Boolean getUserActiveStatus(String userUid);


    /**
     * 删除用户
     *
     * @param userVO
     */
    String deleteUser(UserVO userVO);

    /**
     * 重置用户密码
     *
     * @param userVO
     * @return
     */
    String resetUserPassword(UserVO userVO);

    /**
     * 用户脱敏【去除敏感信息】
     */
    List<User> convertUserList(Collection<User> userList);

    /**
     * 用户脱敏【去除敏感信息】
     */
    User convertUser(User user);

    /**
     * 获取发表的文章数
     *
     * @param userUid
     * @return
     */
    Integer getBlogPublishCount(String userUid);

    /**
     * 获取发表的问题数
     *
     * @param userUid
     * @return
     */
    Integer getProblemPublishCount(String userUid);

    /**
     * 获取文章被访问数
     *
     * @param userUid
     * @return
     */
    Integer getBlogVisitCount(String userUid);

    /**
     * 增加用户积分
     *
     * @param action
     * @param credits
     * @param resourceUid
     * @param userUid
     * @return
     */
    void addUserCredits(String action, Integer credits, String resourceUid, String userUid);

    /**
     * 获取当前用户的积分
     *
     * @return
     */
    Integer getCurrentUserCredits(Boolean refresh);

    /**
     * 更新当前用户背景图片
     *
     * @param userVO
     * @return
     */
    String updateCurrentUserBackgroundImage(UserVO userVO);

    /**
     * 获取TopN的用户【积分倒排】
     *
     * @return
     */
    IPage<User> getUserTopN(UserVO userVO);

    /**
     * 设置用户头像
     *
     * @param list
     */
    void setUserAvatar(Collection<User> list);

    /**
     * 设置用户发布的信息【博客数、问答数、评论数】
     *
     * @param list
     */
    void setUserPublishInfo(Collection<User> list);

    /**
     * 用户初始化积分刷数脚本
     */
    String flushUserCredits();

    /**
     * 用户第三方账号刷数
     */
    String flushUserAccount();

    /**
     * 更新用户积分
     *
     * @param userUid
     * @param credits
     */
    void updateCreditsByUserUid(String userUid, Integer credits);

    /**
     * 更新用户经验值
     *
     * @param userUid
     * @param expValue
     */
    void updateExpValueByUserUid(String userUid, Integer expValue);

    /**
     * 通过uuid和source获取用户
     *
     * @return
     */
    User getUserByUuidAndSource(String uuid, String source);

    /**
     * 查询积分总榜
     *
     * @return
     */
    List<User> getLeaderAll(Boolean refresh);

    /**
     * 获取用户排名
     *
     * @param userUid
     * @return
     */
    Integer getUserRank(String userUid, boolean refresh);

    /**
     * 获取所有的机器人用户
     *
     * @return
     */
    Map<String, User> getRobotUserList();

    /**
     * 获取个人中心网址
     *
     * @return
     */
    String getUserCenterUrl(String userUid);

    /**
     * 校验是否可以回复
     *
     * @param userUid
     * @return
     */
    ChatGPTSetting checkRobotReply(String userUid, int userTag);

    /**
     * chatGPT自动回复
     *
     * @param openID
     * @param message
     * @return
     */
    String chatGPTReply(String openID, String message);

    /**
     * 获取用户UID
     *
     * @return
     */
    String getUserUidByRequest();

    /**
     * 获取用户钱包金额
     * @param userUid
     * @return
     */
    Long getUserAmount(String userUid);

    /**
     * 用户钱包金额消耗
     * @param userUid
     * @return
     */
    void updateAmountByUserUid(String userUid, Long amount);

    /**
     * 刷新用户缓存
     * @param userUid
     */
    void refreshUserCache(String userUid);
}
