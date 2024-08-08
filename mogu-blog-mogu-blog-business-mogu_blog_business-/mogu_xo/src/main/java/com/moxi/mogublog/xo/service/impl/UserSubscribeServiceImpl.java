package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatGPTSetting;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserMapper;
import com.moxi.mogublog.xo.mapper.UserSubscribeMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ErrorCode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订阅表 服务实现类
 *
 * @author 陌溪
 * @since 2023年12月23日10:21:52
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserSubscribeServiceImpl extends SuperServiceImpl<UserSubscribeMapper, UserSubscribe> implements UserSubscribeService {

    @Resource
    private UserSubscribeService userSubscribeService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public String addUserSubscribe(UserSubscribeVO userSubscribeVO) {
        // 入参类型校验
        EResourceType.getType(userSubscribeVO.getResourceType());
        String userUid = RequestHolder.getUserUid();
        // 判断该资源是否被该用户收藏过
        boolean isUserSubscribe = this.checkUserSubscribe(userSubscribeVO);
        if (isUserSubscribe) {
            return ResultUtil.errorWithMessage("您已订阅该资源");
        }
        UserSubscribe userSubscribe = new UserSubscribe();
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(userSubscribeVO, userSubscribe, SysConf.STATUS);
        userSubscribe.setUserUid(userUid);
        boolean isSave = userSubscribe.insert();
        // 发送领域事件，新增订阅消息
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.SUBSCRIBE_ADD, userSubscribe);
        }
        return ResultUtil.successWithMessage("订阅成功");
    }

    @Override
    public String deleteUserSubscribe(UserSubscribeVO userSubscribeVO) {
        String userUid = RequestHolder.getUserUid();
        QueryWrapper<UserSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.RESOURCE_UID, userSubscribeVO.getResourceUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        UserSubscribe userSubscribe = userSubscribeService.getOne(queryWrapper);
        if (userSubscribe == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        } else {
            userSubscribe.setStatus(EStatus.DISABLED);
            boolean isSave = userSubscribe.updateById();
            // 发送取消订阅的领域消息
            if (isSave) {
                domainEventUtil.publishEvent(EventAction.SUBSCRIBE_DELETE, userSubscribe);
            }
        }
        return ResultUtil.successWithMessage("取消订阅成功");
    }

    @Override
    public IPage<UserSubscribe> getPageList(UserSubscribeVO userSubscribeVO) {
        QueryWrapper<UserSubscribe> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userSubscribeVO.getKeyword()) && !StringUtils.isEmpty(userSubscribeVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.SUBJECT_NAME, userSubscribeVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(userSubscribeVO.getUid()) && !StringUtils.isEmpty(userSubscribeVO.getUid().trim())) {
            queryWrapper.eq(SQLConf.UID, userSubscribeVO.getUid().trim());
        }
        if (StringUtils.isNotEmpty(userSubscribeVO.getUserUid()) && !StringUtils.isEmpty(userSubscribeVO.getUserUid().trim())) {
            queryWrapper.eq(SQLConf.USER_UID, userSubscribeVO.getUserUid().trim());
        }
        if (StringUtils.isNotEmpty(userSubscribeVO.getResourceUid()) && !StringUtils.isEmpty(userSubscribeVO.getResourceUid().trim())) {
            queryWrapper.eq(SQLConf.RESOURCE_UID, userSubscribeVO.getResourceUid().trim());
        }
        if (StringUtils.isNotEmpty(userSubscribeVO.getResourceType()) && !StringUtils.isEmpty(userSubscribeVO.getResourceType().trim())) {
            queryWrapper.eq(SQLConf.RESOURCE_TYPE, userSubscribeVO.getResourceType().trim());
        }
        // 排序
        if (StringUtils.isNotEmpty(userSubscribeVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userSubscribeVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(userSubscribeVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userSubscribeVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        Page<UserSubscribe> page = new Page<>();
        page.setCurrent(userSubscribeVO.getCurrentPage());
        page.setSize(userSubscribeVO.getPageSize());
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        IPage<UserSubscribe> pageList = userSubscribeService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public boolean checkUserSubscribe(UserSubscribeVO userSubscribeVO) {
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userSubscribeVO.getResourceType());
        String userUid = RequestHolder.getUserUid();
        // 判断该资源是否被该用户收藏过
        QueryWrapper<UserSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.RESOURCE_UID, userSubscribeVO.getResourceUid());
        queryWrapper.eq(SQLConf.RESOURCE_TYPE, resourceType.getType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = userSubscribeService.count(queryWrapper);
        return count>0;
    }
}
