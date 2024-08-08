package com.moxi.mogublog.admin.restapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meetc.mogublog.security.SecurityUser;
import com.meetc.mogublog.security.exception.UserRoleNotFoundException;
import com.meetc.mogublog.security.jwt.Audience;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import com.meetc.mogublog.security.jwt.UserType;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.CategoryMenu;
import com.moxi.mogublog.commons.entity.OnlineAdmin;
import com.moxi.mogublog.commons.entity.Role;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.CategoryMenuService;
import com.moxi.mogublog.xo.service.RoleService;
import com.moxi.mogublog.xo.service.WebConfigService;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mougblog.base.enums.EMenuType;
import com.moxi.mougblog.base.global.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录管理 RestApi【为了更好地使用security放行把登录管理放在AuthRestApi中】
 *
 * @author limbo
 * @date 2018-10-14
 */
@RestController
@RefreshScope
@RequestMapping("/auth")
@Api(value = "登录相关接口", tags = {"登录相关接口"})
@Slf4j
public class LoginRestApi {

    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Resource
    private WebUtil webUtil;
    @Resource
    private AdminService adminService;
    @Resource
    private RoleService roleService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private CategoryMenuService categoryMenuService;
    @Resource
    private Audience audience;
    @Value(value = "${tokenHead}")
    private String tokenHead;
    @Value(value = "${isRememberMeExpiresSecond}")
    private int isRememberMeExpiresSecond;
    @Value(value = "${data.web.logo}")
    private String defaultAvatar;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Resource
    private WebConfigService webConfigService;

    @Resource
    AuthenticationManager authenticationManager;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public String login(HttpServletRequest request,
                        @ApiParam(name = "username", value = "用户名或邮箱或手机号") @RequestParam(name = "username", required = false) String username,
                        @ApiParam(name = "password", value = "密码") @RequestParam(name = "password", required = false) String password,
                        @ApiParam(name = "client", value = "服务端") @RequestParam(name = "client", required = false) String client,
                        @ApiParam(name = "isRememberMe", value = "是否记住账号密码") @RequestParam(name = "isRememberMe", required = false, defaultValue = "false") Boolean isRememberMe) {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResultUtil.errorWithMessage("账号或密码不能为空");
        }
        String ip = IpUtils.getIpAddr(request);
        String limitCount = redisUtil.get(RedisConf.LOGIN_LIMIT + RedisConf.SEGMENTATION + ip);
        if (StringUtils.isNotEmpty(limitCount)) {
            Integer tempLimitCount = Integer.valueOf(limitCount);
            if (tempLimitCount >= Constants.NUM_FIVE) {
                return ResultUtil.errorWithMessage("密码输错次数过多,已被锁定30分钟");
            }
        }
        String resultInfo = ResultUtil.errorWithMessage("未知异常");
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            //执行登录
            SecurityContextHolder.getContext().setAuthentication(auth);
            //获取登录信息
            SecurityUser admin = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            long expiration = isRememberMe ? isRememberMeExpiresSecond : audience.getExpiresSecond();
            String jwtToken = jwtTokenUtil.createJwt(
                    admin.getUid(),
                    admin.getUsername(),
                    ip,
                    UserType.ADMIN,
                    audience.getClientId(),
                    audience.getName(),
                    expiration * 1000,
                    audience.getBase64Secret());
            String token = tokenHead + jwtToken;
            // 将新的Token存入Redis中
            redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, UserType.ADMIN, token), JsonUtils.objectToJson(admin), expiration, TimeUnit.SECONDS);
            Map<String, Object> result = new HashMap<>(Constants.NUM_ONE);
            result.put(SysConf.TOKEN, token);

            //进行登录相关操作
            Admin adminPo = adminService.getById(admin.getUid());
            if (adminPo != null) {
                Integer count = adminPo.getLoginCount() + 1;
                adminPo.setLoginCount(count);
                adminPo.setLastLoginIp(IpUtils.getIpAddr(request));
                adminPo.setLastLoginTime(new Date());
                adminPo.updateById();
                // 设置token到validCode，用于记录登录用户
                adminPo.setValidCode(token);
                // 设置tokenUid，【主要用于换取token令牌，防止token直接暴露到在线用户管理中】
                adminPo.setTokenUid(StringUtils.getUUID());
                // 添加在线用户到Redis中【设置过期时间】
                adminService.addOnlineAdmin(adminPo, expiration);
            }


            resultInfo = ResultUtil.result(SysConf.SUCCESS, result);
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

    @ApiOperation(value = "用户信息", notes = "用户信息", response = String.class)
    @GetMapping(value = "/info")
    public String info(HttpServletRequest request) {

        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
        if (request.getAttribute(SysConf.ADMIN_UID) == null) {
            return ResultUtil.errorWithMessage("token用户过期");
        }
        Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());

        // 更新最后登录时间
        Integer count = admin.getLoginCount() + 1;
        admin.setLoginCount(count);
        admin.setLastLoginTime(new Date());
        admin.setLastLoginIp(IpUtils.getIpAddr(request));
        admin.updateById();

        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), SysConf.FILE_SEGMENTATION);
            List<String> list = webUtil.getPicture(pictureList);
            if (list.size() > 0) {
                map.put(SysConf.AVATAR, list.get(0));
            } else {
                map.put(SysConf.AVATAR, defaultAvatar);
            }
        }

        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        Collection<Role> roleList = roleService.listByIds(roleUid);
        map.put(SysConf.ROLES, roleList);
        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    @ApiOperation(value = "获取当前用户的菜单", notes = "获取当前用户的菜单", response = String.class)
    @GetMapping(value = "/getMenu")
    public String getMenu(HttpServletRequest request) {

        Collection<CategoryMenu> categoryMenuList = new ArrayList<>();
        Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());

        List<String> roleUid = new ArrayList<>();
        roleUid.add(admin.getRoleUid());
        Collection<Role> roleList = roleService.listByIds(roleUid);
        List<String> categoryMenuUids = new ArrayList<>();
        roleList.forEach(item -> {
            String caetgoryMenuUids = item.getCategoryMenuUids();
            String[] uids = caetgoryMenuUids.replace("[", "").replace("]", "").replace("\"", "").split(",");
            categoryMenuUids.addAll(Arrays.asList(uids));
        });
        categoryMenuList = categoryMenuService.listByIds(categoryMenuUids);

        // 从三级级分类中查询出 二级分类
        List<CategoryMenu> buttonList = new ArrayList<>();
        Set<String> secondMenuUidList = new HashSet<>();
        categoryMenuList.forEach(item -> {
            // 查询二级分类
            if (item.getMenuType() == EMenuType.MENU && item.getMenuLevel() == SysConf.TWO) {
                secondMenuUidList.add(item.getUid());
            }
            // 从三级分类中，得到二级分类
            if (item.getMenuType() == EMenuType.BUTTON && StringUtils.isNotEmpty(item.getParentUid())) {
                // 找出二级菜单
                secondMenuUidList.add(item.getParentUid());
                // 找出全部按钮
                buttonList.add(item);
            }
        });

        Collection<CategoryMenu> childCategoryMenuList = new ArrayList<>();
        Collection<CategoryMenu> parentCategoryMenuList = new ArrayList<>();
        List<String> parentCategoryMenuUids = new ArrayList<>();

        if (secondMenuUidList.size() > 0) {
            childCategoryMenuList = categoryMenuService.listByIds(secondMenuUidList);
        }

        childCategoryMenuList.forEach(item -> {
            //选出所有的二级分类
            if (item.getMenuLevel() == SysConf.TWO) {

                if (StringUtils.isNotEmpty(item.getParentUid())) {
                    parentCategoryMenuUids.add(item.getParentUid());
                }
            }
        });

        if (parentCategoryMenuUids.size() > 0) {
            parentCategoryMenuList = categoryMenuService.listByIds(parentCategoryMenuUids);
        }

        List<CategoryMenu> list = new ArrayList<>(parentCategoryMenuList);

        //对parent进行排序
        Map<String, Object> map = new HashMap<>(Constants.NUM_THREE);
        Collections.sort(list);
        map.put(SysConf.PARENT_LIST, list);
        map.put(SysConf.SON_LIST, childCategoryMenuList);
        map.put(SysConf.BUTTON_LIST, buttonList);
        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    @ApiOperation(value = "获取网站名称", notes = "获取网站名称", response = String.class)
    @GetMapping(value = "/getWebSiteInfo")
    public String getWebSiteInfo() {
        return ResultUtil.successWithData(webConfigService.getWebSiteInfo());
    }


    @ApiOperation(value = "退出登录", notes = "退出登录", response = String.class)
    @PostMapping(value = "/logout")
    public String logout() {
        ServletRequestAttributes attribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attribute.getRequest();
        String token = request.getAttribute(SysConf.TOKEN).toString();
        if (StringUtils.isEmpty(token)) {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        } else {
            // 获取在线用户信息

            //解析token的uid
            String uid = "";
            String username = "";
            String userType = "";
            // 私钥
            String base64Secret = audience.getBase64Secret();
            try {
                uid = jwtTokenUtil.getUserUid(token, base64Secret);
                username = jwtTokenUtil.getUsername(token, base64Secret);
                //jwt加密时使用的是枚举 解析出来的userType是枚举常量名
                userType = jwtTokenUtil.getUserType(token, base64Secret);
            } catch (Exception e) {
                log.error("解析token异常: {}", e.getMessage());
            }

            String adminJson = redisUtil.get(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, userType, token));
            if (StringUtils.isNotEmpty(adminJson)) {
                OnlineAdmin onlineAdmin = JsonUtils.jsonToPojo(adminJson, OnlineAdmin.class);
                String tokenUid = onlineAdmin.getTokenId();
                // 移除Redis中的TokenUid
                redisUtil.delete(RedisConf.LOGIN_UUID_KEY + RedisConf.SEGMENTATION + tokenUid);
            }
            // 移除Redis中的用户
            redisUtil.delete(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, userType, token));
            //清除SecurityContextHolder上下文内容
            SecurityContextHolder.clearContext();
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
        }
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
     * 密码找回
     *
     * @param request
     * @param adminVO
     * @return
     */
    @ApiOperation(value = "密码找回", notes = "密码找回")
    @PostMapping("/findbackPassword")
    public String findbackPassword(HttpServletRequest request, @RequestBody AdminVO adminVO) {

        //限制访问次数
        String ip = IpUtils.getIpAddr(request);
        String limitCount = redisUtil.get(RedisConf.FORGET_LIMIT + RedisConf.SEGMENTATION + ip);
        if (StringUtils.isNotEmpty(limitCount)) {
            Integer tempLimitCount = Integer.valueOf(limitCount);
            if (tempLimitCount >= Constants.NUM_FIVE) {
                return ResultUtil.result(com.moxi.mogublog.xo.global.SysConf.ERROR, "找回密码次数过多，请明天再试");
            }
            redisUtil.incrBy(RedisConf.FORGET_LIMIT + RedisConf.SEGMENTATION + ip, 1L);
        } else {
            redisUtil.setEx(RedisConf.FORGET_LIMIT + RedisConf.SEGMENTATION + ip, JsonUtils.objectToJson(1), 24, TimeUnit.HOURS);
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_NAME, adminVO.getUserName());
        queryWrapper.last(SysConf.LIMIT_ONE);
        Admin admin = adminService.getOne(queryWrapper);
        if (admin != null) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            admin.setPassWord(encoder.encode(adminVO.getPassWord()));
            if (StringUtils.isNotEmpty(admin.getEmail())) {
                // 生成随机激活的token
                String token = StringUtils.getUUID();
                //将从数据库查询的数据缓存到redis中，用于用户邮箱激活，1小时后过期
                redisUtil.setEx(RedisConf.FORGET_PASSWORD + RedisConf.SEGMENTATION + token, JsonUtils.objectToJson(admin), 1, TimeUnit.HOURS);
                rabbitMqUtil.sendForgetPasswordEmail(admin, token);
                return ResultUtil.result(SysConf.SUCCESS, "密码重置成功，请登录邮箱进行新密码激活");
            } else {
                return ResultUtil.errorWithMessage("当前用户未注册邮箱，请联系管理员!");
            }
        } else {
            return ResultUtil.errorWithMessage("未查询到该用户!");
        }
    }
}
