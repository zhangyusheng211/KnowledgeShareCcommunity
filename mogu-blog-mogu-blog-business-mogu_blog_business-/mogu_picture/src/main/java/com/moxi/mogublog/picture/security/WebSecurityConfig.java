package com.moxi.mogublog.picture.security;

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
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtTokenFilter jwtTokenFilter;

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
                // 表示所匹配的URL都需要被认证才能访问
                .antMatchers(
                    "/file/cropperPicture",
                        "/file/pictures",
                        "/file/uploadVideo",
                        "/file/ckeditorUploadFile",
                        "/file/ckeditorUploadCopyFile",
                        "/file/ckeditorUploadToolFile",
                        "/file/ckeditorUploadToolFile",
                        "/networkDisk/**",
                        "/feign/**",
                        "/storage/**"
                ).authenticated()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers(
                        "/file/**"
                ).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().access("@authorizeService.check(authentication, request)");

        // 禁用缓存
        httpSecurity.headers().cacheControl();

        // 添加两个过滤器
        // 添加JWT filter
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }
}