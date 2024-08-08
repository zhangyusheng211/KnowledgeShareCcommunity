package com.moxi.mogublog.admin.security;


import com.meetc.mogublog.security.JwtAuthenticationEntryPoint;
import com.meetc.mogublog.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Resource
    private JwtAuthenticationTokenFilter authenticationTokenFilter;


    /**
     * 装载BCrypt密码编码器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        //因为SpringSecurity使用X-Frame-Options防止网页被Frame。所以需要关闭为了让后端的接口管理的swagger页面正常显示
        httpSecurity.headers().frameOptions().disable();

        httpSecurity
                // CSRF禁用，因为不使用session
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                // 认证失败处理类
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

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
                // 对于获取token的RestApi要允许匿名访问
                .antMatchers("/auth/**",
                        "/sysDictData/**",
                        "/creatCode/**",
                        "/index/**"
                ).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().access("@adminAuthorizeService.check(authentication,request)");
        // 添加两个过滤器
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);


        // 禁用缓存
        httpSecurity.headers().cacheControl();

    }
}

