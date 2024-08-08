package com.meetc.mogublog.security.authorize;

import com.meetc.mogublog.security.SecurityUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义验证白名单
 * 该验证存在于已登录用户
 *  @author Mr.Zjq
 */
@Service("authorizeService")
public class AuthorizeService {

    /**
     * springframework自带的任意路由正则
     */
    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 需要登录但是不需要验权的url
     */
    private static List<String> urls = new ArrayList<String>(){{
        add("/sysDictData/**");
        add("/index/**");
        add("/index/**");
        add("/pictureSort/getPictureSortByUid");
    }};

    /**
     * 管理员白名单url
     */
    private static List<String> superAdminUrl = new ArrayList<String>(){{
        add("/categoryMenu/getAll");
        add("/categoryMenu/getButtonAll");
        add("/categoryMenu/getCategoryMenuByUserName");
    }};

    public boolean check(Authentication authentication, HttpServletRequest request) {
        Object principal = authentication.getPrincipal();
        //判断是否是认证的用户
        if (principal != null && principal instanceof UserDetails) {
            SecurityUser user = (SecurityUser) principal;
            //获取认证用户里的url列表
            List<SimpleGrantedAuthority> authorities = (ArrayList<SimpleGrantedAuthority>) user.getAuthorities();

            //判断url列表里是否包含request请求的url
            boolean contains = authorities.stream()
                    .map(SimpleGrantedAuthority::getAuthority)
                    .anyMatch(a-> antPathMatcher.match(a,request.getRequestURI()));
             //判断访问的url是否在白名单中
            boolean whiteContains = checkWhiteList(request.getRequestURI());
             //检测特定url
            boolean superContains = checkSuperUrl(request.getRequestURI(), user);
            //满足一个即可放行
            return contains||whiteContains||superContains;
        }
        return false;
    }

    /**
     * 校验用户是否登录
     * @return
     */
    public boolean checkUserLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return true;
        }
        return false;
    }

    /**
     * 白名单检测
     * @param url
     * @return
     */
    public boolean checkWhiteList(String url){
        return  urls.stream().anyMatch(a-> antPathMatcher.match(a,url));
    };

    /**
     * 超管白名单检测
     * @param url
     * @return
     */
    public boolean checkSuperUrl(String url,SecurityUser securityUser){
        if (!UserType.SuperAdmin.getName().equals(securityUser.getRole())){
            return superAdminUrl.stream().anyMatch(a->antPathMatcher.match(a,url));
        }else {
            return true;
        }
    }

    public enum UserType{
        SuperAdmin("超级管理员"),
        NormalUser("普通用户");
        private String name;

        UserType(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }
}
