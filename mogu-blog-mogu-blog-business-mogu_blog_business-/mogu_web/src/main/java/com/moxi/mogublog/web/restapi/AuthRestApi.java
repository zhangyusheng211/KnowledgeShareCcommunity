package com.moxi.mogublog.web.restapi;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meetc.mogublog.security.SecurityUser;
import com.meetc.mogublog.security.jwt.Audience;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import com.meetc.mogublog.security.jwt.UserType;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PayFeignClient;
import com.moxi.mogublog.commons.schema.BindRequest;
import com.moxi.mogublog.commons.schema.LoginRequest;
import com.moxi.mogublog.commons.schema.WechatOauthInfoPkg.WechatOauthInfo;
import com.moxi.mogublog.commons.schema.WechatOauthInfoPkg.WechatOauthInfoData;
import com.moxi.mogublog.commons.schema.WechatOauthInfoPkg.WechatWebLoginData;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.utils.text.Convert;
import com.moxi.mogublog.utils.wechat.WechatUtils;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.manager.AuthUserLoginManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mogublog.commons.vo.FeedbackVO;
import com.moxi.mogublog.commons.vo.LinkVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.util.RequestUtil;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.enums.AuthUserGender;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.chanjar.weixin.common.util.http.URIUtil.encodeURIComponent;

/**
 * 第三方登录认证
 *
 * @author 陌溪
 * @date 2020年10月11日10:25:58
 */
@RestController
@RefreshScope
@RequestMapping("/oauth")
@Api(value = "第三方登录相关接口", tags = {"第三方登录相关接口"})
@Slf4j
public class AuthRestApi {
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private UserService userService;
    @Value(value = "${data.webSite.url}")
    private String webSiteUrl;
    @Value(value = "${data.webSite.h5Url:''}")
    private String h5WebSiteUrl;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    AuthUserLoginManager loginManager;
    @Resource
    AuthenticationManager authenticationManager;
    @Resource
    private Audience audience;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Value(value = "${tokenHead}")
    private String tokenHead;
    @Resource
    AsyncService asyncService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private SysParamsService sysParamsService;

    @Resource
    private PayFeignClient payFeignClient;

    @Resource
    private SecretConfigService secretConfigService;


    @ApiOperation(value = "获取认证", notes = "获取认证")
    @RequestMapping("/render")
    public String renderAuth(String source, String type, String webSiteSource) {
        // 将传递过来的转换成大写
        Boolean isOpenLoginType = webConfigService.isOpenLoginType(source.toUpperCase());
        if (!isOpenLoginType) {
            log.error("[renderAuth] 后台未开启该登录方式! source: {}", source);
            return ResultUtil.errorWithMessage("后台未开启该登录方式!");
        }
        if (StringUtils.isEmpty(webSiteSource)) {
            webSiteSource = "pc";
        }
        log.info("[renderAuth] 进入render:" + source);
        AuthRequest authRequest = getAuthRequest(source);
        String userToken = "";
        // 添加指定的前缀的token，用来标识是绑定还是登录
        if (SysConf.LOGIN.equals(type)) {
            // 登录动作，啥事不处理
            log.info("[renderAuth] 用户第三方登录");
            userToken = String.format("%s_%s_%s", SysConf.LOGIN, AuthStateUtils.createState(), webSiteSource);
        } else if (SysConf.BIND.equals(type)) {
            // 如果是绑定动作，需要获取到用户的token
            String userUid = RequestHolder.getUserUid();
            log.info("[renderAuth] 用户第三方账号绑定, userUid: {}", userUid);
            userToken = String.format("%s_%s_%s", SysConf.BIND, userUid, webSiteSource);
        }

        String authorizeUrl = authRequest.authorize(userToken);
        Map<String, String> map = new HashMap<>();
        map.put(SQLConf.URL, authorizeUrl);
        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    /**
     * oauth平台中配置的授权回调地址，以本项目为例，在创建gitee授权应用时的回调地址应为：http://127.0.0.1:8603/oauth/callback/gitee
     * 引入 @Synchronized 目的是为了防止并发创建多个账号
     */
    @Synchronized
    @RequestMapping("/callback/{source}")
    public void login(@PathVariable("source") String source, AuthCallback callback, HttpServletResponse httpServletResponse, HttpServletRequest request) throws IOException {
        log.info("[login] 进入callback：" + source + " callback params：" + JSONObject.toJSONString(callback));
        AuthRequest authRequest = getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (response.getCode() == Constants.NUM_5000) {
            // 跳转到500错误页面
            log.error("[login] 第三方登录失败, error: {}", JsonUtils.objectToJson(response));
            httpServletResponse.sendRedirect(webSiteUrl + Constants.STR_500);
            return;
        }
        AuthUser data = response.getData();
        String userToken = callback.getState();
        if (data == null || data.getToken() == null || StringUtils.isEmpty(userToken)) {
            // 跳转到500错误页面
            log.error("[login] 第三方登录失败, error: {}", JsonUtils.objectToJson(response));
            httpServletResponse.sendRedirect(webSiteUrl + Constants.STR_500);
            return;
        }

        // 根据token类型，判断走的逻辑
        User user;
        if (userToken.contains(SysConf.LOGIN)) {
            user = loginManager.login(source, data);
        } else {
            String userUid = userToken.substring(5);
            if (StringUtils.isEmpty(userUid)) {
                log.error("[login] 绑定第三方账号失败, 用户uid为空");
                httpServletResponse.sendRedirect(webSiteUrl + "error?msg=" + StringUtils.encode("用户账号已经被激活"));
                return;
            }
            user = loginManager.bind(source, data, userUid);
        }

        // 判断是PC还是移动端
        boolean isH5 = false;
        if (userToken.endsWith("h5")) {
            isH5 = true;
        }

        // 用户登录或注册逻辑
        String accessToken = "";
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), null));
            //执行登录
            SecurityContextHolder.getContext().setAuthentication(auth);
            //获取登录信息
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String ip = IpUtils.getIpAddr(request);
            String jwtToken = jwtTokenUtil.createJwt(
                    securityUser.getUid(),
                    securityUser.getUsername(),
                    ip,
                    UserType.USER,
                    audience.getClientId(),
                    audience.getName(),
                    audience.getExpiresSecond() * 1000,
                    audience.getBase64Secret());
            accessToken = tokenHead + jwtToken;
            // 将新的Token存入Redis中
            redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.USER, accessToken), JsonUtils.objectToJson(securityUser), audience.getExpiresSecond(), TimeUnit.SECONDS);
            Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
            result.put(SysConf.TOKEN, accessToken);
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中
            redisUtil.setEx(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
            // 存储用户uid到token的关联关系
            redisUtil.setEx(RedisConf.USER_UID_TO_TOKEN + Constants.SYMBOL_COLON + user.getUid(), accessToken, audience.getExpiresSecond(), TimeUnit.SECONDS);
        } catch (AuthenticationException e) {
            log.error("[login] 登录服务异常, error: {}", e.getMessage());
            ResultUtil.errorWithMessage(e.getMessage());
        } finally {
            // 如果是h5端，需要重定向到h5页面
            if (isH5) {
                httpServletResponse.sendRedirect(h5WebSiteUrl + "?token=" + accessToken);
            } else {
                httpServletResponse.sendRedirect(webSiteUrl + "?token=" + accessToken);
            }

        }
    }


    @RequestMapping("/revoke/{source}/{token}")
    public Object revokeAuth(@PathVariable("source") String source, @PathVariable("token") String token) throws IOException {
        AuthRequest authRequest = getAuthRequest(source);
        return authRequest.revoke(AuthToken.builder().accessToken(token).build());
    }

    @RequestMapping("/refresh/{source}")
    public Object refreshAuth(@PathVariable("source") String source, String token) {
        AuthRequest authRequest = getAuthRequest(source);
        return authRequest.refresh(AuthToken.builder().refreshToken(token).build());
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @GetMapping("/verify/{accessToken}")
    public String verifyUser(@PathVariable("accessToken") String accessToken) {
        if (!accessToken.startsWith(tokenHead)) {
            log.error("[verifyUser] Token为空");
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        // 判断附身权限
        String userUid = RequestHolder.getRequest().getHeader(SysConf.X_USER_UID);
        if (StringUtils.isNotEmpty(userUid)) {
            // 判断当前登录的用户能否直接附身操作【从参数Key中获取附身的key】
            String authUserUidListJson = sysParamsService.getSysParamsValueByKey(SysConf.SYS_AUTH_USER);
            // 判断当前用户能否附身
            boolean authSuccess = false;
            // 获取当前登录的用户信息
            String nowUserInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
            String[] authUserUidList = authUserUidListJson.split(",");
            for (String authUserUid : authUserUidList) {
                if (nowUserInfo.contains(authUserUid)) {
                    authSuccess = true;
                }
            }
            // 鉴权成功，可以附身
            if (authSuccess) {
                String token = redisUtil.get(RedisConf.USER_UID_TO_TOKEN + Constants.SYMBOL_COLON + userUid);
                if (StringUtils.isNotEmpty(token)) {
                    accessToken = token.replaceAll("\"", "");
                }
            }
        }
        String userInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
        if (StringUtils.isEmpty(userInfo)) {
            log.error("[verifyUser] 用户信息为空");
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        } else {
            User user = JsonUtils.jsonToPojo(userInfo, User.class);
            if (user != null) {
                // 等级换算
                EUserLevel userLevel = EUserLevel.getLvByExpValue(user.getExpValue());
                user.setUserLevel(userLevel.getLevel());

                if (IpUtils.isInner(user.getLastLoginIp())) {
                    // 如果用戶最后一次登录是内网ip，那么需要重新设置
                    User loginUser = userService.getById(user.getUid());
                    if (loginUser != null) {
                        String ip = RequestHolder.getIp();
                        loginUser.setLastLoginIp(ip);
                        loginUser.setIpSource(IpUtils.getCityInfo(ip));
                        loginUser.updateById();

                        // 返回值替换
                        user.setIpSource(IpUtils.getCityInfo(ip));
                        user.setLastLoginIp(ip);
                    }
                }
                return ResultUtil.result(SysConf.SUCCESS, user);
            }
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
    }

    @ApiOperation(value = "删除accessToken", notes = "删除accessToken")
    @RequestMapping("/delete/{accessToken}")
    public String deleteUserAccessToken(@PathVariable("accessToken") String accessToken) {
        stringRedisTemplate.delete(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    /**
     * 通过token获取七牛云配置
     *
     * @param token
     * @return
     */
    @GetMapping("/getSystemConfig")
    public String getSystemConfig(@RequestParam("token") String token) {
        String userInfo = stringRedisTemplate.opsForValue().get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        if (StringUtils.isEmpty(userInfo)) {
            log.error("[getSystemConfig] 获取系统配置失败，用户令牌失效");
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        QueryWrapper<SystemConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        SystemConfig SystemConfig = systemConfigService.getOne(queryWrapper);
        return ResultUtil.result(SysConf.SUCCESS, SystemConfig);
    }

    /**
     * 编辑用户信息
     */
//    @LoginVerify
    @ApiOperation(value = "编辑用户信息", notes = "编辑用户信息")
    @PostMapping("/editUser")
    public String editUser(HttpServletRequest request,
                           @Validated({Default.class}) @RequestBody UserVO userVO, BindingResult result) {
        if (request.getAttribute(SysConf.USER_UID) == null || request.getAttribute(SysConf.TOKEN) == null) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        ThrowableUtils.checkParamArgument(result);
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        String token = request.getAttribute(SysConf.TOKEN).toString();
        User user = userService.getById(userUid);
        if (user == null) {
            log.error("[editUser] 编辑失败, 未找到该用户!");
            return ResultUtil.errorWithMessage("编辑失败, 未找到该用户!");
        }
        log.info("[editUser] 获取到的用户: {}", JsonUtils.objectToJson(user));
        user.setNickName(userVO.getNickName());
        user.setAvatar(userVO.getAvatar());
        user.setBirthday(userVO.getBirthday());
        user.setSummary(userVO.getSummary());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setOccupation(userVO.getOccupation());
        user.setEditorModel(userVO.getEditorModel());

        // 如果开启邮件通知，必须保证邮箱已存在
        if (userVO.getStartEmailNotification() == SysConf.ONE && !StringUtils.isNotEmpty(user.getEmail())) {
            log.error("[editUser] 必须填写并绑定邮箱后，才能开启评论邮件通知~");
            return ResultUtil.errorWithMessage("必须填写并绑定邮箱后，才能开启评论邮件通知~");
        }
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        user.updateById();
        user.setPassWord("");
        user.setPhotoUrl(userVO.getPhotoUrl());
        // 更新redis中的用户信息

        // 判断用户是否更改了邮箱
        if (userVO.getEmail() != null && !userVO.getEmail().equals(user.getEmail())) {
            user.setEmail(userVO.getEmail());
            // 使用RabbitMQ发送邮件
            rabbitMqUtil.sendRegisterEmail(user, token);
            // 修改成功后，更新Redis中的用户信息
            stringRedisTemplate.opsForValue().set(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
            return ResultUtil.result(SysConf.SUCCESS, "您已修改邮箱，请先到邮箱进行确认绑定");
        } else {
            stringRedisTemplate.opsForValue().set(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
        }
    }

    @ApiOperation(value = "更新用户密码", notes = "更新用户密码")
    @PostMapping("/updateUserPwd")
    public String updateUserPwd(HttpServletRequest request, @RequestParam(value = "oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd) {
        if (StringUtils.isEmpty(oldPwd)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        if (request.getAttribute(SysConf.USER_UID) == null || request.getAttribute(SysConf.TOKEN) == null) {
            log.error("[updateUserPwd] 用户令已过期");
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        User user = userService.getById(userUid);
        // 判断是否是第三方登录的账号
        if (!user.getSource().equals(SysConf.MOGU)) {
            log.error("[updateUserPwd] 第三方登录的用户无法修改密码");
            return ResultUtil.errorWithMessage(MessageConf.CANNOT_CHANGE_THE_PASSWORD_BY_USER);
        }
        // 判断旧密码是否一致
        if (user.getPassWord().equals(MD5Utils.string2MD5(oldPwd))) {
            user.setPassWord(MD5Utils.string2MD5(newPwd));
            user.updateById();
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
        }
        return ResultUtil.errorWithMessage(MessageConf.PASSWORD_IS_ERROR);
    }

    @AvoidRepeatableCommit
    @ApiOperation(value = "申请友链", notes = "申请友链")
    @PostMapping("/replyBlogLink")
    public String replyBlogLink(HttpServletRequest request, @RequestBody LinkVO linkVO) {
        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        User user = userService.getById(userUid);
        // 判断该用户是否被禁言，被禁言的用户，也无法进行友链申请操作
        if (user != null && user.getCommentStatus() == SysConf.ZERO) {
            log.error("[replyBlogLink] 用户没有操作权限, userUid: {}", RequestHolder.getUserUid());
            return ResultUtil.errorWithMessage(MessageConf.YOU_DONT_HAVE_PERMISSION_TO_REPLY);
        }

        // 判断是否开启邮件通知
        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
            if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                log.info("[replyBlogLink] 发送友链申请邮件通知");
                String feedback = "收到新的友链申请: " + "<br />"
                        + "名称：" + linkVO.getTitle() + "<br />"
                        + "简介：" + linkVO.getSummary() + "<br />"
                        + "地址：" + linkVO.getUrl();
                rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), feedback);
            } else {
                log.error("[replyBlogLink] 网站没有配置通知接收的邮箱地址！");
            }
        }

        QueryWrapper<Link> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.TITLE, linkVO.getTitle());
        queryWrapper.last(SysConf.LIMIT_ONE);
        Link existLink = linkService.getOne(queryWrapper);

        if (existLink != null) {
            Integer linkStatus = existLink.getLinkStatus();
            String message = "";
            switch (linkStatus) {
                case 0: {
                    message = MessageConf.BLOG_LINK_IS_EXIST;
                }
                break;
                case 1: {
                    message = MessageConf.BLOG_LINK_IS_PUBLISH;
                }
                break;
                case 2: {
                    message = MessageConf.BLOG_LINK_IS_NO_PUBLISH;
                }
                break;
            }
            return ResultUtil.errorWithMessage(message);
        }

        Link link = new Link();
        link.setLinkStatus(ELinkStatus.APPLY);
        link.setTitle(linkVO.getTitle());
        link.setSummary(linkVO.getSummary());
        link.setUrl(linkVO.getUrl());
        link.setClickCount(0);
        link.setSort(0);
        link.setFileUid(linkVO.getFileUid());
        link.setEmail(linkVO.getEmail());
        link.setStatus(EStatus.ENABLE);
        link.setUserUid(userUid);
        link.insert();
        //发送后台站内信通知
        asyncService.executeAsyncBusinessBlackNotice(false, userUid, link.getUid(), EBusinessType.FRIENDLY_LINK.getCode(), link.getTitle());
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);

    }

    @ApiOperation(value = "获取用户反馈", notes = "获取用户反馈")
    @GetMapping("/getFeedbackList")
    public String getFeedbackList(HttpServletRequest request) {
        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        Page<Feedback> page = new Page<>();
        page.setSize(20);
        page.setCurrent(1);
        IPage<Feedback> pageList = feedbackService.page(page, queryWrapper);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @AvoidRepeatableCommit
    @ApiOperation(value = "提交反馈", notes = "提交反馈", response = String.class)
    @PostMapping("/addFeedback")
    public String addFeedback(HttpServletRequest request, @Validated({Insert.class}) @RequestBody FeedbackVO feedbackVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);

        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }

        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        User user = userService.getById(userUid);

        // 判断该用户是否被禁言，被禁言的用户，也无法进行反馈操作
        if (user != null && user.getCommentStatus() == SysConf.ZERO) {
            log.error("[addFeedback] 用户没有操作权限, userUid: {}", RequestHolder.getUserUid());
            return ResultUtil.errorWithMessage(MessageConf.YOU_DONT_HAVE_PERMISSION_TO_FEEDBACK);
        }

        // 判断是否开启邮件通知
        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
            if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                log.info("[addFeedback] 发送反馈邮件通知");
                String feedback = "网站收到新的反馈: " + "<br />"
                        + "标题：" + feedbackVO.getTitle() + "<br />" + "<br />"
                        + "内容" + feedbackVO.getContent();
                rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), feedback);
            } else {
                log.error("[addFeedback] 网站没有配置通知接收的邮箱地址！");
            }
        }

        Feedback feedback = new Feedback();
        feedback.setUserUid(userUid);
        feedback.setTitle(feedbackVO.getTitle());
        feedback.setContent(feedbackVO.getContent());

        // 设置反馈已开启
        feedback.setFeedbackStatus(0);
        feedback.setReply(feedbackVO.getReply());
        feedback.setUpdateTime(new Date());
        feedback.insert();
        //发送后台站内信通知
        asyncService.executeAsyncBusinessBlackNotice(false, userUid, feedback.getUid(), EBusinessType.FEED_BACK.getCode(), feedback.getTitle());
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @ApiOperation(value = "绑定用户邮箱", notes = "绑定用户邮箱")
    @GetMapping("/bindUserEmail/{token}/{code}")
    public String bindUserEmail(@PathVariable("token") String token, @PathVariable("code") String code) {

        String userInfo = stringRedisTemplate.opsForValue().get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        if (StringUtils.isEmpty(userInfo)) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        User user = JsonUtils.jsonToPojo(userInfo, User.class);
        if (user != null) {
            user.updateById();
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        } else {
            log.error("[bindUserEmail] 更新失败");
            return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
        }
    }


    @ApiOperation(value = "微信用户小程序登录回调接口", notes = "微信用户登录回调接口")
    @PostMapping(value = "/wechatUserLogin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    public String wechatUserLogin(@RequestParam(value = "openid") String openid, @RequestParam(value = "content") String content, @RequestParam(value = "sign") String sign) {

        // 1、校验content的合法性
        if (content.length() != 6 || !redisUtil.hasKey("ticket:" + content)) {
            log.error("[wechatUserLogin] 验证码过期或不正确, openId: {}", openid);
            return "验证码过期或不正确";
        }
        if (openid == null) {
            log.error("[wechatUserLogin] 不合法注册条件, openId: {}", openid);
            ResultUtil.errorWithMessage("不合法注册条件");
        }

        // 签名验证
        Map<String, Object> data = new HashMap<>();
        data.put("openid", openid);
        data.put("content", content);
        data.put("sign", sign);
        if (!SignUtils.isSignEquals(data)) {
            log.error("[wechatUserLogin] 签名校验失败, openId: {}", openid);
            return "签名校验失败";
        }

        AuthUser authUser = new AuthUser();
        authUser.setUuid(openid);
        authUser.setGender(AuthUserGender.UNKNOWN);
        String userName = String.format("%s-%s", "WECHAT", RandomUtil.randomString(6));
        authUser.setNickname(userName);
        authUser.setUsername(userName);
        authUser.setSource("WECHAT");

        User user;
        Boolean isBind = false;
        if (content.toUpperCase().indexOf("DL") == 0) {
            user = loginManager.login("WECHAT", authUser);
            isBind = false;
        } else if (content.toUpperCase().indexOf("BD") == 0) {
            String userUid = redisUtil.get("ticket:" + content);
            user = loginManager.bind("WECHAT", authUser, userUid);
            isBind = true;
        } else {
            log.error("[wechatUserLogin] 输入验证码有误, openId: {}", openid);
            return "输入验证码有误";
        }

        HttpServletRequest request = RequestHolder.getRequest();
        String accessToken = "";
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), null));
            //执行登录
            SecurityContextHolder.getContext().setAuthentication(auth);
            //获取登录信息
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String ip = IpUtils.getIpAddr(request);
            String jwtToken = jwtTokenUtil.createJwt(
                    securityUser.getUid(),
                    securityUser.getUsername(),
                    ip,
                    UserType.USER,
                    audience.getClientId(),
                    audience.getName(),
                    audience.getExpiresSecond() * 1000,
                    audience.getBase64Secret());
            accessToken = tokenHead + jwtToken;
            // 将新的Token存入Redis中
            redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.USER, accessToken), JsonUtils.objectToJson(securityUser), audience.getExpiresSecond(), TimeUnit.SECONDS);
            Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
            result.put(SysConf.TOKEN, accessToken);

            if (user != null) {
                // 过滤密码
                user.setPassWord("");
                //将从数据库查询的数据缓存到redis中
                stringRedisTemplate.opsForValue().set(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
                String url = webSiteUrl + "?token=" + accessToken;
                // 3、信息保存到redis中，用于用户登录成功
                redisUtil.setEx("Info:" + content, accessToken, 5 * 60, TimeUnit.SECONDS);
                if (isBind) {
                    return "账号绑定成功❤！(若页面长时间未跳转请刷新验证码)\n\n" + "<a href='" + url + "'>点击这里可打开移动端~</a>";
                } else {
                    return "网站登录成功❤！(若页面长时间未跳转请刷新验证码)\n\n" + "<a href='" + url + "'>点击这里可打开移动端~</a>";
                }
            }
        } catch (AuthenticationException e) {
            if (isBind) {
                log.error("[wechatUserLogin] 账号绑定异常，请联系管理员, openId: {}", openid);
                return "账号绑定异常，请联系管理员❤";
            } else {
                log.error("[wechatUserLogin] 网站登录异常，请联系管理员, openId: {}", openid);
                return "网站登录异常，请联系管理员❤";
            }
        }
        if (isBind) {
            log.error("[wechatUserLogin] 账号绑定异常，请联系管理员, openId: {}", openid);
            return "账号绑定异常，请联系管理员❤";
        } else {
            log.error("[wechatUserLogin] 网站登录异常，请联系管理员, openId: {}", openid);
            return "网站登录异常，请联系管理员❤";
        }
    }

    /**
     *
     * @param operateType 操作类型：login: 登录，bind: 绑定
     * 参考接入文档：https://open.pay.yungouos.com/#/api/api/wx/getWebLogin
     * @return
     */
    @ApiOperation(value = "获取微信登录链接", notes = "获取微信登录链接")
    @PostMapping(value="/getWeChatLoginUrl")
    public String getWeChatLoginUrl(@RequestParam(value = "operateType") String operateType) {
        // 如果没有传递类型，默认是登录
        if (StringUtils.isEmpty(operateType)) {
            operateType = SysConf.LOGIN;
        }
        // 判断是登录操作还是绑定操作，用于生成不同的Code
        // 把Code携带到生成微信链接处，在登录成功后会带上，用来标识身份
        String codePrefix = "DL";
        if (SysConf.BIND.equals(operateType)) {
            codePrefix = "BD";
        }
        // 判断该票券是否存在，如果存在重复生成
        String code = codePrefix + RandomUtil.randomNumbers(4);
        while (redisUtil.hasKey("ticket:" + code)) {
            code = codePrefix + RandomUtil.randomNumbers(4);
        }
        // 调用下游服务，获取微信登录链接
        String loginUrlResult = payFeignClient.getWeChatLoginUrl(code);
        WechatWebLoginData wechatWebLoginData = WebUtils.getData(loginUrlResult, WechatWebLoginData.class);
        if (wechatWebLoginData == null) {
            return ResultUtil.errorWithMessage("获取登录链接失败");
        }

        // 获取到票券
        String ticket = RandomUtil.randomString(32);
        String userUid = RequestHolder.getUserUid();
        // 如果是绑定动作，需要获取到用户登录态
        if (SysConf.BIND.equals(operateType)) {
            // 如果没有获取到用户登录态，直接报错
            if (StringUtils.isEmpty(userUid)) {
                return ResultUtil.errorWithMessage("用户暂未登录，无法进行账号绑定");
            }
            // 把票券换成用户ID
            ticket = userUid;
        }

        // 5分钟后过期
        redisUtil.setEx("ticket:" + code, ticket, 5 * 60, TimeUnit.SECONDS);
        // 构建返回参数
        Map<String, String> result = new HashMap<>();
        result.put("loginKey", code);
        result.put("ticket", ticket);
        result.put("appId", wechatWebLoginData.getAppId());
        result.put("scope", wechatWebLoginData.getScope());
        result.put("state", wechatWebLoginData.getState());
        result.put("redirect_uri", wechatWebLoginData.getRedirect_uri());
        return ResultUtil.successWithData(result);
    }

    /**
     * 微信登录回调函数
     * 参考接入文档：https://open.pay.yungouos.com/#/api/api/wx/getOauthInfo
     * @param code
     * @return
     */
    @ApiOperation(value = "微信登录回调函数", notes = "微信登录回调函数")
    @GetMapping(value = "/wechatLoginCallback")
    public String wechatLoginCallback(@RequestParam(value = "code") String code) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (StringUtils.isEmpty(code)) {
            log.error("[wechatUserLogin] 不合法注册条件, code: {}", code);
            ResultUtil.errorWithMessage("不合法注册条件");
        }
        // 获取用户信息
        String oauthInfoResult = payFeignClient.getWechatOauthInfo(code);
        WechatOauthInfoData wechatOauthInfoData = WebUtils.getData(oauthInfoResult, WechatOauthInfoData.class);
        if (wechatOauthInfoData == null) {
            log.error("[wechatLoginCallback] 获取用户信息失败, code: {}", code);
            return ResultUtil.errorWithMessage("获取用户信息失败");
        }

        String openid = wechatOauthInfoData.getOpenId();
        JSONObject paramsJson = wechatOauthInfoData.getParams();
        // 获取用户携带的自定义Code参数【根据该Code可以判断是登录还是绑定动作】
        String content = Convert.toStr(paramsJson.getString(SysConf.STATE), "");
        // 来源：H5还是PC
        String source = Convert.toStr(paramsJson.getString(SysConf.SOURCE), "");

        // 构建默认的用户
        AuthUser authUser = new AuthUser();
        authUser.setUuid(openid);
        authUser.setGender(AuthUserGender.UNKNOWN);
        String userName = String.format("%s-%s", ELoginType.PERSON_WECHAT.getName(), RandomUtil.randomString(6));
        authUser.setNickname(userName);
        authUser.setUsername(userName);
        authUser.setSource(ELoginType.PERSON_WECHAT.getName());
        // 获取用户信息
        WechatOauthInfo wechatOauthInfo = wechatOauthInfoData.getWxUserInfo();
        if (wechatOauthInfo != null) {
            authUser.setAvatar(wechatOauthInfo.getHeadimgurl());
            authUser.setNickname(wechatOauthInfo.getNickname());
        }

        User user;
        boolean isBind = false;
        // 根据Code判断对应的用户行为
        if (content.toUpperCase().indexOf("DL") == 0) {
            user = loginManager.login(ELoginType.PERSON_WECHAT.getName(), authUser);
        } else if (content.toUpperCase().indexOf("BD") == 0) {
            String userUid = redisUtil.get("ticket:" + content);
            user = loginManager.bind(ELoginType.PERSON_WECHAT.getName(), authUser, userUid);
            isBind = true;
        } else {
            log.error("[wechatUserLogin] 登录信息有误, openId: {}", openid);
            return "登录信息有误";
        }

        String accessToken;
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), null));
            //执行登录
            SecurityContextHolder.getContext().setAuthentication(auth);
            //获取登录信息
            SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String ip = IpUtils.getIpAddr(request);
            String jwtToken = jwtTokenUtil.createJwt(
                    securityUser.getUid(),
                    securityUser.getUsername(),
                    ip,
                    UserType.USER,
                    audience.getClientId(),
                    audience.getName(),
                    audience.getExpiresSecond() * 1000,
                    audience.getBase64Secret());
            accessToken = tokenHead + jwtToken;
            // 将新的Token存入Redis中
            redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.USER, accessToken), JsonUtils.objectToJson(securityUser), audience.getExpiresSecond(), TimeUnit.SECONDS);
            Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
            result.put(SysConf.TOKEN, accessToken);
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中
            stringRedisTemplate.opsForValue().set(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
            String url = webSiteUrl + "?token=" + accessToken;
            // 3、信息保存到redis中，用于用户登录成功
            redisUtil.setEx("Info:" + content, accessToken, 5 * 60, TimeUnit.SECONDS);

            if ("h5".equals(source)) {
                HttpServletResponse resp = RequestHolder.getResponse();
                assert resp != null;
                url = h5WebSiteUrl + "?token=" + accessToken;
                resp.sendRedirect(url);
                return null;
            }
            if (isBind) {
                return "账号绑定成功❤！(若页面长时间未跳转请刷新验证码重新登录)\n\n" + "<a href='" + url + "'>点击这里可以手动跳转~</a>";
            } else {
                return "网站登录成功❤！(若页面长时间未跳转请刷新验证码重新登录)\n\n" + "<a href='" + url + "'>点击这里可以手动跳转~</a>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isBind) {
                log.error("[wechatUserLogin] 账号绑定异常，请联系管理员, openId: {}", openid);
                return "账号绑定异常，请联系管理员❤";
            } else {
                log.error("[wechatUserLogin] 网站登录异常，请联系管理员, openId: {}", openid);
                return "网站登录异常，请联系管理员❤";
            }
        }
    }


    @ApiOperation(value = "小程序登录", notes = "小程序登录")
    @PostMapping("/appLogin")
    public String appLogin(@RequestBody LoginRequest loginRequest) {
        // 获取小程序TOKEN
        SecretConfig secretConfig = getSecretConfig(SysConf.MINI);
        // 根据小程序TicketCode，换取用户信息
        JSONObject jsonObject = WechatUtils.getSessionKeyOrOpenId(secretConfig.getBizId(), secretConfig.getBizSecret(), loginRequest.getTicketCode());
        if (jsonObject.getJSONObject("error_code") != null) {
            throw new QueryException("登录异常");
        }
        String openID = jsonObject.getString("openid");
        if (StringUtils.isEmpty(openID)) {
            throw new QueryException("登录异常");
        }
        AuthUser authUser = new AuthUser();
        authUser.setUuid(openID);
        authUser.setGender(AuthUserGender.UNKNOWN);
        String userName = String.format("%s-%s", "MINI", RandomUtil.randomString(6));
        authUser.setNickname(userName);
        authUser.setUsername(userName);
        authUser.setSource(ELoginType.MINI.getName());

        // 根据OpenID进行登录
        User user = loginManager.login("MINI", authUser);
        // 获取Token
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), null));
        //执行登录
        SecurityContextHolder.getContext().setAuthentication(auth);
        //获取登录信息
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String ip = IpUtils.getIpAddr(RequestHolder.getRequest());
        String jwtToken = jwtTokenUtil.createJwt(
                securityUser.getUid(),
                securityUser.getUsername(),
                ip,
                UserType.USER,
                audience.getClientId(),
                audience.getName(),
                audience.getExpiresSecond() * 1000,
                audience.getBase64Secret());
        String accessToken = tokenHead + jwtToken;
        // 将新的Token存入Redis中
        redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.USER, accessToken), JsonUtils.objectToJson(securityUser), audience.getExpiresSecond(), TimeUnit.SECONDS);
        Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
        result.put(SysConf.TOKEN, accessToken);
        result.put(SysConf.IS_NEW_USER, user.getIsNewUser());
        // 过滤密码
        user.setPassWord("");
        //将从数据库查询的数据缓存到redis中
        redisUtil.setEx(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);

        return ResultUtil.successWithData(result);
    }


    /**
     * getUserInfoByBindCode
     * @param bindRequest
     * @return
     */
    @AvoidRepeatableCommit(timeout = 1000)
    @ApiOperation(value = "根据绑定码获取用户信息", notes = "根据绑定码获取用户信息")
    @PostMapping(value = "/getUserInfoByBindCode")
    public String getUserInfoByBindCode(@RequestBody BindRequest bindRequest) {
        if (StringUtils.isEmpty(bindRequest.getBindCode())) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        String bindCode = bindRequest.getBindCode();
        // 1、校验content的合法性
        if (bindCode.length() != 6 || !redisUtil.hasKey("ticket:" + bindCode)) {
            log.error("[bindCode] 验证码过期或不正确, bindCode: {}", bindCode);
            return ResultUtil.errorWithMessage( "验证码过期或不正确");
        }
        String userUid = redisUtil.get("ticket:" + bindCode);
        if (StringUtils.isEmpty(userUid)) {
            log.error("[bindCode] 验证码过期或不正确, bindCode: {}", bindCode);
            return ResultUtil.errorWithMessage( "验证码过期或不正确");
        }
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.errorWithMessage( "用户不存在");
        }
        user = userService.setUserAvatar(user);
        user = userService.convertUser(user);
        return ResultUtil.successWithData(user);
    }

    /**
     * 完成小程序Code绑定
     * @param bindRequest
     * @return
     */
    @AvoidRepeatableCommit(timeout = 1000)
    @ApiOperation(value = "绑定Code", notes = "绑定Code")
    @PostMapping(value = "/bindCode")
    public String bindCode(@RequestBody BindRequest bindRequest) {
        if (StringUtils.isEmpty(bindRequest.getBindCode()) || StringUtils.isEmpty(bindRequest.getTicketCode())) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        // 获取小程序TOKEN
        SecretConfig secretConfig = getSecretConfig(SysConf.MINI);
        String bindCode = bindRequest.getBindCode();
        // 根据小程序TicketCode，换取用户信息
        JSONObject jsonObject = WechatUtils.getSessionKeyOrOpenId(secretConfig.getBizId(), secretConfig.getBizSecret(), bindRequest.getTicketCode());
        if (jsonObject.getJSONObject("error_code") != null) {
            throw new QueryException("登录异常");
        }
        String openID = jsonObject.getString("openid");
        if (StringUtils.isEmpty(openID)) {
            throw new QueryException("获取登录态异常");
        }

        // 1、校验content的合法性
        if (bindCode.length() != 6 || !redisUtil.hasKey("ticket:" + bindCode)) {
            log.error("[bindCode] 验证码过期或不正确, bindCode: {}", bindCode);
            return ResultUtil.errorWithMessage( "验证码过期或不正确，请重新输入或更新验证码");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUuid(openID);
        authUser.setGender(AuthUserGender.UNKNOWN);
        String userName = String.format("%s-%s", "MINI", RandomUtil.randomString(6));
        authUser.setNickname(userName);
        authUser.setUsername(userName);
        authUser.setSource(ELoginType.MINI.getName());
        String userUid = redisUtil.get("ticket:" + bindCode);
        loginManager.bind("MINI", authUser, userUid);
        // 绑定完成后，删除验证码
        redisUtil.delete("ticket:" + bindCode);

        return ResultUtil.successWithMessage("账号绑定成功，请退出后重新登录");
    }

    @ApiOperation(value = "通过openID获取用户信息", notes = "通过openID获取用户信息")
    @PostMapping(value = "/getUserByOpenID", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = "application/json;charset=UTF-8")
    public String getUserByOpenID(@RequestParam(value = "openid") String openid) {
        return ResultUtil.successWithData(loginManager.getUserByOpenID(openid));
    }

    @ApiOperation(value = "获取小程序绑定链接", notes = "获取小程序绑定链接")
    @PostMapping(value = "/getMiniBindUrl")
    public String getMiniBindUrl() {
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isEmpty(userUid)) {
            return ResultUtil.errorWithMessage("用户未登录");
        }
        // 判断该票券是否存在
        String code = "BD" + RandomUtil.randomNumbers(4);
        while (redisUtil.hasKey("ticket:" + code)) {
            code = "BD" + RandomUtil.randomNumbers(4);
        }
        SecretConfig secretConfig = getSecretConfig(SysConf.MINI);

        String accessToken = WechatUtils.getAccessToken(secretConfig.getBizId(), secretConfig.getBizSecret());
        if (StringUtils.isEmpty(accessToken)) {
            return ResultUtil.errorWithMessage("请求后台接口出错");
        }
        String url = WechatUtils.generateUrlLink(accessToken, "pages/index/index", code);
        // 5分钟后过期
        redisUtil.setEx("ticket:" + code, userUid, 5 * 60, TimeUnit.SECONDS);
        return ResultUtil.successWithData(url);
    }

    @AvoidRepeatableCommit
    @ApiOperation(value = "获取第三方账号绑定列表", notes = "获取第三方账号绑定列表", response = String.class)
    @PostMapping("/getAccountBindList")
    public String getAccountBindList() {
        return ResultUtil.successWithData(loginManager.getUserAccountList());
    }

    /**
     * 鉴权
     *
     * @param source
     * @return
     */
    private AuthRequest getAuthRequest(String source) {
        AuthRequest authRequest = null;
        SecretConfig secretConfig =  getSecretConfig(source);
        switch (source) {
            case SysConf.GITHUB:
                authRequest = new AuthGithubRequest(AuthConfig.builder()
                        .clientId(secretConfig.getBizId())
                        .clientSecret(secretConfig.getBizSecret())
                        .redirectUri(secretConfig.getRequestUrl())
                        .build());
                break;
            case SysConf.GITEE:
                authRequest = new AuthGiteeRequest(AuthConfig.builder()
                        .clientId(secretConfig.getBizId())
                        .clientSecret(secretConfig.getBizSecret())
                        .redirectUri(secretConfig.getRequestUrl())
                        .build());
                break;
            case SysConf.QQ:
                authRequest = new AuthQqRequest(AuthConfig.builder()
                        .clientId(secretConfig.getBizId())
                        .clientSecret(secretConfig.getBizSecret())
                        .redirectUri(secretConfig.getRequestUrl())
                        .build());
                break;
            default:
                break;
        }
        if (null == authRequest) {
            throw new AuthException(MessageConf.OPERATION_FAIL);
        }
        return authRequest;
    }

    private SecretConfig getSecretConfig(String source) {
        SecretConfig secretConfig = null;
        switch (source) {
            case SysConf.GITEE: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.GITEE.getName());
            } break;
            case SysConf.GITHUB: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.GITHUB.getName());
            } break;

            case SysConf.QQ: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.QQ.getName());
            } break;
            case SysConf.WECHAT: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.WECHAT.getName());
            } break;
            case SysConf.MINI: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.MINI.getName());
            } break;
            case SysConf.PERSON_WECHAT: {
                secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_LOGIN.getType(), ELoginType.PERSON_WECHAT.getName());
            } break;
        }
        if (secretConfig == null) {
            log.error("[getSecretConfig] 未查询到第三方登录相关配置，请到后台密钥管理->第三方登录添加相关密钥");
            throw new AuthException("未查询到第三方登录相关配置，请联系管理员");
        }
        return secretConfig;
    }
}
