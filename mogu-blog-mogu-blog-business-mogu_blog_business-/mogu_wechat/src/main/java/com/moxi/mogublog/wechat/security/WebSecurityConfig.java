package com.moxi.mogublog.wechat.security;


import com.meetc.mogublog.security.CustomAuthenticationProvider;
import com.meetc.mogublog.security.JwtAuthenticationEntryPoint;
import com.meetc.mogublog.security.filter.JwtAuthenticationTokenFilter;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * SpringSecurity配置文件
 * 用于配置哪些请求被拦截，哪些请求可以匿名访问
 *
 * @author 陌溪
 * @date 2020年9月19日10:05:40
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        //原因是因为springSecurty使用X-Frame-Options防止网页被Frame。所以需要关闭为了让后端的接口管理的swagger页面正常显示
        httpSecurity.headers().frameOptions().disable();

        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .cors()//新加入,允许跨域
                .and()
                .csrf().disable()

                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/*",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/webjars/**",
                        "/actuator/**",
                        "/druid/**"
                ).permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers(
                        "/wxAuth/**",
                        "/wx/msg/**",
                        "/wxUser/**",
                        "/wxUserTags/**"
                ).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().access("@weChatAuthorizeService.check(authentication, request)");

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加两个过滤器
        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }
}