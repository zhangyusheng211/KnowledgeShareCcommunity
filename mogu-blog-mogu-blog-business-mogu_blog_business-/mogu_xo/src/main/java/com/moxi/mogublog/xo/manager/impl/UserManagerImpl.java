package com.moxi.mogublog.xo.manager.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserPraiseRecord;
import com.moxi.mogublog.commons.schema.LoginRequest;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.utils.wechat.WechatUtils;
import com.moxi.mogublog.xo.manager.AuthUserLoginManager;
import com.moxi.mogublog.xo.manager.UserManager;
import com.moxi.mogublog.xo.mapper.UserPraiseRecordMapper;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EPraiseType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.model.AuthUser;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户模块
 *
 * @author 遇见
 */
@Service
public class UserManagerImpl implements UserManager {

    @Resource
    UserPraiseRecordMapper userPraiseRecordMapper;
    @Resource
    private UserService userService;

    @Override
    public List<User> getPraiseUserList(String resourceUid, EResourceType resourceType) {
        // 查找出所有点赞的用户
        List<UserPraiseRecord> userPraiseRecordList = userPraiseRecordMapper.selectList(new QueryWrapper<UserPraiseRecord>().select("DISTINCT `user_uid`").lambda()
                .eq(UserPraiseRecord::getResourceUid, resourceUid)
                .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                .eq(UserPraiseRecord::getPraiseType, EPraiseType.PRAISE.getType())
                .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
        );

        // 找到用户列表
        List<String> userUidList = userPraiseRecordList.stream().map(UserPraiseRecord::getUserUid).collect(Collectors.toList());
        // 判断一下当前登录的用户，是否在点赞列表，如果在则置顶
        String userUid = RequestHolder.getUserUid();
        List<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            List<User> tempUserList = userService.getUserListAndAvatarByIds(userUidList);
            for (User user : tempUserList) {
                if (user.getUid().equals(userUid)) {
                    userList.add(0, user);
                } else {
                    userList.add(user);
                }
            }
        }
        return userList;
    }
}
