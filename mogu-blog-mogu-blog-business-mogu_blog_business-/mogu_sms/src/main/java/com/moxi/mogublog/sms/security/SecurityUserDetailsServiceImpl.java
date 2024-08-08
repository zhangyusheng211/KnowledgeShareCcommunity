package com.moxi.mogublog.sms.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meetc.mogublog.security.SecurityUserFactory;
import com.meetc.mogublog.security.exception.UserRoleNotFoundException;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.CategoryMenu;
import com.moxi.mogublog.commons.entity.Role;
import com.moxi.mogublog.utils.CheckUtils;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.CategoryMenuService;
import com.moxi.mogublog.xo.service.RoleService;
import com.moxi.mougblog.base.enums.EStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将SpringSecurity中的用户管理和数据库的管理员对应起来
 *
 * @author 遇见
 * @date 2022/2/11
 */
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private AdminService adminService;

    @Resource
    private RoleService roleService;

    @Resource
    private CategoryMenuService categoryMenuService;

    /**
     * @param username 浏览器输入的用户名【需要保证用户名的唯一性】
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Boolean isEmail = CheckUtils.checkEmail(username);
        Boolean isMobile = CheckUtils.checkMobileNumber(username);
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (isEmail) {
            queryWrapper.eq(com.moxi.mogublog.xo.global.SQLConf.EMAIL, username);
        } else if (isMobile) {
            queryWrapper.eq(com.moxi.mogublog.xo.global.SQLConf.MOBILE, username);
        } else {
            queryWrapper.eq(SQLConf.USER_NAME, username);
        }
        queryWrapper.last(SysConf.LIMIT_ONE);
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        Admin admin = adminService.getOne(queryWrapper);
        if (admin == null) {
            throw new UsernameNotFoundException(String.format("[%s]该账号未注册,无法登录", username));
        } else {
            //查询出角色信息封装到admin中
            Role role = roleService.getById(admin.getRoleUid());
            if (role == null) {
                throw new UserRoleNotFoundException(String.format("[%s]该账号未配置角色,无法登录", username));
            }
            List<String> roleNames = new ArrayList<>();
            roleNames.add(role.getRoleName());
            admin.setRoleNames(roleNames);
            List<String> categoryMenuUids = new ArrayList<>();
            String[] uids = role.getCategoryMenuUids().replace("[", "").replace("]", "").replace("\"", "").split(",");
            categoryMenuUids.addAll(Arrays.asList(uids));
            List<CategoryMenu> hasPermission =
                    categoryMenuService
                            .list(
                                    new LambdaQueryWrapper<CategoryMenu>()
                                            .in(CategoryMenu::getUid, categoryMenuUids)
                                            .eq(CategoryMenu::getStatus, 1)
                            );

            admin.setPermissionList(
                    hasPermission.size() > 0
                            ?
                            hasPermission.stream().map(
                                    categoryMenu -> {
                                        return categoryMenu.getUrl();
                                    }).collect(Collectors.toList())
                            :
                            new ArrayList<>()
            );
            return SecurityUserFactory.create(admin, "admin");
        }
    }
}
