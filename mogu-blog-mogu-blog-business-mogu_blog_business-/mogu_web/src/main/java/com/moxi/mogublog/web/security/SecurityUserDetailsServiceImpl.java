package com.moxi.mogublog.web.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.meetc.mogublog.security.SecurityUserFactory;
import com.meetc.mogublog.security.exception.UserNonactivatedException;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 将SpringSecurity中的用户管理和数据库的管理员对应起来
 *
 * @author 遇见
 * @date 2022/2/11
 */
@Service
@Slf4j
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserService userService;

    /**
     * @param username 浏览器输入的用户名【需要保证用户名的唯一性】
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_NAME, username).or().eq(SQLConf.EMAIL, username));
        queryWrapper.last(SysConf.LIMIT_ONE);
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        User user = userService.getOne(queryWrapper);
        if (user == null || EStatus.DISABLED == user.getStatus()) {
            log.error(String.format("[%s]该账号未注册,无法登录", username));
            throw new UsernameNotFoundException(String.format("[%s]该账号未注册,无法登录", username));
        } else {
            if (EStatus.FREEZE == user.getStatus()) {
                log.error(String.format("[%s]该账号未激活,无法登录", username));
                throw new UserNonactivatedException(String.format("[%s]该账号未激活,无法登录", username));
            }
            return SecurityUserFactory.create(user, "web");
        }
    }
}
