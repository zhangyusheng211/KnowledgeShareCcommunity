package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.vo.AdminVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.AdminMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EContributeSource;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 管理员表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-04
 */
@Service
public class AdminServiceImpl extends SuperServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SysParamsService sysParamsService;
    @Autowired
    private WebUtil webUtil;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebVisitService webVisitService;


    @Override
    public List<Admin> getAdminListByUid(Collection<String> uidList) {

        if (uidList.size() == 0) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConf.UID, uidList);
        List<Admin> adminList = adminService.list(queryWrapper);

        final StringBuilder fileUids = new StringBuilder();
        for (Admin admin : adminList) {
            if (StringUtils.isNotEmpty(admin.getAvatar())) {
                fileUids.append(admin.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        }
        //获取图片
        Map<String, String> pictureMap = new HashMap<>(Constants.NUM_TEN);
        String pictureResult = null;
        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });
        List<Admin> result = new ArrayList<>();
        for (Admin tempAdmin : adminList) {
            Admin admin = new Admin();
            // 数据脱敏
            admin.setUid(tempAdmin.getUid());
            admin.setPhotoUrl(tempAdmin.getPhotoUrl());
            admin.setSummary(tempAdmin.getSummary());
            admin.setNickName(tempAdmin.getNickName());
            admin.setGender(tempAdmin.getGender());
            admin.setOccupation(tempAdmin.getOccupation());
            String avatar = tempAdmin.getAvatar();
            if (StringUtils.isNotEmpty(avatar)) {
                if (pictureMap.get(avatar) != null && pictureMap.get(avatar) != "") {
                    admin.setPhotoUrl(pictureMap.get(avatar));
                }
            }
            result.add(admin);
        }
        return result;
    }

    @Override
    public String getOnlineAdminList(AdminVO adminVO) {
        // 获取Redis中匹配的所有key
        Set<String> keys = redisUtil.keys(RedisConf.ONLINE_ADMIN_LIST + "*");
        List<String> onlineAdminJsonList = redisUtil.multiGet(keys);
        // 拼装分页信息
        int pageSize = adminVO.getPageSize().intValue();
        int currentPage = adminVO.getCurrentPage().intValue();
        int total = onlineAdminJsonList.size();
        int startIndex = Math.max((currentPage - 1) * pageSize, 0);
        int endIndex = Math.min(currentPage * pageSize, total);
        //TODO 截取出当前分页下的内容，后面考虑用Redis List做分页
        List<String> onlineAdminSubList = onlineAdminJsonList.subList(startIndex, endIndex);
        List<OnlineAdmin> onlineAdminList = new ArrayList<>();
        for (String item : onlineAdminSubList) {
            OnlineAdmin onlineAdmin = JsonUtils.jsonToPojo(item, OnlineAdmin.class);
            // 数据脱敏【移除用户的token令牌】
            onlineAdmin.setToken("");
            onlineAdminList.add(onlineAdmin);
        }
        Page<OnlineAdmin> page = new Page<>();
        page.setCurrent(currentPage);
        page.setTotal(total);
        page.setSize(pageSize);
        page.setRecords(onlineAdminList);
        return ResultUtil.successWithData(page);
    }

    @Override
    public Admin getAdminByUser(String userName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_NAME, userName);
        queryWrapper.last(SysConf.LIMIT_ONE);
        //清空密码，防止泄露
        Admin admin = adminService.getOne(queryWrapper);
        admin.setPassWord(null);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtil.getPicture(pictureList));
        }
        Admin result = new Admin();
        result.setNickName(admin.getNickName());
        result.setOccupation(admin.getOccupation());
        result.setSummary(admin.getSummary());
        result.setAvatar(admin.getAvatar());
        result.setPhotoList(admin.getPhotoList());
        if (StringUtils.isNotEmpty(admin.getPersonResume())) {
            result.setPersonResume(admin.getPersonResume().replaceAll(" src=", " data-src="));
        }
        return result;
    }

    @Override
    public Admin getMe() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (request.getAttribute(SysConf.ADMIN_UID) == null || request.getAttribute(SysConf.ADMIN_UID) == "") {
            return new Admin();
        }
        Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());
        //清空密码，防止泄露
        admin.setPassWord(null);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtil.getPicture(pictureList));
        }
        return admin;
    }

    @Override
    public void addOnlineAdmin(Admin admin, Long expirationSecond) {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        String browser = map.get(SysConf.BROWSER);
        String ip = IpUtils.getIpAddr(request);
        OnlineAdmin onlineAdmin = new OnlineAdmin();
        onlineAdmin.setAdminUid(admin.getUid());
        onlineAdmin.setTokenId(admin.getTokenUid());
        onlineAdmin.setToken(admin.getValidCode());
        onlineAdmin.setOs(os);
        onlineAdmin.setBrowser(browser);
        onlineAdmin.setIpaddr(ip);
        onlineAdmin.setLoginTime(DateUtils.getNowTime());

        onlineAdmin.setUserName(admin.getUserName());
        onlineAdmin.setExpireTime(DateUtils.getDateStr(new Date(), expirationSecond));

        Role role = roleService.getById(admin.getRoleUid());
        if (role != null) {
            onlineAdmin.setRoleName(role.getRoleName());
        }

        //从Redis中获取IP来源
        String jsonResult = redisUtil.get(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(ip, SysConf.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                onlineAdmin.setLoginLocation(addresses);
                redisUtil.setEx(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            onlineAdmin.setLoginLocation(jsonResult);
        }
        // 将登录的管理员存储到在线用户表
        redisUtil.setEx(RedisConf.ONLINE_ADMIN_LIST + RedisConf.SEGMENTATION + admin.getValidCode(), JsonUtils.objectToJson(onlineAdmin), expirationSecond, TimeUnit.SECONDS);
        // 在维护一张表，用于 uuid - token 互相转换
        redisUtil.setEx(RedisConf.LOGIN_UUID_KEY + RedisConf.SEGMENTATION + admin.getTokenUid(), admin.getValidCode(), expirationSecond, TimeUnit.SECONDS);
    }

    @Override
    public String getList(AdminVO adminVO) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        String pictureResult = null;
        if (StringUtils.isNotEmpty(adminVO.getKeyword())) {
            queryWrapper.like(SQLConf.USER_NAME, adminVO.getKeyword()).or().like(SQLConf.NICK_NAME, adminVO.getKeyword().trim());
        }
        Page<Admin> page = new Page<>();
        page.setCurrent(adminVO.getCurrentPage());
        page.setSize(adminVO.getPageSize());
        // 去除密码
        queryWrapper.select(Admin.class, i -> !i.getProperty().equals(SQLConf.PASS_WORD));
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<Admin> pageList = adminService.page(page, queryWrapper);
        List<Admin> list = pageList.getRecords();

        final StringBuilder fileUids = new StringBuilder();
        List<String> adminUidList = new ArrayList<>();
        // 权限附身的用户
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
            adminUidList.add(item.getUid());

        });

        Map<String, String> pictureMap = new HashMap<>(Constants.NUM_TEN);
        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> userCollection = userService.listByIds(userUidList);
            if (userCollection.size() > 0) {
                List<User> userList = userService.convertUserList(userCollection);
                for (User user : userList) {
                    userMap.put(user.getUid(), user);
                }
            }
        }

        // 获取用户的网盘存储空间
        String storageListJson = pictureFeignClient.getStorageByAdminUid(adminUidList);
        List<Storage> storageList = webUtil.getList(storageListJson, Storage.class);
        Map<String, Storage> storageMap = new HashMap<>();
        storageList.forEach(item -> {
            storageMap.put(item.getAdminUid(), item);
        });

        for (Admin item : list) {
            Role role = roleService.getById(item.getRoleUid());
            item.setRole(role);

            //获取图片
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getAvatar(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    if (pictureMap.get(picture) != null && pictureMap.get(picture) != "") {
                        pictureListTemp.add(pictureMap.get(picture));
                    }
                });
                item.setPhotoList(pictureListTemp);
            }

            // 设置权限附身的用户
            if (StringUtils.isNotEmpty(item.getUserUid()) && userMap.get(item.getUserUid()) != null) {
                item.setUser(userMap.get(item.getUserUid()));
            }

            // 设置已用容量大小和最大容量
            Storage storage = storageMap.get(item.getUid());
            if (storage != null) {
                item.setStorageSize(storage.getStorageSize());
                item.setMaxStorageSize(storage.getMaxStorageSize());
            } else {
                // 如果没有，默认为0
                item.setStorageSize(0L);
                item.setMaxStorageSize(0L);
            }
        }
        return ResultUtil.successWithData(pageList);
    }

    @Override
    public String addAdmin(AdminVO adminVO) {

        String mobile = adminVO.getMobile();
        String userName = adminVO.getUserName();
        String email = adminVO.getEmail();
        if (StringUtils.isEmpty(userName)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(mobile)) {
            return ResultUtil.errorWithMessage("邮箱和手机号至少一项不能为空");
        }
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_NAME, userName);
        Admin temp = adminService.getOne(queryWrapper);
        if (temp == null) {
            Admin admin = new Admin();
            admin.setAvatar(adminVO.getAvatar());
            admin.setEmail(adminVO.getEmail());
            admin.setGender(adminVO.getGender());
            admin.setUserName(adminVO.getUserName());
            admin.setNickName(adminVO.getNickName());
            admin.setRoleUid(adminVO.getRoleUid());
            // 绑定用户
            admin.setUserUid(adminVO.getUserUid());
            // 设置为审核状态
            admin.setStatus(EStatus.ENABLE);
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            //设置默认密码
            admin.setPassWord(encoder.encode(defaultPassword));
            adminService.save(admin);
            //TODO 这里需要通过SMS模块，发送邮件告诉初始密码

            // 更新成功后，同时申请网盘存储空间
            String maxStorageSize = sysParamsService.getSysParamsValueByKey(SysConf.MAX_STORAGE_SIZE);
            // 初始化网盘的容量, 单位 B
            pictureFeignClient.initStorageSize(admin.getUid(), StringUtils.getLong(maxStorageSize, 0L) * 1024 * 1024);
            return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
        }
        return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
    }

    @Override
    public String editAdmin(AdminVO adminVO) {
        Admin admin = adminService.getById(adminVO.getUid());
        Assert.notNull(admin, MessageConf.PARAM_INCORRECT);
        //判断修改的对象是否是admin，admin的用户名必须是admin
        if (admin.getUserName().equals(SysConf.ADMIN) && !adminVO.getUserName().equals(SysConf.ADMIN)) {
            return ResultUtil.errorWithMessage("超级管理员用户名必须为admin");
        }
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.USER_NAME, adminVO.getUserName());
        List<Admin> adminList = adminService.list(queryWrapper);
        if (adminList != null) {
            for (Admin item : adminList) {
                if (item.getUid().equals(adminVO.getUid())) {
                    continue;
                } else {
                    return ResultUtil.errorWithMessage("修改失败，用户名存在");
                }
            }
        }

        // 判断是否更改了RoleUid，更新redis中admin的URL访问路径
        if (StringUtils.isNotEmpty(adminVO.getRoleUid()) && !adminVO.getRoleUid().equals(admin.getRoleUid())) {
            redisUtil.delete(RedisConf.ADMIN_VISIT_MENU + RedisConf.SEGMENTATION + admin.getUid());
        }
        admin.setUserName(adminVO.getUserName());
        admin.setAvatar(adminVO.getAvatar());
        admin.setNickName(adminVO.getNickName());
        admin.setGender(adminVO.getGender());
        admin.setEmail(adminVO.getEmail());
        admin.setQqNumber(adminVO.getQqNumber());
        admin.setGithub(adminVO.getGithub());
        admin.setGitee(adminVO.getGitee());
        admin.setOccupation(adminVO.getOccupation());
        admin.setUpdateTime(new Date());
        admin.setMobile(adminVO.getMobile());
        admin.setRoleUid(adminVO.getRoleUid());
        // 绑定用户
        admin.setUserUid(adminVO.getUserUid());
        // 无法直接修改密码，只能通过重置密码完成密码修改
        admin.setPassWord(null);
        admin.updateById();

        // 更新完成后，判断是否调整了网盘的大小
        String result = pictureFeignClient.editStorageSize(admin.getUid(), adminVO.getMaxStorageSize() * 1024 * 1024);
        Map<String, String> resultMap = webUtil.getMessage(result);
        if (SysConf.SUCCESS.equals(resultMap.get(SysConf.CODE))) {
            return ResultUtil.successWithMessage(resultMap.get(SysConf.MESSAGE));
        } else {
            return ResultUtil.errorWithMessage(resultMap.get(SysConf.MESSAGE));
        }
    }

    @Override
    public String editMe(AdminVO adminVO) {
        String adminUid = RequestHolder.getAdminUid();
        if (StringUtils.isEmpty(adminUid)) {
            return ResultUtil.errorWithMessage(MessageConf.INVALID_TOKEN);
        }
        Admin admin = new Admin();
        // 【使用Spring工具类提供的深拷贝，减少大量模板代码】
        BeanUtils.copyProperties(adminVO, admin, SysConf.STATUS);
        admin.setUpdateTime(new Date());
        admin.updateById();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String changePwd(String oldPwd, String newPwd) {
        String adminUid = RequestHolder.getAdminUid();
        if (StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        Admin admin = adminService.getById(adminUid);
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isPassword = encoder.matches(oldPwd, admin.getPassWord());
        if (isPassword) {
            admin.setPassWord(encoder.encode(newPwd));
            admin.setUpdateTime(new Date());
            admin.updateById();
            return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.ERROR_PASSWORD);
        }
    }

    @Override
    public String resetPwd(AdminVO adminVO) {
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        // 获取当前用户的管理员uid
        String adminUid = RequestHolder.getAdminUid();
        Admin admin = adminService.getById(adminVO.getUid());
        // 判断是否是admin重置密码【其它超级管理员，无法重置admin的密码】
        if (SysConf.ADMIN.equals(admin.getUserName()) && !admin.getUid().equals(adminUid)) {
            return ResultUtil.errorWithMessage(MessageConf.UPDATE_ADMIN_PASSWORD_FAILED);
        } else {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            admin.setPassWord(encoder.encode(defaultPassword));
            admin.setUpdateTime(new Date());
            admin.updateById();
            return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
        }
    }

    @Override
    public String deleteBatchAdmin(List<String> adminUidList) {
        boolean checkResult = StringUtils.checkUidList(adminUidList);
        if (!checkResult) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<Admin> adminList = new ArrayList<>();
        adminUidList.forEach(item -> {
            Admin admin = new Admin();
            admin.setUid(item);
            admin.setStatus(EStatus.DISABLED);
            admin.setUpdateTime(new Date());
            adminList.add(admin);
        });
        adminService.updateBatchById(adminList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String forceLogout(List<String> tokenUidList) {
        if (tokenUidList == null || tokenUidList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }

        // 从Redis中通过TokenUid获取到用户的真实token
        List<String> tokenList = new ArrayList<>();
        tokenUidList.forEach(item -> {
            String token = redisUtil.get(RedisConf.LOGIN_UUID_KEY + RedisConf.SEGMENTATION + item);
            if (StringUtils.isNotEmpty(token)) {
                tokenList.add(token);
            }
        });

        // 根据token删除Redis中的在线用户
        List<String> keyList = new ArrayList<>();
        List<String> onlineList = new ArrayList<>();
        String onlinePrefix = RedisConf.ONLINE_ADMIN_LIST + RedisConf.SEGMENTATION;
        String keyPrefix = String.format("%s:%s:", RedisConf.LOGIN_TOKEN_KEY, "ADMIN");
        for (String token : tokenList) {
            keyList.add(keyPrefix + token);
            onlineList.add(onlinePrefix + token);
        }
        redisUtil.delete(keyList);
        redisUtil.delete(onlineList);

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public Integer getBlogPublishCount(String adminUid) {
        if (StringUtils.isEmpty(adminUid)) {
            return 0;
        }
        String blogVisitCountJson = redisUtil.get(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + adminUid);
        if (StringUtils.isNotEmpty(blogVisitCountJson)) {
            return Integer.valueOf(blogVisitCountJson);
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.ADMINUID, adminUid);
        queryWrapper.eq(SQLConf.ARTICLE_SOURCE, EContributeSource.ADMIN_PUBLISH);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        Integer blogPublishCount = blogService.count(queryWrapper);
        redisUtil.setEx(RedisConf.BLOG_PUBLISH_COUNT + Constants.SYMBOL_COLON + adminUid, JsonUtils.objectToJson(blogPublishCount), 10, TimeUnit.MINUTES);
        return blogPublishCount;
    }

    @Override
    public Integer getBlogVisitCount(String adminUid) {

        Integer blogVisitCount = 0;
        if (StringUtils.isEmpty(adminUid)) {
            return blogVisitCount;
        }

        String blogVisitCountJson = redisUtil.get(RedisConf.BLOG_VISIT_COUNT + Constants.SYMBOL_COLON + adminUid);
        if (StringUtils.isNotEmpty(blogVisitCountJson)) {
            return Integer.valueOf(blogVisitCountJson);
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.ADMINUID, adminUid);
        queryWrapper.eq(SQLConf.ARTICLE_SOURCE, EContributeSource.ADMIN_PUBLISH);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 只获取uid信息
        queryWrapper.select(SQLConf.OID);
        List<Blog> blogList = blogService.list(queryWrapper);
        List<Integer> visitOidList = new ArrayList<>();
        blogList.forEach(item -> {
            visitOidList.add(item.getOid());
        });

        if (visitOidList.size() > 0) {
            QueryWrapper<WebVisit> webVisitQueryWrapper = new QueryWrapper<>();
            queryWrapper.in(SQLConf.OTHER_DATA, visitOidList);
            blogVisitCount = webVisitService.count(webVisitQueryWrapper);
            redisUtil.setEx(RedisConf.BLOG_VISIT_COUNT + Constants.SYMBOL_COLON + adminUid, JsonUtils.objectToJson(blogVisitCount), 10, TimeUnit.MINUTES);
        }
        return blogVisitCount;
    }

    @Override
    public String getAdminUserUid(String adminUid) {
        Admin admin = adminService.getById(adminUid);
        if (admin == null) {
            return null;
        }
        return admin.getUserUid();
    }
}
