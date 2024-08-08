package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.commons.vo.UserWatchVO;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserWatchMapper;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.service.UserWatchService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.EWatchStatus;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用户关注 服务实现类
 *
 * @author 陌溪
 * @date 2021年6月13日18:01:06
 */
@Service
public class UserWatchServiceImpl extends SuperServiceImpl<UserWatchMapper, UserWatch> implements UserWatchService {

    @Autowired
    private UserWatchService userWatchService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<UserWatch> getPageList(UserWatchVO userWatchVO) {
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        // 判断关注者和被关注者是否为空
        if (StringUtils.isEmpty(userWatchVO.getUserUid()) && StringUtils.isEmpty(userWatchVO.getToUserUid())) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        // 状态标志位，判断是否获取关注者 还是 被关注者【主要是通过userUid和toUserUid来进行判断】
        Boolean isWatch = false;
        // 获取TA关注了谁
        if (StringUtils.isNotEmpty(userWatchVO.getUserUid())) {
            isWatch = true;
            queryWrapper.eq(SQLConf.USER_UID, userWatchVO.getUserUid());
        }
        // 获取TA的粉丝
        if (StringUtils.isNotEmpty(userWatchVO.getToUserUid())) {
            isWatch = false;
            queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Page<UserWatch> page = new Page<>();
        page.setCurrent(userWatchVO.getCurrentPage());
        page.setSize(userWatchVO.getPageSize());

        IPage<UserWatch> pageList = userWatchService.page(page, queryWrapper);
        List<UserWatch> userWatchList = pageList.getRecords();
        List<String> userIdList = new ArrayList<>();
        List<String> adminIdList = new ArrayList<>();
        Boolean finalIsWatch = isWatch;
        userWatchList.forEach(item -> {
            if (finalIsWatch) {
                // 获取他关注了谁
                if (Constants.STR_ONE.equals(item.getIsAdmin())) {
                    adminIdList.add(item.getToUserUid());
                } else {
                    userIdList.add(item.getToUserUid());
                }
            } else {
                // 获取谁关注了他【管理员无法主动关注用户】
                userIdList.add(item.getUserUid());
            }
        });
        List<User> userList = new ArrayList<>();
        List<Admin> adminList = new ArrayList<>();
        if (userIdList.size() > 0) {
            userList = userService.getUserListAndAvatarByIds(userIdList);
        }
        if (adminIdList.size() > 0) {
            adminList = adminService.getAdminListByUid(adminIdList);
        }
        Map<String, User> userMap = new HashMap<>();
        Map<String, Admin> adminMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        adminList.forEach(item -> {
            adminMap.put(item.getUid(), item);
        });
        // 关注列表的用户uid
        List<String> watchUserUidList = new ArrayList<>();
        userWatchList.forEach(item -> {
            // 判断是关注者还是被关注者
            if (finalIsWatch) {
                // 获取用户的粉丝
                if (Constants.STR_ONE.equals(item.getIsAdmin())) {
                    item.setAdmin(adminMap.get(item.getToUserUid()));
                } else {
                    item.setUser(userMap.get(item.getToUserUid()));
                }
                // 设置用户id
                watchUserUidList.add(item.getToUserUid());
            } else {
                // 获取用户的粉丝
                item.setUser(userMap.get(item.getUserUid()));
                // 设置用户id
                watchUserUidList.add(item.getUserUid());
            }
        });

        // 获取当前登录用户的信息
        String userUid = RequestHolder.getUserUid();
        // 我关注的用户
        Map<String, String> focusMap = new HashMap<>();
        // 关注我的用户
        Map<String, String> followMap = new HashMap<>();
        if (StringUtils.isNotEmpty(userUid) && watchUserUidList.size() > 0) {
            // 获取我关注的用户
            QueryWrapper<UserWatch> focusQueryWrapper = new QueryWrapper<>();
            focusQueryWrapper.eq(SQLConf.USER_UID, userUid);
            focusQueryWrapper.in(SQLConf.TO_USER_UID, watchUserUidList);
            focusQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            List<UserWatch> userWatches = userWatchService.list(focusQueryWrapper);


            // 从我关注的用户中，获取关注我的用户
            if (userWatches.size() > 0) {
                List<String> followUserUidList = new ArrayList<>();
                userWatches.forEach(item -> {
                    followUserUidList.add(item.getToUserUid());
                    focusMap.put(item.getToUserUid(), item.getToUserUid());
                });
                QueryWrapper<UserWatch> followQueryWrapper = new QueryWrapper<>();
                followQueryWrapper.in(SQLConf.USER_UID, followUserUidList);
                followQueryWrapper.eq(SQLConf.TO_USER_UID, userUid);
                List<UserWatch> followUserWatches = userWatchService.list(followQueryWrapper);
                followUserWatches.forEach(item -> {
                    followMap.put(item.getUserUid(), item.getUserUid());
                });
            }
        }


        for (UserWatch item : userWatchList) {
            String bizUserUid = "";
            if (finalIsWatch) {
                // 用户的粉丝
                bizUserUid = item.getToUserUid();
            } else {
                // 关注的用户
                bizUserUid = item.getUserUid();
            }
            // 初始化为未关注
            item.setWatchStatus(EWatchStatus.NO_WATCH);
            if (StringUtils.isNotEmpty(userUid)) {
                // 关注了用户
                if (focusMap.get(bizUserUid) != null) {
                    // 已关注
                    item.setWatchStatus(EWatchStatus.WATCH);
                    if (followMap.get(bizUserUid) != null) {
                        // 互相关注
                        item.setWatchStatus(EWatchStatus.MUTUAL_WATCH);
                    }
                }
            }
        }
        return pageList.setRecords(userWatchList);
    }

    @Override
    public String addUserWatch(UserWatchVO userWatchVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if (userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        if (userUid.equals(userWatchVO.getToUserUid())) {
            return ResultUtil.errorWithMessage("您无法关注自己");
        }
        if (userWatchVO.getIsAdmin() == Constants.STR_ONE) {
            throw new InsertException("无法关注管理员");
        }

        // 判断该用户是否被关注过
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid.toString());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = userWatchService.count(queryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage("您已关注过该用户");
        }

        UserWatch userWatch = new UserWatch();
        userWatch.setUserUid(userUid.toString());
        userWatch.setToUserUid(userWatchVO.getToUserUid());
        userWatch.setIsAdmin(userWatchVO.getIsAdmin());
        boolean isSave = userWatch.insert();

        if (isSave) {
            domainEventUtil.publishEvent(EventAction.WATCH_ADD, userWatch);
        }
        return ResultUtil.successWithMessage("关注成功");
    }

    @Override
    public String deleteUserWatch(UserWatchVO userWatchVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if (userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid.toString());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        UserWatch userWatch = userWatchService.getOne(queryWrapper);
        if (userWatch == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        } else {
            userWatch.setStatus(EStatus.DISABLED);
            boolean isSave = userWatch.updateById();
            if (isSave) {
                domainEventUtil.publishEvent(EventAction.WATCH_CANCEL, userWatch);
            }
            return ResultUtil.successWithMessage("取消关注成功");
        }
    }

    @Override
    public Boolean checkUserWatch(UserWatchVO userWatchVO) {
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userWatchVO.getUserUid());
        queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = userWatchService.count(queryWrapper);
        return count > 0;
    }

    @Override
    public Integer getUserWatchCount(UserWatchVO userWatchVO) {
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userWatchVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, userWatchVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(userWatchVO.getToUserUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, userWatchVO.getToUserUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer userWatchCount = userWatchService.count(queryWrapper);
        return userWatchCount;
    }

    @Override
    public List<UserWatch> convertUserWatchList(Collection<UserWatch> userWatchList) {
        List<String> userIdList = new ArrayList<>();
        List<UserWatch> resultList = new ArrayList<>();
        userWatchList.forEach(item -> {
            userIdList.add(item.getUserUid());
        });
        List<User> userList = new ArrayList<>();
        if (userIdList.size() > 0) {
            userList = userService.getUserListAndAvatarByIds(userIdList);
        }

//        userList = userService.convertUserList(userList);

        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        userWatchList.forEach(item -> {
            item.setUser(userMap.get(item.getUserUid()));
            resultList.add(item);
        });
        return resultList;
    }

    @Override
    public Integer checkCurrentUserWatch(UserWatchVO userWatchVO) {
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            return EWatchStatus.NO_WATCH;
        }
        List<String> userUidList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userWatchVO.getToUserUid())) {
            userUidList.add(userUid);
            userUidList.add(userWatchVO.getToUserUid());
        } else {
            return EWatchStatus.NO_WATCH;
        }
        List<UserWatch> list = userWatchService.list(
                new LambdaQueryWrapper<UserWatch>()
                        .in(UserWatch::getUserUid, userUidList)
                        .in(UserWatch::getToUserUid, userUidList)
                        .eq(UserWatch::getStatus, EStatus.ENABLE)
        );

        if (list.size() >= 2) {
            // 说明是互相关注
            return EWatchStatus.MUTUAL_WATCH;
        } else if (list.size() == 1) {
            // 有一个关注的，判断是自己关注了，还是对方关注了
            UserWatch tempUserWatch = list.get(0);
            // 如果是自己关注
            if (userUid.equals(tempUserWatch.getUserUid())) {
                return EWatchStatus.WATCH;
            }
        }
        return EWatchStatus.NO_WATCH;
    }
}