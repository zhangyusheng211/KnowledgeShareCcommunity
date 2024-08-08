package com.moxi.mogublog.web.restapi;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meetc.mogublog.security.SecurityUser;
import com.meetc.mogublog.security.exception.UserRoleNotFoundException;
import com.meetc.mogublog.security.jwt.Audience;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import com.meetc.mogublog.security.jwt.UserType;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.LoginRequest;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.manager.UserManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.EOpenStatus;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.EUserLevel;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录RestApi，系统自带的登录注册功能
 * 第三方登录请移步AuthRestApi
 *
 * @author 陌溪
 * @date 2020年5月6日17:50:23
 */
@RestController
@RefreshScope
@RequestMapping("/auth")
@Api(value = "登录管理相关接口", tags = {"登录管理相关接口"})
@Slf4j
public class LoginRestApi {

    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private WebConfigService webConfigService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SystemConfigService systemConfigService;
    @Resource
    private SysParamsService sysParamsService;

    @Resource
    AuthenticationManager authenticationManager;

    @Resource
    private Audience audience;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Value(value = "${tokenHead}")
    private String tokenHead;
    @Autowired
    private AdminService adminService;


    /**
     * 网站
     */
    @Value(value = "${data.webSite.url}")
    private String WEB_SITE;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名或邮箱或手机号"),
            @ApiImplicitParam(name = "passWord", value = "密码"),
            @ApiImplicitParam(name = "client", value = "服务端"),
            @ApiImplicitParam(name = "isRememberMe", value = "是否记住账号密码"),

    })
    public String login(HttpServletRequest request, @RequestBody UserVO userVO) {

        Boolean isOpenLoginType = webConfigService.isOpenLoginType(RedisConf.PASSWORD);
        if (!isOpenLoginType) {
            return ResultUtil.errorWithMessage("后台未开启该登录方式!");
        }
        String ip = IpUtils.getIpAddr(request);
        String limitCount = redisUtil.get(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip);
        if (StringUtils.isNotEmpty(limitCount)) {
            Integer tempLimitCount = Integer.valueOf(limitCount);
            if (tempLimitCount >= Constants.NUM_FIVE) {
                return ResultUtil.errorWithMessage("密码输错次数过多,已被锁定30分钟");
            }
        }
        if (StringUtils.isEmpty(userVO.getUserName()) || StringUtils.isEmpty(userVO.getPassWord())) {
            return ResultUtil.errorWithMessage("账号或密码不能为空");
        }
        String resultInfo = ResultUtil.errorWithMessage("未知异常");
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userVO.getUserName(), userVO.getPassWord()));
            //执行登录
            SecurityContextHolder.getContext().setAuthentication(auth);
            //获取登录信息
            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String jwtToken = jwtTokenUtil.createJwt(
                    user.getUid(),
                    user.getUsername(),
                    ip,
                    UserType.USER,
                    audience.getClientId(),
                    audience.getName(),
                    audience.getExpiresSecond() * 1000,
                    audience.getBase64Secret());
            String token = tokenHead + jwtToken;
            // 将新的Token存入Redis中
            redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.USER, token), JsonUtils.objectToJson(user), audience.getExpiresSecond(), TimeUnit.SECONDS);
            Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
            result.put(SysConf.TOKEN, token);

            // 获取用户信息
            User loginUser = userService.getById(user.getUid());
            if (loginUser != null && loginUser.getStatus() == EStatus.ENABLE) {
                // 过滤密码
                loginUser.setPassWord("");
                userService.setUserAvatar(loginUser);
                // 获取用户等级
                EUserLevel userLevel = EUserLevel.getLvByExpValue(loginUser.getExpValue());
                loginUser.setUserLevel(userLevel.getLevel());

                //将从数据库查询的数据缓存到redis中
                redisUtil.setEx(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token, JsonUtils.objectToJson(loginUser), audience.getExpiresSecond(), TimeUnit.SECONDS);
                // 存储用户uid到token的关联关系
                redisUtil.setEx(RedisConf.USER_UID_TO_TOKEN + Constants.SYMBOL_COLON + loginUser.getUid(), JsonUtils.objectToJson(token), audience.getExpiresSecond(), TimeUnit.SECONDS);
                resultInfo = ResultUtil.result(SysConf.SUCCESS, result);
            } else {
                resultInfo = ResultUtil.errorWithMessage("用户不存在或未激活");
            }
        } catch (UsernameNotFoundException e) {
            resultInfo = ResultUtil.errorWithMessage(e.getMessage());
        } catch (UserRoleNotFoundException e) {
            resultInfo = ResultUtil.errorWithMessage(e.getMessage());
        } catch (BadCredentialsException e) {
            //出现密码错误异常时 将redis中错误次数+1
            resultInfo = ResultUtil.errorWithMessage(String.format(MessageConf.LOGIN_ERROR, setLoginCommit(request)));
        } finally {
            return resultInfo;
        }
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public String register(@Validated({Insert.class}) @RequestBody UserVO userVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        // 判断是否开启登录方式
        Boolean isOpenLoginType = webConfigService.isOpenLoginType(RedisConf.PASSWORD);
        if (!isOpenLoginType) {
            return ResultUtil.errorWithMessage("后台未开启注册功能!");
        }
        if (userVO.getUserName().length() < Constants.NUM_FIVE || userVO.getUserName().length() >= Constants.NUM_TWENTY || userVO.getPassWord().length() < Constants.NUM_FIVE || userVO.getPassWord().length() >= Constants.NUM_TWENTY) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_NAME, userVO.getUserName()).or().eq(SQLConf.EMAIL, userVO.getEmail()));
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        User user = userService.getOne(queryWrapper);
        if (user != null) {
            return ResultUtil.errorWithMessage(MessageConf.USER_OR_EMAIL_EXIST);
        }
        user = new User();
        user.setUserName(userVO.getUserName());
        user.setNickName(userVO.getNickName());
        user.setPassWord(new BCryptPasswordEncoder().encode(userVO.getPassWord()));
        user.setEmail(userVO.getEmail());
        // 设置账号来源，蘑菇博客
        user.setSource(SysConf.MOGU);
        user.setLastLoginIp(ip);
        user.setBrowser(map.get(SysConf.BROWSER));
        user.setOs(map.get(SysConf.OS));

        // 获取注册用户默认权限
        String defaultUserAuth = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_USER_AUTH);
        user.setAuthCodeList(defaultUserAuth);

        // 设置用户默认头像
        String defaultAvatarStr = sysParamsService.getSysParamsValueByKey(SysConf.USER_DEFAULT_AVATAR);
        List<String> defaultAvatarList = StringUtils.changeStringToString(defaultAvatarStr, Constants.SYMBOL_COMMA);
        // 随机一个头像
        if (defaultAvatarList.size() > 1) {
            Integer index = RandomUtil.randomInt(defaultAvatarList.size() - 1);
            user.setAvatar(defaultAvatarList.get(index));
            // 获取图片信息
            String defaultPictureList = this.pictureFeignClient.getPicture(user.getAvatar(), com.moxi.mogublog.xo.global.SysConf.FILE_SEGMENTATION);
            List<String> defaultPhotoList = webUtil.getPicture(defaultPictureList);
            if (defaultPhotoList.size() > 0) {
                user.setPhotoUrl(defaultPhotoList.get(0));
            }
        }

        // 判断是否开启用户邮件激活状态
        SystemConfig systemConfig = systemConfigService.getConfig();
        String openEmailActivate = systemConfig.getOpenEmailActivate();
        String resultMessage = "注册成功";
        if (EOpenStatus.OPEN.equals(openEmailActivate)) {
            user.setStatus(EStatus.FREEZE);
        } else {
            // 未开启注册用户邮件激活，直接设置成激活状态
            user.setStatus(EStatus.ENABLE);
        }
        user.insert();

        // 判断是否需要发送邮件通知
        if (EOpenStatus.OPEN.equals(openEmailActivate)) {
            // 生成随机激活的token
            String token = StringUtils.getUUID();
            // 过滤密码
            user.setPassWord("");
            //将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
            redisUtil.setEx(RedisConf.ACTIVATE_USER + RedisConf.SEGMENTATION + token, JsonUtils.objectToJson(user), 1, TimeUnit.HOURS);
            // 发送邮件，进行账号激活
            rabbitMqUtil.sendActivateEmail(user, token);
            resultMessage = "注册成功，请登录邮箱进行账号激活";
        }
        return ResultUtil.result(SysConf.SUCCESS, resultMessage);
    }

    @ApiOperation(value = "激活用户账号", notes = "激活用户账号")
    @GetMapping(value = "/activeUser/{token}", produces = "text/html;charset=utf-8")
    public void bindUserEmail(@PathVariable("token") String token) throws IOException {
        HttpServletResponse response = RequestHolder.getResponse();
        // 从redis中获取用户信息
        String userInfo = redisUtil.get(RedisConf.ACTIVATE_USER + RedisConf.SEGMENTATION + token);
        if (StringUtils.isEmpty(userInfo)) {
            response.sendRedirect( WEB_SITE + "error?msg=" + StringUtils.encode(MessageConf.INVALID_TOKEN));
            return;
        }
        User user = JsonUtils.jsonToPojo(userInfo, User.class);
        if (EStatus.FREEZE != user.getStatus()) {
            response.sendRedirect(WEB_SITE + "error?msg=" + StringUtils.encode("用户账号已经被激活"));
            return;
        }
        user.setStatus(EStatus.ENABLE);
        user.updateById();

        // 更新成功后，需要把该用户名下其它未激活的用户删除【删除】
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_NAME, user.getUserName());
        queryWrapper.ne(SQLConf.UID, user.getUid());
        queryWrapper.ne(SQLConf.STATUS, EStatus.ENABLE);
        List<User> userList = userService.list(queryWrapper);
        if (userList.size() > 0) {
            List<String> uidList = new ArrayList<>();
            userList.forEach(item -> {
                uidList.add(item.getUid());
            });
            // 移除所有未激活的用户【该用户名下的】
            userService.removeByIds(uidList);
        }
        response.sendRedirect(WEB_SITE + "success?msg=" + StringUtils.encode("激活成功"));
    }

    @ApiOperation(value = "退出登录", notes = "退出登录", response = String.class)
    @PostMapping(value = "/logout", produces = "text/html;charset=utf-8")
    public String logout(@ApiParam(name = "token", value = "token令牌", required = false) @RequestParam(name = "token", required = false) String token) {
        if (StringUtils.isEmpty(token)) {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
        String userJson = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        if (StringUtils.isEmpty(userJson)) {
            return ResultUtil.result(SysConf.SUCCESS, "退出成功");
        }
        redisUtil.delete(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + token);
        User user = JsonUtils.jsonToPojo(userJson, User.class);
        if (user == null) {
            return ResultUtil.result(SysConf.SUCCESS, "退出成功");
        }
        // 删除userUid和token的关系
        redisUtil.delete(RedisConf.USER_UID_TO_TOKEN + Constants.SYMBOL_COLON + user.getUid());
        return ResultUtil.result(SysConf.SUCCESS, "退出成功");
    }


    /**
     * 设置登录限制，返回剩余次数
     * 密码错误五次，将会锁定30分钟
     *
     * @param request
     */
    private Integer setLoginCommit(HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        String count = redisUtil.get(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip);
        Integer surplusCount = 5;
        if (StringUtils.isNotEmpty(count)) {
            Integer countTemp = Integer.valueOf(count) + 1;
            surplusCount = surplusCount - countTemp;
            redisUtil.setEx(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip, String.valueOf(countTemp), 30, TimeUnit.MINUTES);
        } else {
            surplusCount = surplusCount - 1;
            redisUtil.setEx(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip, Constants.STR_ONE, 30, TimeUnit.MINUTES);
        }
        return surplusCount;
    }

    /**
     * 激活管理员密码
     * @param token
     * @return
     */
    @ApiOperation(value = "激活管理员密码", notes = "激活管理员密码")
    @GetMapping("/activePassword/{token}")
    public String activePassword(@PathVariable("token") String token) {
        // 从redis中获取用户信息
        String adminInfo = redisUtil.get(RedisConf.FORGET_PASSWORD + RedisConf.SEGMENTATION + token);
        if (StringUtils.isEmpty(adminInfo)) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        Admin tempAdmin = JsonUtils.jsonToPojo(adminInfo, Admin.class);
        if (tempAdmin != null && StringUtils.isNotEmpty(tempAdmin.getUid())) {
            String adminUid = tempAdmin.getUid();
            Admin admin = adminService.getById(adminUid);
            if (admin != null) {
                admin.setPassWord(tempAdmin.getPassWord());
                admin.updateById();
            }
            return ResultUtil.successWithMessage( MessageConf.UPDATE_SUCCESS);
        }
        return ResultUtil.errorWithMessage(MessageConf.UPDATE_FAIL);
    }
}
