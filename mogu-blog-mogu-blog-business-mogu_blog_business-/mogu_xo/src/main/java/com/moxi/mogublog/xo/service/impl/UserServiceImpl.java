package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PayFeignClient;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatGPTSetting;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.BaseSQLConf;
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
 * 用户表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-04
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    CommentService commentService;
    @Resource
    SignInRecordService signInRecordService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SysParamsService sysParamsService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private BlogService blogService;
    @Resource
    private QuestionService questionService;
    @Resource
    private WebVisitService webVisitService;
    @Resource
    private CreditsLogService creditsLogService;
    @Resource
    private FileFeignUtil fileFeignUtil;
    @Resource
    private UserMomentService userMomentService;
    @Resource
    private ProblemService problemService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private UserEquityRecordService userEquityRecordService;
    @Resource
    private AdminService adminService;
    @Value(value = "${data.webSite.url}")
    private String webSiteUrl;
    @Resource
    private DomainEventUtil domainEventUtil;
    /**
     * token过期的时间
     */
    @Value(value = "${audience.expiresSecond}")
    private Long expiresSecond;
    @Resource
    private SmsFeignClient smsFeignClient;


    @Override
    public User insertUserInfo(HttpServletRequest request, String response) {
        Map<String, Object> map = JsonUtils.jsonToMap(response);
        boolean exist = false;
        User user = new User();
        Map<String, Object> data = JsonUtils.jsonToMap(JsonUtils.objectToJson(map.get(SysConf.DATA)));
        if (data.get(SysConf.UUID) != null && data.get(SysConf.SOURCE) != null) {
            if (getUserBySourceAnduuid(data.get(SysConf.SOURCE).toString(), data.get(SysConf.UUID).toString()) != null) {
                user = getUserBySourceAnduuid(data.get(SysConf.SOURCE).toString(), data.get(SysConf.UUID).toString());
                exist = true;
            }
        } else {
            log.error("未获取到uuid或source");
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, MessageConf.INSERT_DEFAULT_ERROR);
        }

        if (data.get(SysConf.EMAIL) != null) {
            user.setEmail(data.get(SysConf.EMAIL).toString());
        }
        if (data.get(SysConf.AVATAR) != null) {
            user.setAvatar(data.get(SysConf.AVATAR).toString());
        }
        if (data.get(SysConf.NICKNAME) != null) {
            user.setNickName(data.get(SysConf.NICKNAME).toString());
        }
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(IpUtils.getIpAddr(request));
        if (exist) {
            user.updateById();
        } else {
            user.setUuid(data.get(SysConf.UUID).toString());
            user.setSource(data.get(SysConf.SOURCE).toString());
            user.setUserName("mg".concat(user.getSource()).concat(user.getUuid()));
            //产生(0,999999]之间的随机数
            Integer randNum = (int) (Math.random() * (999999) + 1);
            //进行六位数补全
            String workPassWord = String.format("%06d", randNum);
            user.setPassWord(workPassWord);
            user.insert();
        }
        return user;
    }

    @Override
    public User getUserBySourceAnduuid(String source, String uuid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.UUID, uuid).eq(BaseSQLConf.SOURCE, source);
        return userService.getOne(queryWrapper);
    }

    @Override
    public Integer getUserCount(int status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return userService.count(queryWrapper);
    }

    @Override
    public User serRequestInfo(User user) {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get("OS");
        String browser = map.get("BROWSER");
        String ip = IpUtils.getIpAddr(request);
        user.setLastLoginIp(ip);
        user.setOs(os);
        user.setBrowser(browser);
        user.setLastLoginTime(new Date());
        //从Redis中获取IP来源
        String jsonResult = stringRedisTemplate.opsForValue().get(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(ip, "utf-8");
            if (StringUtils.isNotEmpty(addresses)) {
                user.setIpSource(addresses);
                stringRedisTemplate.opsForValue().set(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            user.setIpSource(jsonResult);
        }
        return user;
    }

    @Override
    public List<User> getUserListByIds(List<String> ids) {
        List<User> userList = new ArrayList<>();
        if (ids == null || ids.size() == 0) {
            return userList;
        }
        Collection<User> userCollection = userService.listByIds(ids);
        userCollection.forEach(item -> {
            userList.add(item);
        });
        return userList;
    }

    @Override
    public List<User> getUserListAndAvatarByIds(Collection<String> ids) {
        List<User> userList = new ArrayList<>();
        if (ids == null || ids.size() == 0) {
            return userList;
        }
        Collection<User> userCollection = userService.listByIds(ids);
        // 过滤用户敏感信息
        userCollection.forEach(item -> {
            userList.add(this.convertUser(item));
        });
        // 设置用户头像
        this.setUserAvatar(userList);

        return userList;
    }

    @Override
    public Map<String, User> getUserAvatarMapByIds(List<String> ids) {
        Map<String, User> userMap = new HashMap<>();
        if (ids.size() == 0) {
            return userMap;
        }
        List<User> userList = this.getUserListByIds(ids);
        this.setUserAvatar(userList);
        userList = this.convertUserList(userList);

        for (User user : userList) {
            userMap.put(user.getUid(), user);
        }
        return userMap;
    }

    @Override
    public User setUserAvatar(User user) {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        this.setUserAvatar(userList);
        return userList.get(0);
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查询用户名
        if (StringUtils.isNotEmpty(userVO.getKeyword()) && !StringUtils.isEmpty(userVO.getKeyword().trim())) {
            String keyword = userVO.getKeyword().trim();
            queryWrapper.like(SQLConf.USER_NAME, keyword).or().like(SQLConf.NICK_NAME, keyword).or().like(SQLConf.UID, keyword);
        }
        if (StringUtils.isNotEmpty(userVO.getSource()) && !StringUtils.isEmpty(userVO.getSource().trim())) {
            queryWrapper.eq(SQLConf.SOURCE, userVO.getSource().trim());
        }
        if (userVO.getCommentStatus() != null) {
            queryWrapper.eq(SQLConf.COMMENT_STATUS, userVO.getCommentStatus());
        }
        if (userVO.getUserTag() != null) {
            queryWrapper.eq(SQLConf.USER_TAG, userVO.getUserTag());
        }
        if (userVO.getUserUidList() != null && userVO.getUserUidList().size() > 0) {
            queryWrapper.in(SQLConf.UID, userVO.getUserUidList());
        }
        if (userVO.getNeedEmail() != null && userVO.getNeedEmail()) {
            queryWrapper.isNotNull(SQLConf.EMAIL);
        }

        if (StringUtils.isNotEmpty(userVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(userVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        queryWrapper.select(User.class, i -> !i.getProperty().equals(SQLConf.PASS_WORD));
        Page<User> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        queryWrapper.ne(SQLConf.STATUS, EStatus.DISABLED);
        IPage<User> pageList = userService.page(page, queryWrapper);
        List<User> list = pageList.getRecords();
        // 设置用户头像
        if (userVO.getNeedAvatar() == null || userVO.getNeedAvatar()) {
            this.setUserAvatar(list);
        }
        // 设置当前等级上限经验值
        list.forEach(item -> {
            item.setLevelMaxCredits(EUserLevel.getLvByExpValue(item.getExpValue()).getMaxCredit());
        });
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addUser(UserVO userVO) {
        User user = new User();
        // 字段拷贝【将userVO中的内容拷贝至user】
        BeanUtils.copyProperties(userVO, user, SysConf.STATUS);
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassWord(encoder.encode(defaultPassword));
        user.setSource("MOGU");
        user.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editUser(UserVO userVO) {
        User user = userService.getById(userVO.getUid());
        if (user == null) {
            return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
        }
        user.setUserName(userVO.getUserName());
        user.setEmail(userVO.getEmail());
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        user.setOccupation(userVO.getOccupation());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setSummary(userVO.getSummary());
        user.setBirthday(userVO.getBirthday());
        user.setAvatar(userVO.getAvatar());
        user.setNickName(userVO.getNickName());
        user.setUserTag(userVO.getUserTag());
        user.setCommentStatus(userVO.getCommentStatus());
        user.setUpdateTime(new Date());
        user.setStatus(userVO.getStatus());
        user.setAuthCodeList(userVO.getAuthCodeList());
        user.updateById();

        // 编辑用户时，删除用户的评论状态
        redisUtil.delete(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + user.getUid());

        refreshUserCache(user.getUid());
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public Boolean getUserActiveStatus(String userUid) {

        // 判断用户是否被封号
        String userActiveStatus = redisUtil.get(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + userUid);
        // 用户已被封号
        if (Constants.STR_ZERO.equals(userActiveStatus)) {
            return false;
        } else {
            // redis中没记录
            if (userActiveStatus == null) {
                // 可能更新过状态，需要查库进行确认
                User user = userService.getById(userUid);
                // 用户不存在
                if (user == null) {
                    return false;
                }
                // 再次判断用户是否被封号
                if (Constants.NUM_ONE == user.getCommentStatus()) {
                    // 未封号
                    redisUtil.setEx(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + user.getUid(), Constants.STR_ONE, 7, TimeUnit.DAYS);
                    return true;
                } else {
                    // 已封号
                    redisUtil.setEx(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + user.getUid(), Constants.STR_ZERO, 7, TimeUnit.DAYS);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String deleteUser(UserVO userVO) {
        User user = userService.getById(userVO.getUid());
        user.setStatus(EStatus.DISABLED);
        user.setUpdateTime(new Date());
        user.updateById();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        User user = userService.getById(userVO.getUid());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassWord(encoder.encode(defaultPassword));
        user.setUpdateTime(new Date());
        user.updateById();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public List<User> convertUserList(Collection<User> userList) {
        List<User> resultUserList = new ArrayList<>();
        userList.forEach(item -> {
            resultUserList.add(convertUser(item));
        });
        return resultUserList;
    }

    @Override
    public User convertUser(User item) {
        User user = new User();
        user.setUid(item.getUid());
        user.setAvatar(item.getAvatar());
        user.setPhotoUrl(item.getPhotoUrl());
        user.setNickName(item.getNickName());
        user.setOccupation(item.getOccupation());
        user.setSummary(item.getSummary());
        user.setCreateTime(item.getCreateTime());
        user.setLoadingValid(item.getLoadingValid());
        user.setStatus(item.getStatus());
        user.setGender(item.getGender());
        user.setUserTag(item.getUserTag());
        user.setCredits(item.getCredits());
        user.setExpValue(item.getExpValue());
        user.setUserLevel(EUserLevel.getLvByExpValue(item.getExpValue()).getLevel());
        user.setBackgroundFileUrl(item.getBackgroundFileUrl());
        user.setBackgroundFileUid(item.getBackgroundFileUid());
        // 设置当前等级上限积分
        user.setLevelMaxCredits(EUserLevel.getLvByExpValue(user.getExpValue()).getMaxCredit());

        // 过滤用户
        String ip = item.getLastLoginIp();
        String ipPossession = redisUtil.get(RedisConf.USER_IP_POSSESSION + Constants.SYMBOL_COLON + user.getUid());
        if (StringUtils.isEmpty(ipPossession) && StringUtils.isNotEmpty(ip)) {
            // ip属地
            ipPossession = IpUtils.getIpPossession(ip);
            // 将信息设置到redis中
            redisUtil.setEx(RedisConf.USER_IP_POSSESSION + Constants.SYMBOL_COLON + user.getUid(), ipPossession, 1, TimeUnit.HOURS);
        }
        user.setUserIpPossession(ipPossession);
        return user;
    }

    @Override
    public Integer getBlogPublishCount(String userUid) {
        if (StringUtils.isEmpty(userUid)) {
            return 0;
        }
        String blogVisitCountJson = redisUtil.get(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(blogVisitCountJson)) {
            return Integer.valueOf(blogVisitCountJson);
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.ARTICLE_SOURCE, EContributeSource.USER_PUBLISH);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        Integer blogPublishCount = blogService.count(queryWrapper);
        redisUtil.setEx(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(blogPublishCount), 10, TimeUnit.MINUTES);
        return blogPublishCount;
    }

    @Override
    public Integer getProblemPublishCount(String userUid) {
        if (StringUtils.isEmpty(userUid)) {
            return 0;
        }
        String problemVisitCountJson = redisUtil.get(RedisConf.PROBLEM_PUBLISH_COUNT + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(problemVisitCountJson)) {
            return Integer.valueOf(problemVisitCountJson);
        }
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        Integer problemPublishCount = problemService.count(queryWrapper);
        redisUtil.setEx(RedisConf.PROBLEM_PUBLISH_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(problemPublishCount), 10, TimeUnit.MINUTES);
        return problemPublishCount;
    }

    @Override
    public Integer getBlogVisitCount(String userUid) {
        Integer blogVisitCount = 0;
        if (StringUtils.isEmpty(userUid)) {
            return blogVisitCount;
        }

        String blogVisitCountJson = redisUtil.get(RedisConf.BLOG_VISIT_COUNT + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(blogVisitCountJson)) {
            return Integer.valueOf(blogVisitCountJson);
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.ARTICLE_SOURCE, EContributeSource.USER_PUBLISH);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 只获取uid信息
        queryWrapper.select(SQLConf.OID, SQLConf.UID);
        List<Blog> blogList = blogService.list(queryWrapper);
        List<String> visitUidList = new ArrayList<>();
        blogList.forEach(item -> {
            visitUidList.add(item.getUid());
        });

        if (visitUidList.size() > 0) {
            QueryWrapper<WebVisit> webVisitQueryWrapper = new QueryWrapper<>();
            // 兼容历史数据
            webVisitQueryWrapper.in(SQLConf.MODULE_UID, visitUidList);
            blogVisitCount = webVisitService.count(webVisitQueryWrapper);
            redisUtil.setEx(RedisConf.BLOG_VISIT_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(blogVisitCount), 10, TimeUnit.MINUTES);
        }
        return blogVisitCount;
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public void addUserCredits(String action, Integer credits, String resourceUid, String ownUserUid) {

        // 获取用户token
        String userUid = ownUserUid == null ? RequestHolder.getUserUid() : ownUserUid;
        if (StringUtils.isEmpty(userUid)) {
            log.error("[addUserCredits] 用户未登录，无法新增积分");
            return;
        }
        ECreditType creditType = ECreditType.getByValue(action);
        /**
         * 重新传入的积分值
         */
        credits = (credits == null ? creditType.getCredit() : credits);


        if (creditType == null) {
            log.error("[addUserCredits] 该动作无法新增积分; creditType:{}" + creditType.getAction() + ", userUid: " + userUid);
        }

        // 判断该动作是否达到本日积分上限
        String creditLimitJson = redisUtil.get(RedisConf.USER_CREDITS_ADD_LIMIT + Constants.SYMBOL_COLON + userUid + Constants.SYMBOL_COLON + creditType.getCode());
        Integer creditLimit = 0;
        if (StringUtils.isNotEmpty(creditLimitJson)) {
            creditLimit = Integer.valueOf(creditLimitJson);
            if (creditLimit >= creditType.getLimit()) {
                log.error("[addUserCredits] 该动作已达到今日获取积分上限; creditType:" + creditType.getAction() + ", userUid: " + userUid);
                return;
            }
        }

        // 判断该资源是否已获取过积分【例如重复审批】
        //【任务奖励和抽奖奖不会进行该判断】
        List<String> skipCreditTypeList = Lists.newArrayList(ECreditType.CREDITS_LUCKY.getAction(), ECreditType.LUCKY_AWARD.getAction(), ECreditType.SYSTEM.getAction(), ECreditType.TASK_AWARD.getAction());
        if (StringUtils.isNotEmpty(resourceUid) && credits >= 0 && !skipCreditTypeList.contains(action)) {
            int count = creditsLogService.count(new LambdaQueryWrapper<CreditsLog>()
                    .eq(CreditsLog::getResourceUid, resourceUid)
                    .eq(CreditsLog::getActionCode, creditType.getCode())
                    .eq(CreditsLog::getStatus, EStatus.ENABLE)
            );
            if (count > 0) {
                log.error("[addUserCredits] 该资源已经获取过积分，无法重复获取; resourceUid: {}", resourceUid);
                return;
            }
        }

        User user = userService.getById(userUid);
        if (user != null) {
            // 如果是积分扣除，需要计算扣除的积分是否大于总的积分
            if (credits < 0 && user.getCredits() + credits < 0) {
                log.error("[addUserCredits] 积分余额不足，无法完成支付, userUid: {}", user.getUid());
                throw new InsertException("积分余额不足，无法完成操作");
            }

            // 清空redis中的积分记录
            redisUtil.delete(RedisConf.USER_CREDITS + Constants.SYMBOL_COLON + userUid);
            // 增加积分限制
            redisUtil.setEx(RedisConf.USER_CREDITS_ADD_LIMIT + Constants.SYMBOL_COLON + userUid + Constants.SYMBOL_COLON + creditType.getCode(), JsonUtils.objectToJson(creditLimit + 1), 1, TimeUnit.DAYS);

            /**
             * 先加积分记录   这时候old积分是原始积分
             */
            CreditsLogVO creditsLogVO = new CreditsLogVO();
            creditsLogVO.setUserUid(userUid);
            creditsLogVO.setChangeCredits(credits);
            creditsLogVO.setUserName(user.getUserName());
            creditsLogVO.setActionName(creditType.getAction());
            creditsLogVO.setActionCode(creditType.getCode());
            creditsLogVO.setResourceUid(resourceUid);
            creditsLogVO.setCreateTime(new Date());
            creditsLogService.addCreditsLog(creditsLogVO);
            /**
             * 再加积分
             */
            userService.updateCreditsByUserUid(userUid, credits);
            // 经验值只是增加，不会减少
            // 抽奖相关的活动，不记录经验值
            if (credits > 0 && !skipCreditTypeList.contains(action)) {
                userService.updateExpValueByUserUid(userUid, credits);
            }
            // 发送积分变更事件
            CreditsLog creditsLog = new CreditsLog();
            BeanUtils.copyProperties(creditsLogVO, creditsLog, SQLConf.STATUS);
            domainEventUtil.publishEvent(EventAction.CREDITS_CHANGE, creditsLog);
        } else {
            throw new QueryException("用户信息不存在");
        }
    }

    @Override
    public Integer getCurrentUserCredits(Boolean refresh) {
        Integer credits = 0;
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            return credits;
        }
        String creditsJson = redisUtil.get(RedisConf.USER_CREDITS + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(creditsJson) && !refresh) {
            return Integer.valueOf(creditsJson);
        }
        User user = userService.getById(userUid);
        credits = user.getCredits();
        // 设置积分到redis中
        redisUtil.setEx(RedisConf.USER_CREDITS + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(credits), 10, TimeUnit.MINUTES);
        return credits;
    }

    @Override
    public String updateCurrentUserBackgroundImage(UserVO userVO) {
        if (StringUtils.isNotEmpty(userVO.getBackgroundFileUid())) {
            String userUid = RequestHolder.getUserUid();
            User user = userService.getById(userUid);
            if (user != null) {
                user.setBackgroundFileUid(userVO.getBackgroundFileUid());
                user.updateById();
                return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
            }
        }
        return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
    }

    @Override
    public IPage<User> getUserTopN(UserVO userVO) {
        // 首页展示的topN
        String userListJson = redisUtil.get(RedisConf.INDEX_TOP_N_USER);
        String userListTotalJson = redisUtil.get(RedisConf.INDEX_TOP_N_USER_TOTAL);
        String topN = sysParamsService.getSysParamsValueByKey(SysConf.USER_TOP_N);
        String topNJson = redisUtil.get(RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + SysConf.USER_TOP_N);
        if (userVO.getCurrentPage() == Constants.NUM_ONE && StringUtils.isNotEmpty(userListJson) && StringUtils.isNotEmpty(userListTotalJson) && StringUtils.isNotEmpty(topNJson) && !userVO.getRefresh()) {
            List<User> userList = (List<User>) JsonUtils.jsonArrayToArrayList(userListJson);
            Integer total = Integer.valueOf(userListTotalJson);
            IPage<User> page = new Page();
            page.setRecords(userList);
            page.setTotal(total);
            page.setSize(Constants.NUM_TEN);
            page.setCurrent(Constants.NUM_ONE);
            return page;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> userPage = new Page<>();
        userPage.setCurrent(userVO.getCurrentPage());
        userPage.setSize(Constants.NUM_TEN);
        queryWrapper.orderByDesc(SQLConf.EXP_VALUE);
        IPage<User> userPageList = userService.page(userPage, queryWrapper);

        // 判断页的大小是否超过限定
        int topNum = Integer.valueOf(topN);
        if (userPageList.getTotal() > topNum) {
            userPageList.setTotal(topNum);
        }

        List<User> userList = userPageList.getRecords();
        if (userList.size() > 0) {
            // 获取用户头像
            userService.setUserAvatar(userList);
            // 过滤用户敏感信息
            userList = userService.convertUserList(userList);
            // 设置用户信息基本信息
            userService.setUserPublishInfo(userList);
        }
        userPageList.setRecords(userList);
        // 只缓存第一页的内容
        if (userList.size() > 0 && userPageList.getCurrent() == 1) {
            redisUtil.setEx(RedisConf.INDEX_TOP_N_USER, JsonUtils.objectToJson(userList), 10, TimeUnit.MINUTES);
            redisUtil.setEx(RedisConf.INDEX_TOP_N_USER_TOTAL, JsonUtils.objectToJson(userPageList.getTotal()), 10, TimeUnit.MINUTES);
        }
        return userPageList;
    }

    @Override
    public void setUserAvatar(Collection<User> list) {
        List<String> fileUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUidList.add(item.getAvatar());
            }
            if (StringUtils.isNotEmpty(item.getBackgroundFileUid())) {
                fileUidList.add(item.getBackgroundFileUid());
            }
        });
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        for (User item : list) {
            //获取头像
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getAvatar(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    if (pictureMap.get(picture) != null && pictureMap.get(picture) != "") {
                        pictureListTemp.add(pictureMap.get(picture));
                    }
                });
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                }
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getBackgroundFileUid())) {
                item.setBackgroundFileUrl(pictureMap.get(item.getBackgroundFileUid()));
            }
        }
    }

    @Override
    public void setUserPublishInfo(Collection<User> list) {
        for (User user : list) {
            // 获取博客数
            BlogVO blogVO = new BlogVO();
            blogVO.setUserUid(user.getUid());
            Integer blogPublishCount = blogService.getBlogCount(blogVO);
            user.setBlogPublishCount(blogPublishCount);

            // 获取问答数
            QuestionVO questionVO = new QuestionVO();
            questionVO.setUserUid(user.getUid());
            Integer questionPublishCount = questionService.getQuestionCount(questionVO);
            user.setQuestionPublishCount(questionPublishCount);

            // 获取评论数
            CommentVO commentVO = new CommentVO();
            commentVO.setUserUid(user.getUid());
            Integer commentCount = commentService.getCommentCount(commentVO);
            user.setCommentPublishCount(commentCount);

            // 获取问答数
            UserMomentVO userMomentVO = new UserMomentVO();
            userMomentVO.setUserUid(user.getUid());
            Integer momentCount = userMomentService.getUserMomentCount(userMomentVO);
            user.setMomentPublishCount(momentCount);
        }
    }

    @Override
    public String flushUserCredits() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<User> userList = userService.list(queryWrapper);
        for (User user : userList) {
            int credits = 0;
            // 获取博客数
            BlogVO blogVO = new BlogVO();
            blogVO.setUserUid(user.getUid());
            Integer blogPublishCount = blogService.getBlogCount(blogVO);
            credits += blogPublishCount * ECreditType.Blog.getCredit();

            // 获取问答数
            QuestionVO questionVO = new QuestionVO();
            questionVO.setUserUid(user.getUid());
            Integer questionPublishCount = questionService.getQuestionCount(questionVO);
            credits += questionPublishCount * ECreditType.Question.getCredit();

            // 获取评论数
            CommentVO commentVO = new CommentVO();
            commentVO.setUserUid(user.getUid());
            Integer commentCount = commentService.getCommentCount(commentVO);
            credits += commentCount * ECreditType.Comment.getCredit();

            // 获取用户签到次数
            Integer signInCount = signInRecordService.getSignInCount(user.getUid());
            credits += signInCount * ECreditType.SignIn.getCredit();

            user.setCredits(credits);
        }
        Boolean isSave = userService.updateBatchById(userList);
        if (isSave) {
            redisUtil.delete(RedisConf.INDEX_TOP_N_USER);
            return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
        }
    }

    @Override
    public String flushUserAccount() {
        // 查询出账号非蘑菇注册的账号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.ne(SQLConf.SOURCE, "MOGU");
        List<User> userList = userService.list(queryWrapper);
        Set<String> hashSet = new HashSet<>();

        for (User user : userList) {
            Boolean isContains = hashSet.contains(user.getUuid());
            // 未包含过，进行处理
            if (!isContains) {
                hashSet.add(user.getUuid());

                UserAccount userAccount = new UserAccount();
                userAccount.setUserUid(user.getUid());
                userAccount.setAccountId(user.getUuid());
                userAccount.setUserName(user.getUserName());
                userAccount.setNickName(user.getNickName());
                userAccount.setSource(user.getSource());
                userAccount.setBindTime(user.getCreateTime());
                userAccount.setAvatar(user.getAvatar());
                userAccount.setEmail(user.getEmail());
                userAccount.setGender(user.getGender());
                userAccount.setSummary(user.getSummary());
                userAccount.insert();
            } else {
                // 已经存在，直接跳过
                continue;
            }
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public void updateCreditsByUserUid(String userUid, Integer credits) {
        userMapper.updateCreditsByUserUid(userUid, credits);
    }

    @Override
    public void updateExpValueByUserUid(String userUid, Integer expValue) {
        userMapper.updateExpValueByUserUid(userUid, expValue);
    }

    @Override
    public User getUserByUuidAndSource(String uuid, String source) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.SOURCE, source);
        queryWrapper.eq(SQLConf.ACCOUNT_ID, uuid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        UserAccount userAccount = userAccountService.getOne(queryWrapper);
        if (userAccount != null) {
            String userUid = userAccount.getUserUid();
            return userService.getById(userUid);
        }
        return null;
    }

    /**
     * 查询积分总榜
     *
     * @return
     */
    @Override
    public List<User> getLeaderAll(Boolean refresh) {
        String rankUserListJson = redisUtil.get(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_ALL_LIST);
        List<User> userList;
        if (StringUtils.isNotEmpty(rankUserListJson) && refresh) {
            userList = JsonUtils.jsonToList(rankUserListJson, User.class);
        } else {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.orderByDesc(SQLConf.CREDITS);
            queryWrapper.last("limit 10");
            List<User> leaderAll = userService.list(queryWrapper);
            // 过滤敏感信息
            userService.setUserAvatar(leaderAll);
            userList = userService.convertUserList(leaderAll);
            redisUtil.setEx(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_ALL_LIST, JsonUtils.objectToJson(userList), 24, TimeUnit.HOURS);
        }
        return userList;
    }

    @Override
    public Integer getUserRank(String userUid, boolean refresh) {
        if (StringUtils.isEmpty(userUid)) {
            log.error("[getUserRank] " + MessageConf.PARAM_INCORRECT);
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        String rankJson = redisUtil.get(RedisConf.LOGIN_TOKEN_KEY + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(rankJson) && !refresh) {
            return Integer.valueOf(rankJson);
        }
        Integer rank = userMapper.getUserRank(userUid);
        if (rank != null) {
            redisUtil.setEx(RedisConf.LOGIN_TOKEN_KEY + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(rank), 10, TimeUnit.MINUTES);
        }
        return rank;
    }

    @Override
    public Map<String, User> getRobotUserList() {
        // 获取机器人列表
        String robotUserListJson = redisUtil.get(RedisConf.ROBOT_USER_LIST);
        List<User> robotUserList = new ArrayList<>();
        if (StringUtils.isNotEmpty(robotUserListJson)) {
            robotUserList = JsonUtils.jsonToList(robotUserListJson, User.class);
        } else {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq(SQLConf.USER_TAG, EUserTag.ROBOT.getValue());
            userQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            robotUserList = userService.list(userQueryWrapper);
            // 设置头像，过滤敏感信息
            if (robotUserList.size() > 0) {
                setUserAvatar(robotUserList);
                robotUserList = convertUserList(robotUserList);
            }
        }
        if (robotUserList != null && robotUserList.size() > 0) {
            redisUtil.setEx(RedisConf.ROBOT_USER_LIST, JsonUtils.objectToJson(robotUserList), 10, TimeUnit.MINUTES);
        }
        return robotUserList.stream()
                .collect(Collectors.toMap(User::getUid, Function.identity()));
    }

    @Override
    public String getUserCenterUrl(String userUid) {
        return webSiteUrl + "userCenter?userUid=" + userUid;
    }

    @Override
    public ChatGPTSetting checkRobotReply(String userUid, int userTag) {
        ChatGPTSetting chatGPTSetting = new ChatGPTSetting();
        chatGPTSetting.setBlock(false);
        chatGPTSetting.setOpenRobotReply(false);

        // 获取ChatGPT配置
        String chatGPTSettingJson = sysParamsService.getSysParamsValueByKey(SysConf.SYS_CHATGPT_SETTING);
        if (StringUtils.isEmpty(chatGPTSettingJson)) {
            chatGPTSetting.setOpenRobotReply(true);
            chatGPTSetting.setBlockMessage("系统配置异常");
            return chatGPTSetting;
        }

        chatGPTSetting = JsonUtils.jsonToPojo(chatGPTSettingJson, ChatGPTSetting.class);
        assert chatGPTSetting != null;

        String countJson = redisUtil.get(RedisConf.CHATGPT_REPLY_COUNT + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isEmpty(countJson)) {
            return chatGPTSetting;
        }
        int count = Integer.parseInt(countJson);
        int limitCount = 0;
        if (userTag == 0) {
            // 普通用户，校验普通次数
            limitCount = chatGPTSetting.getReplyCount();
        } else {
            // vip用户，校验vip次数
            limitCount = chatGPTSetting.getReplyCount();
        }
        chatGPTSetting.setLimitCount(count);
        chatGPTSetting.setBlock(limitCount < count);
        return chatGPTSetting;
    }

    @Override
    public String chatGPTReply(String openID, String message) {

        if (StringUtils.isEmpty(openID)) {
            return ResultUtil.errorWithMessage("请求参数异常");
        }

        // 获取用户信息
        String userJson = redisUtil.get(RedisConf.ACCOUNT_ID_TO_USER + Constants.SYMBOL_COLON + openID);
        User user;
        if (StringUtils.isEmpty(userJson)) {
            QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
            userAccountQueryWrapper.eq(SQLConf.ACCOUNT_ID, openID);
            userAccountQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            userAccountQueryWrapper.last(SysConf.LIMIT_ONE);
            UserAccount userAccount = userAccountService.getOne(userAccountQueryWrapper);
            if (userAccount == null) {
                log.error("[chatGPTReply] 账户信息为空, AccountID: {}", openID);
                return ResultUtil.errorWithMessage("该微信暂未在网站进行注册，请先完成微信登录后再继续~");
            }
            user = userService.getById(userAccount.getUserUid());
            if (user != null) {
                redisUtil.setEx(RedisConf.ACCOUNT_ID_TO_USER + Constants.SYMBOL_COLON + openID, JsonUtils.objectToJson(user), 10, TimeUnit.MINUTES);
            }
        } else {
            user = JsonUtils.jsonToPojo(userJson, User.class);
        }

        if (user == null) {
            log.error("[chatGPTReply] 用户信息为空, AccountID: {}", openID);
            return ResultUtil.errorWithMessage("未查询到用户信息，请稍后再试");
        }

        // 开始进行机器人回复
        // 获取ChatGPT配置
        ChatGPTSetting chatGPTSetting = userService.checkRobotReply(user.getUid(), user.getUserTag());

        String regex = "<a[^>]*>[^<]*<\\/a>";
        String result = message.replaceAll(regex, "");
        Document doc = Jsoup.parse(result);
        String question = doc.text();
        String chatGPTResult = "";
        // 是否开启
        if (chatGPTSetting == null || !chatGPTSetting.isOpenRobotReply()) {
            chatGPTResult = "抱歉，后台暂未开启智能问答，请联系管理员";
        } else {
            // 开启了问答，校验是否还有提问次数
            if (chatGPTSetting.isBlock()) {
                chatGPTResult = "抱歉，今日机器人回复您的次数已达到上限，暂时无法提供问答服务";
            } else {
                // chatGPTResult = HttpRequestUtil.askOpenAi(question, chatGPTSetting.getApiUrl(), chatGPTSetting.getApiKey(), user.getUid());
                chatGPTResult = smsFeignClient.getTextAnswer(question);
                // 没有回复内容，可能内部系统存在问题
                if (StringUtils.isEmpty(chatGPTResult)) {
                    chatGPTResult = "抱歉，网络出现问题，请稍后再试";
                } else {
                    chatGPTResult = chatGPTResult.replaceFirst("\\n", "").replaceFirst("\\n", "");

                    // redis计数器增加
                    redisUtil.setEx(RedisConf.CHATGPT_REPLY_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(chatGPTSetting.getLimitCount() + 1), 12, TimeUnit.HOURS);
                }
            }
        }
        return ResultUtil.successWithData(chatGPTResult);
    }

    @Override
    public String getUserUidByRequest() {
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(userUid)) {
            return userUid;
        }

        String adminUid = RequestHolder.getAdminUid();
        if (StringUtils.isEmpty(adminUid)) {
            throw new QueryException("用户未登录");
        }
        // 当为后台管理员添加的时候
        Admin admin = adminService.getById(adminUid);
        if (admin == null) {
            throw new QueryException("管理员状态错误,请重新登录后!");
        }
        // 判断是否附身用户
        if (StringUtils.isEmpty(admin.getUserUid())) {
            throw new QueryException("您暂未附身用户，无法进行操作，请到 管理员管理-> 编辑, 选择要附身用户 再重新操作!");
        }
        return admin.getUserUid();
    }

    @Override
    public Long getUserAmount(String userUid) {
        User user = userService.getById(userUid);
        if (user == null) {
            return 0L;
        }
        return user.getAmount();
    }

    @Override
    public void updateAmountByUserUid(String userUid, Long operateAmount) {
        // 如果操作的金额是负数，说明是积分扣除操作，还需要获取用户的当前余额
        User user = userService.getById(userUid);
        if (user == null) {
            log.error("[updateAmountByUserUid] 暂未查询到该用户，userUid: " + userUid);
            throw new UpdateException("暂未查询到该用户");
        }
        if (operateAmount < 0) {
            if (user.getAmount() + operateAmount < 0) {
                log.error("[updateAmountByUserUid] 用户钱包余额不足，操作失败，userUid: " + userUid);
                throw new UpdateException("余额不足，操作失败");
            }
        }
        userMapper.updateAmountByUserUid(userUid, operateAmount);
    }

    @Override
    public void refreshUserCache(String userUid) {
        User user = userService.getById(userUid);
        if (user == null) {
            return;
        }
        String token = redisUtil.get(RedisConf.USER_UID_TO_TOKEN + Constants.SYMBOL_COLON + userUid);
        if (StringUtils.isNotEmpty(token)) {
            // 根据token替换用户一些内容
            token = token.replaceAll("\"", "");
            String userJson = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
            User userCache = JsonUtils.jsonToPojo(userJson, User.class);
            if (userCache != null) {
                // 更新最新的数据
                userCache.setAuthCodeList(user.getAuthCodeList());
                userCache.setUserTag(user.getUserTag());
                // 直接更新TOKEN信息
                redisUtil.setEx(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JsonUtils.objectToJson(userCache), expiresSecond, TimeUnit.SECONDS);
                redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, "user", token), JsonUtils.objectToJson(user), expiresSecond, TimeUnit.SECONDS);
            }
        }
    }
}
