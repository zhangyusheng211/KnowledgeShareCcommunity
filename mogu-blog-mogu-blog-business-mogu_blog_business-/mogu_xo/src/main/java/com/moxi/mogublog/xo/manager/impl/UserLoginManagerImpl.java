package com.moxi.mogublog.xo.manager.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.LoginRequest;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.MD5Utils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.utils.wechat.WechatUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.AuthUserLoginManager;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.service.SystemConfigService;
import com.moxi.mogublog.xo.service.UserAccountService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录模块
 *
 * @author 遇见
 */
@Slf4j
@Service
public class UserLoginManagerImpl implements AuthUserLoginManager {
    @Resource
    private WebUtil webUtil;

    @Resource
    private PictureFeignClient pictureFeignClient;

    @Resource
    private SystemConfigService systemConfigService;
    /**
     * 用户模块
     */
    @Resource
    UserService userService;
    /**
     * 第三方用户与系统用户绑定模块
     */
    @Resource
    UserAccountService userAccountService;

    @Value(value = "${DEFAULE_PWD}")
    private String DEFAULE_PWD;

    @Resource
    private SysParamsService sysParamsService;

    @Override
    public User login(String source, AuthUser authUser) {

        User user;
        Boolean exist = true;
        UserAccount userAccount = userAccountService.queryRelation(source, authUser.getUuid());

        /**
         *
         * 当未查找到关系表，说明用户是第一次登录，需要创建一个用户
         */
        if (userAccount == null) {
            user = new User();
            exist = false;
            // 判断邮箱是否存在
            if (authUser.getEmail() != null) {
                String email = authUser.getEmail();
                user.setEmail(email);
            }
            // 判断用户性别
            if (authUser.getGender() != null) {
                AuthUserGender gender = authUser.getGender();
                if (SysConf.MALE.equals(gender.name())) {
                    user.setGender(EGender.MALE);
                } else if (SysConf.FEMALE.equals(gender.name())) {
                    user.setGender(EGender.FEMALE);
                } else {
                    user.setGender(EGender.UNKNOWN);
                }
            }
            // 设置新创建的用户
            user.setIsNewUser(Constants.NUM_ONE);
            // 设置用户权限
            String defaultUserAuth = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_USER_AUTH);
            user.setAuthCodeList(defaultUserAuth);
        } else {
            user = userService.getById(userAccount.getUserUid());
        }

        // 通过头像uid获取图片
        String pictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
        List<String> photoList = webUtil.getPicture(pictureList);
        Map<String, Object> picMap = (Map<String, Object>) JsonUtils.jsonToObject(pictureList, Map.class);

        // 判断该用户是否含有头像信息
        if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE)) && photoList.size() > 0) {
//            List<Map<String, Object>> picData = (List<Map<String, Object>>) picMap.get(SysConf.DATA);
//            String fileOldName = picData.get(0).get(SysConf.FILE_OLD_NAME).toString();
//            // 判断本地的图片是否和第三方登录的一样，如果不一样，那么更新
//            // 如果旧名称为blob表示是用户自定义的，代表用户在本网站使用了自定义头像，那么就再也不同步更新第三方平台的头像了
//            // 如果是微信/QQ登录，直接显示原头像
//            if (fileOldName.equals(authUser.getAvatar()) || SysConf.BLOB.equals(fileOldName) || ELoginType.WECHAT.getName().equals(source) || ELoginType.QQ.getName().equals(source)) {
//                user.setPhotoUrl(photoList.get(0));
//            } else {
//                updateUserPhoto(authUser, user);
//            }

            // 有图片，就不更新了
            user.setPhotoUrl(photoList.get(0));
        } else {
            // 当获取头像失败时，需要从网站上进行获取
            updateUserPhoto(authUser, user);
        }

        // 如果还是没有头像，那么从默认头像库中获取
        if (StringUtils.isEmpty(user.getAvatar())) {
            String defaultAvatarStr = sysParamsService.getSysParamsValueByKey(SysConf.USER_DEFAULT_AVATAR);
            List<String> defaultAvatarList = StringUtils.changeStringToString(defaultAvatarStr, Constants.SYMBOL_COMMA);
            // 随机一个头像
            if (defaultAvatarList.size() > 1) {
                Integer index = RandomUtil.randomInt(defaultAvatarList.size() - 1);
                user.setAvatar(defaultAvatarList.get(index));
                // 获取图片信息
                String defaultPictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
                List<String> defaultPhotoList = webUtil.getPicture(defaultPictureList);
                if (defaultPhotoList.size() > 0) {
                    user.setPhotoUrl(defaultPhotoList.get(0));
                }
            }
        }

        // 判断是否需要更换用户昵称
        if (StringUtils.isNotEmpty(authUser.getNickname()) && !exist) {
            user.setNickName(authUser.getNickname());
        }

        if (StringUtils.isNotEmpty(authUser.getRemark()) && !exist) {
            user.setSummary(authUser.getRemark());
        }

        if (user.getLoginCount() == null) {
            user.setLoginCount(1);
        } else {
            user.setLoginCount(user.getLoginCount() + 1);
        }

        // 获取浏览器，IP来源，以及操作系统
        user = userService.serRequestInfo(user);
        // 暂时将token也存入到user表中，为了以后方便更新redis中的内容
        if (authUser.getToken() != null && authUser.getToken().getAccessToken() != null) {
            user.setValidCode(authUser.getToken().getAccessToken());
        }

        // 用户等级换算
        EUserLevel userLevel = EUserLevel.getLvByExpValue(user.getExpValue());
        user.setUserLevel(userLevel.getLevel());

        if (exist) {
            user.updateById();
        } else {
            user.setUuid(authUser.getUuid());
            user.setSource(authUser.getSource());
            user.setUserName(authUser.getUsername());
            // 如果昵称为空，那么直接设置用户名
            if (StringUtils.isEmpty(user.getNickName())) {
                user.setNickName(authUser.getUsername());
            }
            // 默认密码
            user.setPassWord(MD5Utils.string2MD5(DEFAULE_PWD));
            // 设置是否开启评论邮件通知
            if (StringUtils.isNotEmpty(user.getEmail())) {
                // 有邮箱默认开启
                user.setStartEmailNotification(EOpenStatus.OPEN_STATUS);
            } else {
                user.setStartEmailNotification(EOpenStatus.CLOSE_STATUS);
            }
            // 增加新用户
            user.insert();

            //创建新的账号关联关系
            UserAccount newRelation = new UserAccount();
            newRelation.setUserUid(user.getUid());
            newRelation.setUserName(authUser.getUsername());
            newRelation.setNickName(authUser.getNickname());
            newRelation.setSource(authUser.getSource());
            newRelation.setAccountId(authUser.getUuid());
            newRelation.setBindTime(new Date());
            newRelation.setGender(user.getGender());
            newRelation.setSummary(user.getSummary());
            newRelation.setAvatar(user.getAvatar());
            newRelation.setEmail(user.getEmail());
            newRelation.insert();
        }
        return user;
    }

    @Override
    public User bind(String source, AuthUser authUser, String userUid) {
        HttpServletResponse response = RequestHolder.getResponse();
        if (StringUtils.isEmpty(userUid)) {
            throw new InsertException("未查询到userUid，无法完成账号绑定");
        }
        User user = userService.getById(userUid);
        if (user == null) {
            throw new InsertException("未查询到登录用户，无法完成账号绑定");
        }

        // 通过头像uid获取图片
        String pictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
        List<String> photoList = webUtil.getPicture(pictureList);
        Map<String, Object> picMap = (Map<String, Object>) JsonUtils.jsonToObject(pictureList, Map.class);
        // 判断该用户是否含有头像信息
        if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE)) && photoList.size() > 0) {
            user.setPhotoUrl(photoList.get(0));
        }

        UserAccount userAccount = userAccountService.queryRelation(source, authUser.getUuid());
        /**
         *
         * 当未查找到关系表，如果存在记录，说明账号已被绑定
         */
        if (userAccount == null) {
            //创建新的账号关联关系
            UserAccount newRelation = new UserAccount();
            newRelation.setUserUid(user.getUid());
            newRelation.setUserName(authUser.getUsername());
            newRelation.setNickName(authUser.getNickname());
            newRelation.setSource(authUser.getSource());
            newRelation.setBindTime(new Date());
            newRelation.setAccountId(authUser.getUuid());
            // 判断邮箱是否存在
            if (authUser.getEmail() != null) {
                String email = authUser.getEmail();
                newRelation.setEmail(email);
            }
            // 判断用户性别
            if (authUser.getGender() != null) {
                AuthUserGender gender = authUser.getGender();
                if (SysConf.MALE.equals(gender.name())) {
                    newRelation.setGender(EGender.MALE);
                } else if (SysConf.FEMALE.equals(gender.name())) {
                    newRelation.setGender(EGender.FEMALE);
                } else {
                    newRelation.setGender(EGender.UNKNOWN);
                }
            }
            // 判断是否需要更换用户昵称
            if (StringUtils.isNotEmpty(authUser.getNickname())) {
                newRelation.setNickName(authUser.getNickname());
            }
            // 更新简介
            if (StringUtils.isNotEmpty(authUser.getRemark())) {
                newRelation.setSummary(authUser.getRemark());
            }
            // 更新用户头像
            updateUserPhoto(authUser, user);
            newRelation.setAvatar(user.getAvatar());
            newRelation.insert();
        } else {
            // 如果当前账户绑定的就是当前用户，那么提示已经绑定
            if (userUid.equals(userAccount.getUserUid())) {
                throw new InsertException(MessageConf.USER_ACCOUNT_REPETITION);
            }
            // 只有当前用户的经验值低于100的时候，说明是一个新号才允许被覆盖，否则报错提示
            User oldUser = userService.getById(userAccount.getUserUid());
            if (oldUser.getExpValue() > 100) {
                throw new InsertException(MessageConf.USER_ACCOUNT_EXIST);
            }
            userAccount.setUserUid(userUid);
            userAccount.updateById();
        }

        // 用户等级换算
        EUserLevel userLevel = EUserLevel.getLvByExpValue(user.getExpValue());
        user.setUserLevel(userLevel.getLevel());
        return user;
    }

    @Override
    public List<UserAccount> getUserAccountList() {
        String userUid = RequestHolder.getUserUid();
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<UserAccount> userAccountList = userAccountService.list(queryWrapper);
        // 对账号来源进行转化
        for (UserAccount userAccount : userAccountList) {
            ELoginType loginType = ELoginType.getLoginTypeByName(userAccount.getSource());
            if (loginType == null) {
                continue;
            }
            userAccount.setSourceDesc(loginType.getDesc());
        }
        return userAccountList;
    }

    @Override
    public User getUserByOpenID(String openID) {
        if (StringUtils.isEmpty(openID)) {
            throw new QueryException("参数异常，openID is null");
        }
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq(SQLConf.ACCOUNT_ID, openID);
        userAccountQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        userAccountQueryWrapper.last(SysConf.LIMIT_ONE);
        UserAccount userAccount = userAccountService.getOne(userAccountQueryWrapper);
        if (userAccount == null) {
            throw new QueryException("用户未注册");
        }
        return userService.getById(userAccount.getUserUid());
    }

    /**
     * 更新用户头像
     *
     * @param data
     * @param user
     */
    private void updateUserPhoto(AuthUser data, User user) {
        QueryWrapper<SystemConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        SystemConfig systemConfig = systemConfigService.getOne(queryWrapper);
        // 获取到头像，然后上传到自己服务器
        FileVO fileVO = new FileVO();
        fileVO.setAdminUid(SysConf.DEFAULT_UID);
        fileVO.setUserUid(SysConf.DEFAULT_UID);
        fileVO.setProjectName(SysConf.BLOG);
        fileVO.setSortName(SysConf.ADMIN);
        fileVO.setSystemConfig(JsonUtils.object2Map(systemConfig));
        List<String> urlList = new ArrayList<>();
        if (data.getAvatar() != null) {
            urlList.add(data.getAvatar());
        }
        fileVO.setUrlList(urlList);
        String res = this.pictureFeignClient.uploadPicsByUrl(fileVO);
        Map<String, Object> resultMap = JsonUtils.jsonToMap(res);
        if (resultMap.get(SysConf.CODE) != null && SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE).toString())) {
            if (resultMap.get(SysConf.DATA) != null) {
                List<Map<String, Object>> listMap = (List<Map<String, Object>>) resultMap.get(SysConf.DATA);
                if (listMap != null && listMap.size() > 0) {
                    Map<String, Object> pictureMap = listMap.get(0);
                    // 设置图片信息
                    user.setAvatar(pictureMap.get(SysConf.UID).toString());
                    user.setPhotoUrl(pictureMap.get(SysConf.PICTURE_URL).toString());
                }
            }
        }
    }

}
