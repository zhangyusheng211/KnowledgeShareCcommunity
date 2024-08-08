package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.Medal;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 关于我 RestApi
 *
 * @author 陌溪
 * @date 2018年11月12日14:51:54
 */
@RestController
@RequestMapping("/about")
@Api(value = "关于我相关接口", tags = {"关于我相关接口"})
@Slf4j
public class UserCenterRestApi {

    @Resource
    private AdminService adminService;
    @Resource
    private WebConfigService webConfigService;
    @Resource
    private BlogService blogService;
    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;
    @Resource
    private UserWatchService userWatchService;
    @Resource
    private SignInRecordService signInRecordService;
    @Resource
    private UserMomentService userMomentService;
    @Resource
    private ProblemService problemService;
    @Resource
    private CommentService commentService;
    @Resource
    private CollectService collectService;
    @Resource
    private UserEquityRecordService userEquityRecordService;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private WebVisitService webVisitService;

    @Resource
    private UserPraiseRecordService userPraiseRecordService;
    @Resource
    private MedalService medalService;

    /**
     * 获取关于我的信息
     */
    @BussinessLog(value = "关于我", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation(value = "关于我", notes = "关于我")
    @GetMapping("/getMe")
    public String getMe() {

        log.info("获取关于我的信息");
        return ResultUtil.result(SysConf.SUCCESS, adminService.getAdminByUser(SysConf.ADMIN));
    }

    @ApiOperation(value = "获取联系方式", notes = "获取联系方式")
    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @ApiOperation(value = "通过ID获取用户【包含用户和管理员】", notes = "通过ID获取用户")
    @GetMapping("/getUserByUid")
    public String getUserByUid(HttpServletRequest request, @ApiParam(name = "adminUid", value = "管理员uid", required = false) @RequestParam(name = "adminUid", required = false, defaultValue = "") String adminUid,
                               @ApiParam(name = "userUid", value = "用户uid", required = false) @RequestParam(name = "userUid", required = false, defaultValue = "") String userUid,
                               @ApiParam(name = "refresh", value = "是否强制刷新", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") boolean refresh) throws ExecutionException, InterruptedException {
        Object currentUserUid = request.getAttribute(SysConf.USER_UID);
        if (StringUtils.isNotEmpty(adminUid)) {
            List<String> adminUidList = new ArrayList<>();
            adminUidList.add(adminUid);
            List<Admin> adminList = adminService.getAdminListByUid(adminUidList);
            if (adminList.size() == 0) {
                throw new QueryException(MessageConf.PARAM_INCORRECT);
            }
            // 默认没有关注用户
            Boolean isWatchUser = false;
            if (currentUserUid != null) {
                UserWatchVO userWatchVO = new UserWatchVO();
                userWatchVO.setUserUid(currentUserUid.toString());
                userWatchVO.setToUserUid(adminUid);
                isWatchUser = userWatchService.checkUserWatch(userWatchVO);
            } else {
                log.info("前端用户未登录");
            }
            // 获取管理员
            Admin admin = adminList.get(0);
            admin.setIsWatchUser(isWatchUser);
            return ResultUtil.successWithData(admin);
        } else if (StringUtils.isNotEmpty(userUid)) {
            List<String> userUidList = new ArrayList<>();
            userUidList.add(userUid);
            List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
            //从Redis中获取用户卡片信息
            String jsonResult = redisUtil.get(RedisConf.USER_CARD_DETAIL + Constants.SYMBOL_COLON + userUid);
            if (StringUtils.isEmpty(jsonResult) || refresh) {
                if (userList.size() > 0) {
                    // 创建一个最大线程数为10的线程池
                    ExecutorService threadPool = Executors.newFixedThreadPool(12);
                    List<Callable<Void>> tasks = new ArrayList<>(); // 定义任务列表
                    User user = userList.get(0);

                    // 添加文章数设置任务
                    tasks.add(() -> {
                        // 设置文章数
                        Integer blogPublishCount = userService.getBlogPublishCount(user.getUid());
                        user.setBlogPublishCount(blogPublishCount);
                        return null;
                    });

                    // 添加阅读数设置任务
                    tasks.add(() -> {
                        // 设置阅读数
                        // 默认没有关注用户
                        boolean isWatchUser = false;
                        if (currentUserUid != null) {
                            UserWatchVO userWatchVO = new UserWatchVO();
                            userWatchVO.setUserUid(currentUserUid.toString());
                            userWatchVO.setToUserUid(userUid);
                            isWatchUser = userWatchService.checkUserWatch(userWatchVO);
                        } else {
                            log.info("前端用户未登录");
                        }
                        Integer blogVisitCount = userService.getBlogVisitCount(user.getUid());
                        user.setBlogVisitCount(blogVisitCount);
                        user.setIsWatchUser(isWatchUser);
                        return null;
                    });

                    // 添加关注人数设置任务
                    tasks.add(() -> {
                        // 获取关注人数
                        UserWatchVO watches = new UserWatchVO();
                        watches.setUserUid(userUid);
                        user.setUserWatchCount(userWatchService.getUserWatchCount(watches));
                        return null;
                    });

                    // 添加粉丝人数设置任务
                    tasks.add(() -> {
                        UserWatchVO followes = new UserWatchVO();
                        followes.setToUserUid(userUid);
                        user.setUserFollowCount(userWatchService.getUserWatchCount(followes));
                        return null;
                    });

                    // 添加动态数量设置任务
                    tasks.add(() -> {
                        UserMomentVO userMomentVO = new UserMomentVO();
                        userMomentVO.setUserUid(userUid);
                        user.setUserMomentCount(userMomentService.getUserMomentCount(userMomentVO));
                        return null;
                    });

                    // 添加问题数量设置任务
                    tasks.add(() -> {
                        ProblemVO problemVO = new ProblemVO();
                        problemVO.setUserUid(userUid);
                        user.setProblemPublishCount(problemService.getProblemCount(problemVO));
                        return null;
                    });

                    // 添加收藏数量设置任务
                    tasks.add(() -> {
                        CollectVO collectVO = new CollectVO();
                        collectVO.setUserUid(userUid);
                        user.setUserCollectCount(collectService.getUserCollectCount(collectVO));
                        return null;
                    });

                    // 添加评论数量设置任务
                    tasks.add(() -> {
                        CommentVO commentVO = new CommentVO();
                        commentVO.setUserUid(userUid);
                        user.setUserCommentCount(commentService.getCommentCount(commentVO));
                        return null;
                    });

                    // 添加勋章列表设置任务
                    tasks.add(() -> {
                        MedalVO medalVO = new MedalVO();
                        medalVO.setUserUid(userUid);
                        List<Medal> medalList = medalService.getUserShowMedalList(medalVO);
                        user.setUserMedalList(medalList);
                        user.setUserMedalCount(medalList.size());
                        return null;
                    });

                    // 添加勋章列表设置任务
                    tasks.add(() -> {
                        // 设置用户排名
                        Integer rank = userService.getUserRank(userUid, refresh);
                        user.setRank(rank);
                        // 设置当前等级上限积分
                        user.setLevelMaxCredits(EUserLevel.getLvByExpValue(user.getExpValue()).getMaxCredit());
                        return null;
                    });

                    // 添加勋章列表设置任务
                    tasks.add(() -> {
                        // 设置七日访问量
                        user.setWebVisitCountByWeek(webVisitService.getVisitCountByWeek(userUid));
                        return null;
                    });

                    // 添加勋章列表设置任务
                    tasks.add(() -> {
                        // 七日点赞数
                        user.setPraiseCountByWeek(userPraiseRecordService.getPraiseCountByWeek(userUid));
                        return null;
                    });

                    threadPool.invokeAll(tasks); // 并发执行所有任务
                    threadPool.shutdown(); // 关闭线程池

                    // 将用户卡片数据 存储到 redis
                    redisUtil.setEx(RedisConf.USER_CARD_DETAIL + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(user), 10, TimeUnit.MINUTES);
                    return ResultUtil.successWithData(user);
                } else {
                    return ResultUtil.errorWithMessage("该用户不存在");
                }
            } else {
                User user = JsonUtils.jsonToPojo(jsonResult, User.class);
                return ResultUtil.successWithData(user);
            }
        }
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "通过ID获取用户个人中心统计信息", notes = "通过ID获取用户个人中心统计信息")
    @GetMapping("/getUserCenterByUid")
    public String getInfoByUid(HttpServletRequest request,
                               @ApiParam(name = "adminUid", value = "管理员uid", required = false) @RequestParam(name = "adminUid", required = false, defaultValue = "") String adminUid,
                               @ApiParam(name = "userUid", value = "用户uid", required = false) @RequestParam(name = "userUid", required = false, defaultValue = "") String userUid) {
        if (StringUtils.isNotEmpty(adminUid)) {

            // 获取发表的文章数
            BlogVO blogVO = new BlogVO();
            blogVO.setAdminUid(adminUid);
            Map<String, Integer> map = new HashMap<>();
            map.put("blogCount", blogService.getBlogCount(blogVO));
            // 获取发表的问答数
            QuestionVO questionVO = new QuestionVO();
            questionVO.setAdminUid(adminUid);
            map.put("questionCount", questionService.getQuestionCount(questionVO));
            // 获取关注人数
            UserWatchVO watches = new UserWatchVO();
            watches.setUserUid(adminUid);
            map.put("watchCount", userWatchService.getUserWatchCount(watches));
            // 获取粉丝人数
            UserWatchVO followes = new UserWatchVO();
            followes.setToUserUid(adminUid);
            map.put("followCount", userWatchService.getUserWatchCount(followes));
            // 超管没有动态和问题数
            map.put("momentCount", 0);
            map.put("problemCount", 0);
            return ResultUtil.successWithData(map);

        } else if (StringUtils.isNotEmpty(userUid)) {
            BlogVO blogVO = new BlogVO();
            blogVO.setUserUid(userUid);
            Map<String, Integer> map = new HashMap<>();
            map.put("blogCount", blogService.getBlogCount(blogVO));
            QuestionVO questionVO = new QuestionVO();
            questionVO.setUserUid(userUid);
            map.put("questionCount", questionService.getQuestionCount(questionVO));

            // 获取关注人数
            UserWatchVO watches = new UserWatchVO();
            watches.setUserUid(userUid);
            map.put("watchCount", userWatchService.getUserWatchCount(watches));

            // 获取粉丝人数
            UserWatchVO followes = new UserWatchVO();
            followes.setToUserUid(userUid);
            map.put("followCount", userWatchService.getUserWatchCount(followes));

            // 设置动态数量
            UserMomentVO userMomentVO = new UserMomentVO();
            userMomentVO.setUserUid(userUid);
            map.put("momentCount", userMomentService.getUserMomentCount(userMomentVO));

            // 设置动态数量
            ProblemVO problemVO = new ProblemVO();
            problemVO.setUserUid(userUid);
            map.put("problemCount", problemService.getProblemCount(problemVO));

            return ResultUtil.successWithData(map);
        }

        log.info("通过ID获取用户个人中心信息");
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "通过用户获取博客列表", notes = "通过用户获取博客列表")
    @PostMapping("/getBlogListByUser")
    public String getBlogListByUser(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取博客列表");
        blogVO.setIsPublish(EPublish.PUBLISH);
        blogVO.setAuditStatus(EAuditStatus.AGREE);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getPageList(blogVO));
    }

    @ApiOperation(value = "通过用户获取问答列表", notes = "通过用户获取问答列表")
    @PostMapping("/getQuestionListByUser")
    public String getQuestionListByUser(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取问答列表");
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

    @ApiOperation(value = "获取用户关注列表", notes = "获取用户关注列表")
    @PostMapping("/getUserWatchList")
    public String getUserWatchList(@Validated({GetList.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取用户关注列表");
        return ResultUtil.successWithData(userWatchService.getPageList(userWatchVO));
    }

    @ApiOperation(value = "判断当前登录用户，是否关注过某人", notes = "判断当前登录用户，是否关注过某人")
    @PostMapping("/checkCurrentUserWatch")
    public String checkCurrentUserWatch(@RequestBody UserWatchVO userWatchVO) {
        log.info("获取用户关注列表");
        return ResultUtil.successWithData(userWatchService.checkCurrentUserWatch(userWatchVO));
    }


    @AvoidRepeatableCommit(timeout = 5000)
    @ApiOperation(value = "关注某人", notes = "关注某人")
    @PostMapping("/addUserWatch")
    public String addUserWatch(@Validated({Insert.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("关注某人");
        return userWatchService.addUserWatch(userWatchVO);
    }

    @AvoidRepeatableCommit(timeout = 5000)
    @ApiOperation(value = "取消关注某人", notes = "取消关注某人")
    @PostMapping("/deleteUserWatch")
    public String deleteUserWatch(@Validated({Insert.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("取消关注某人");
        return userWatchService.deleteUserWatch(userWatchVO);
    }

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getQuestionList")
    public String getQuestionList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        // 前端没有传递用户UID时，将查询在线用户的博客列表
        if (StringUtils.isEmpty(questionVO.getUserUid()) && request.getAttribute(SysConf.USER_UID) != null) {
            questionVO.setUserUid(request.getAttribute(SysConf.USER_UID).toString());
        }
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

    @AvoidRepeatableCommit
    @BussinessLog(value = "用户签到", behavior = EBehavior.SIGN_IN)
    @ApiOperation(value = "用户签到", notes = "用户签到")
    @PostMapping("/userSignIn")
    public String userSignIn() {
        log.info("用户签到");
        String userUid = RequestHolder.getUserUid();
        return signInRecordService.userSignIn(userUid);
    }

    @ApiOperation(value = "微信用户签到回调接口", notes = "微信用户签到回调接口")
    @PostMapping(value = "/wechatUserSignIn")
    public String wechatUserLogin(@RequestParam(value = "openid") String openid, @RequestParam(value = "sign") String sign) {

        // 签名验证
        Map<String, Object> data = new HashMap<>();
        data.put("openid", openid);
        data.put("sign", sign);
        if (!SignUtils.isSignEquals(data)) {
            return ResultUtil.errorWithMessage("签名校验失败");
        }

        User user = userService.getUserByUuidAndSource(openid, ELoginType.WECHAT.getName());
        if (user != null) {
            return signInRecordService.userSignIn(user.getUid());
        }
        return ResultUtil.errorWithMessage("用户不存在");
    }

    @BussinessLog(value = "用户补签", behavior = EBehavior.RETROACTIVE_SIGN_IN)
    @ApiOperation(value = "用户补签", notes = "用户补签")
    @PostMapping("/retroactive")
    public String retroactive(@RequestBody SignInRecordVO signInVO) {
        log.info("用户补签");
        return signInRecordService.retroactive(signInVO);
    }

    @ApiOperation(value = "用户当月签到信息", notes = "用户当月签到信息")
    @GetMapping("/signDataByUserUid")
    public String signDataByUserUid(@ApiParam(name = "refresh", value = "是否刷新") @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh,
                                    @ApiParam(name = "dateStr", value = "选择日期") @RequestParam(name = "dateStr", required = false) String dateStr) {
        log.info("用户当月签到信息");
        return signInRecordService.signDataByUserUid(refresh, dateStr);
    }

    @ApiOperation(value = "获取登录用户积分", notes = "获取登录用户积分")
    @PostMapping("/getCurrentUserCredits")
    public String getCurrentUserCredits(@ApiParam(name = "refresh", value = "是否刷新") @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        log.info("获取用户积分");
        return ResultUtil.successWithData(userService.getCurrentUserCredits(refresh));
    }

    @SubmitVerify
    @AvoidRepeatableCommit(timeout = 30000)
    @BussinessLog(value = "更新背景图片", behavior = EBehavior.UPDATE_BACKGROUND_IMAGE)
    @ApiOperation(value = "更新背景图片", notes = "更新当前用户背景图片")
    @PostMapping("/updateCurrentUserBackgroundImage")
    public String updateCurrentUserBackgroundImage(@RequestBody UserVO userVO) {
        log.info("更新背景图片");
        return userService.updateCurrentUserBackgroundImage(userVO);
    }


    @BussinessLog(value = "搜索群聊用户", behavior = EBehavior.SEARCH_USER_LIST)
    @ApiOperation(value = "搜索群聊用户", notes = "搜索群聊用户")
    @PostMapping("/getUserList")
    public String getUserList(@RequestBody UserVO userVO) {
        String userUid = RequestHolder.getUserUid();
        log.info("搜索群聊用户");
        userVO.setPageSize(10L);
        IPage<User> userIPage = userService.getPageList(userVO);
        List<User> userList = userService.convertUserList(userIPage.getRecords());
        List<User> newUserList = new ArrayList<>();
        for (User user : userList) {
            // 排除自己
            if (user.getUid().equals(userUid)) {
                continue;
            }
            newUserList.add(user);
        }
        userIPage.setRecords(newUserList);
        return ResultUtil.successWithData(userIPage);
    }

    @FeignSecurity
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @PostMapping("/getUserListByPage")
    public List<User> getUserListByPage(@RequestBody UserVO userVO) {
        log.info("获取用户列表");
        IPage<User> userIPage = userService.getPageList(userVO);
        return userService.convertUserList(userIPage.getRecords());
    }

    /**
     * 查询积分总榜
     * @return
     */
    @ApiOperation(value = "查询积分总榜", notes = "查询积分总榜")
    @GetMapping(value = "/getLeaderAll")
    public String getLeaderAll(@ApiParam(name = "refresh", value = "是否刷新配置", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        List<User> leaderAll = userService.getLeaderAll(refresh);
        return ResultUtil.result(SysConf.SUCCESS, leaderAll);
    }

    /**
     * 获取当前用户钱包金额
     * @return
     */
    @ApiOperation(value = "获取当前用户钱包金额", notes = "获取当前用户钱包金额")
    @PostMapping("/getCurrentUserAmount")
    public String getCurrentUserAmount() {
        log.info("获取当前用户钱包金额");
        return ResultUtil.successWithData(userService.getUserAmount(RequestHolder.getUserUid()));
    }
}

