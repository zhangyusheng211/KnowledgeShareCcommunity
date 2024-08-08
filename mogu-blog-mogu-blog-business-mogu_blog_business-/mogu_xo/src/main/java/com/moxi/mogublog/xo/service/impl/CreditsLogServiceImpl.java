package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.CreditsLog;
import com.moxi.mogublog.commons.entity.LuckyActivity;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.CreditsLogMapper;
import com.moxi.mogublog.xo.service.CreditsLogService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import com.moxi.mougblog.base.vo.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 积分日志 服务实现类
 *
 * @author 陌溪
 * @date 2021年11月27日16:40:30
 */
@Service
public class CreditsLogServiceImpl extends SuperServiceImpl<CreditsLogMapper, CreditsLog> implements CreditsLogService {
    @Resource
    CreditsLogMapper creditsLogMapper;

    @Resource
    CreditsLogService creditsLogService;
    @Resource
    private UserService userService;
    @Resource
    RedisUtil redisUtil;

    @Override
    public IPage<CreditsLog> getPageList(CreditsLogVO creditsLogVO) {
        QueryWrapper<CreditsLog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(creditsLogVO.getKeyword()) && !StringUtils.isEmpty(creditsLogVO.getKeyword())) {
            String keyword = creditsLogVO.getKeyword().trim();
            queryWrapper.or().like(SQLConf.USER_NAME, keyword).or().eq(SQLConf.USER_UID, keyword).or().eq(SQLConf.ACTION_NAME, keyword);
        }
        Page<CreditsLog> page = new Page<>();
        page.setCurrent(creditsLogVO.getCurrentPage());
        page.setSize(creditsLogVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(creditsLogVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(creditsLogVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(creditsLogVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(creditsLogVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<CreditsLog> pageList = creditsLogService.page(page, queryWrapper);
        List<CreditsLog> creditsLogList = pageList.getRecords();
        List<String> userUidList = creditsLogList.stream().map(CreditsLog::getUserUid).collect(Collectors.toList());
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            userMap = userService.getUserListAndAvatarByIds(userUidList).stream().collect(Collectors.toMap(User::getUid, Function.identity()));
        }
        for (CreditsLog creditsLog: creditsLogList) {
            creditsLog.setUser(userMap.get(creditsLog.getUserUid()));
        }
        pageList.setRecords(creditsLogList);
        return pageList;
    }

    @Override
    public String add(CreditsLogVO creditsLogVO) {
        CreditsLog creditsLog = new CreditsLog();
        BeanUtils.copyProperties(creditsLogVO, creditsLog, SysConf.STATUS);
        creditsLog.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public Page<CreditsLogVO> queryList(PageInfo pageInfo) {
        Page page = pageInfo.creatPage();
        String requestUserUid = RequestHolder.getUserUid();
        return creditsLogMapper.queryList(page, requestUserUid);
    }

    @Override
    public void addCreditsLog(CreditsLogVO creditsLogVO) {
        creditsLogMapper.addCreditsLog(creditsLogVO);
    }

    @Override
    public Page<CreditsLogVO> queryCredits(Page page, String uid) {
        return creditsLogMapper.queryCredits(page, uid);
    }


    @Override
    public Integer queryCount(String userUid, Date date, String code) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return creditsLogMapper.queryCount(userUid, simpleDateFormat.format(date), code);
    }

    /**
     * 查询积分月榜
     *
     * @return
     */
    @Override
    public List<CreditsLog> getLeaderMonth(Boolean refresh) {

        String rankMonthListJson = redisUtil.get(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_MONTH_LIST);
        List<CreditsLog> leaderMonth;
        if (StringUtils.isNotEmpty(rankMonthListJson) && refresh) {
            leaderMonth = JsonUtils.jsonToList(rankMonthListJson, CreditsLog.class);
        } else {
            // 设置时间区间为上月初到月末
            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.MONTH, -1);
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            Date timeFirstDay = calendar1.getTime();
            //获取前一个月最后一天
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, 0);
            Date timeLastDay = calendar2.getTime();

            QueryWrapper<CreditsLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("user_uid, sum(change_credits) as sumCredits ");
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.ge(SQLConf.CREATE_TIME, timeFirstDay);
            queryWrapper.le(SQLConf.CREATE_TIME, timeLastDay);
            queryWrapper.groupBy(SQLConf.USER_UID);
            queryWrapper.orderByDesc(SQLConf.SUM_CREDITS);
            queryWrapper.last("limit 10");
            leaderMonth = creditsLogService.list(queryWrapper);
            List<String> userIds = new ArrayList<>();
            leaderMonth.forEach((item) -> {
                userIds.add(item.getUserUid());
            });
            // 获取用户列表
            Map<String, User> userMap = this.usersConvert(userIds);

            leaderMonth.forEach((item) -> {
                item.setUser(userMap.get(item.getUserUid()));
            });
            redisUtil.setEx(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_MONTH_LIST, JsonUtils.objectToJson(leaderMonth), 24, TimeUnit.HOURS);
        }
        return leaderMonth;
    }

    /**
     * 查询积分周榜
     *
     * @return
     */
    @Override
    public List<CreditsLog> getLeaderWeek(Boolean refresh) {

        String rankWeekListJson = redisUtil.get(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_WEEK_LIST);
        List<CreditsLog> leaderWeek;
        if (StringUtils.isNotEmpty(rankWeekListJson) && refresh) {
            leaderWeek = JsonUtils.jsonToList(rankWeekListJson, CreditsLog.class);
        } else {
            // 获取上周 周一到 周天 区间
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DATE, -7);
            instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            instance.set(Calendar.HOUR_OF_DAY, 0);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            //上周一日期
            Date lastMonday = instance.getTime();
            Calendar instance1 = Calendar.getInstance();
            instance1.setTime(new Date());
            instance1.add(Calendar.DATE, -1);
            instance1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            instance1.set(Calendar.HOUR_OF_DAY, 0);
            instance1.set(Calendar.MINUTE, 0);
            instance1.set(Calendar.SECOND, 0);
            //上周日日期
            Date lastSunday = instance1.getTime();

            QueryWrapper<CreditsLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("user_uid, sum(change_credits) as sumCredits ");
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.ge(SQLConf.CREATE_TIME, lastMonday);
            queryWrapper.le(SQLConf.CREATE_TIME, lastSunday);
            queryWrapper.groupBy(SQLConf.USER_UID);
            queryWrapper.orderByDesc(SQLConf.SUM_CREDITS);
            queryWrapper.last("limit 10");
            leaderWeek = creditsLogService.list(queryWrapper);
            List<String> userIds = new ArrayList<>();
            leaderWeek.forEach((item) -> {
                userIds.add(item.getUserUid());
            });

            // 获取用户列表
            Map<String, User> userMap = this.usersConvert(userIds);

            leaderWeek.forEach((item) -> {
                item.setUser(userMap.get(item.getUserUid()));
            });
            redisUtil.setEx(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_WEEK_LIST, JsonUtils.objectToJson(leaderWeek), 24, TimeUnit.HOURS);
        }
        return leaderWeek;
    }


    /**
     * List<userIds> 转换为 Map<userId , User>
     *
     * @param userIds
     * @return
     */
    private Map<String, User> usersConvert(List<String> userIds) {
        Map<String, User> userMap = new HashMap<>();
        if (userIds.size() > 0) {
            Collection<User> collection = userService.listByIds(userIds);
            // 设置头像
            userService.setUserAvatar(collection);
            // 过滤敏感信息
            List<User> userList = userService.convertUserList(collection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }
        return userMap;
    }


}
