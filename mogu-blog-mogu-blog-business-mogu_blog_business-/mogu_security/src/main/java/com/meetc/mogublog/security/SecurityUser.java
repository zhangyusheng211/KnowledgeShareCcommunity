package com.meetc.mogublog.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moxi.mogublog.commons.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * SpringSecurity中的用户实体类
 *
 * @author 陌溪
 * @date 2020年9月19日21:43:47
 */
public class SecurityUser implements UserDetails {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * user  Uid
     */
    private  String uid;
    /**
     * user 用户名
     */
    private  String username;
    /**
     * user 密码
     */
    private String password;
    /**
     * 角色
     */
    private String role;
    /**
     * 标识 User/Admin
     */
    private String client;
    /**
     * 是否禁用
     */
    private  boolean enabled;

    private  Collection<? extends GrantedAuthority> authorities;

    /**
     *
     * @param uid
     * @param username
     * @param password
     * @param role
     * @param client
     * @param enabled
     * @param authorities
     */
    public SecurityUser(
            String uid,
            String username,
            String password,
            Role role, String client, Boolean enabled,
            Collection<? extends GrantedAuthority> authorities) {
        this.uid = uid;
        this.username = username;
        this.password =password;
        this.client = client;
        this.enabled = enabled;
        this.authorities = authorities;
        this.role = role == null? "普通用户": role.getRoleName();
    }

    /**
     * 无鉴权服务专用SecurityUser
     * @param username
     */
    public SecurityUser(String uid, String username, String client) {
        this.uid = uid;
        this.username = username;
        this.client = client;
    }

    /**
     * 返回分配给用户的角色列表
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public String getUid() {
        return uid;
    }


    @Override
    public String getUsername() {
        return username;
    }

    public String getClient() {
        return client;
    }

    /**
     * 账户是否激活
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 账户是否未过期
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "SecurityUser{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", client='" + client + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}

