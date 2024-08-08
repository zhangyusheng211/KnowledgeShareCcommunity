package com.moxi.mogublog.sms.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private JwtTokenFilter jwtTokenFilter;

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非 remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        //因为SpringSecurity使用X-Frame-Options防止网页被Frame。所以需要关闭为了让后端的接口管理的swagger页面正常显示
        httpSecurity.headers().frameOptions().disable();

        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .cors()//新加入,允许跨域
                .and()
                .csrf().disable()

                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
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
                        "/actuator/trace",
                        "/actuator/health",
                        "/actuator/metrics",
                        "/druid/**"
                ).permitAll()
                // 表示所匹配的URL都需要被认证才能访问
                .antMatchers(
                        "/task/**"
                ).authenticated()
                // 除上面外的所有请求全部不需要鉴权
                .anyRequest().permitAll();

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加两个过滤器
        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }
}

