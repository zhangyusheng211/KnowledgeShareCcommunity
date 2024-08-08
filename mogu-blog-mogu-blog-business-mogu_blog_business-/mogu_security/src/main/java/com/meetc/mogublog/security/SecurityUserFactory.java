package com.meetc.mogublog.security;


import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mougblog.base.enums.EStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpringSecurity用户工厂类
 *
 * @author 陌溪
 * @date 2020年9月19日20:03:25
 */
public final class SecurityUserFactory {

    private SecurityUserFactory() {
    }

    /**
     * 通过管理员Admin，生成一个SpringSecurity用户
     *
     * @param admin
     * @param client
     * @return
     */
    public static SecurityUser create(Admin admin, String client) {
        boolean enabled = admin.getStatus() == EStatus.ENABLE;
        return new SecurityUser(
                admin.getUid(),
                admin.getUserName(),
                admin.getPassWord(),
                admin.getRole(),
                client,
                enabled,
                mapToGrantedAuthorities(admin.getPermissionList())
        );
    }

    /**
     * 通过用户User，生成一个SpringSecurity用户
     * @param user
     * @param client
     * @return
     */
    public static SecurityUser create(User user, String client) {
        boolean enabled = user.getStatus() == EStatus.ENABLE;
        return new SecurityUser(
                user.getUid(),
                user.getUserName(),
                user.getPassWord(),
                null,
                client,
                enabled,
                mapToGrantedAuthorities(new ArrayList<String>())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
