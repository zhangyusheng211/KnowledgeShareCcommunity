package com.moxi.mogublog.picture.security;


import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.meetc.mogublog.security.SecurityUser;
import com.meetc.mogublog.security.jwt.Audience;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import com.meetc.mogublog.security.jwt.UserType;
import com.moxi.mogublog.commons.global.RedisConf;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT认证过滤器 【验证token有效性】
 *
 * @author 陌溪
 * @date 2020年9月19日10:05:40
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {


    @Resource
    private Audience audience;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Value(value = "${tokenHead}")
    private String tokenHead;

    @Value(value = "${tokenHeader}")
    private String tokenHeader;

    /**
     * token过期的时间
     */
    @Value(value = "${audience.expiresSecond}")
    private Long expiresSecond;

    /**
     * token刷新的时间
     */
    @Value(value = "${audience.refreshSecond}")
    private Long refreshSecond;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 返回请求头设置LogId
        MDCAdapter mdc = MDC.getMDCAdapter();
        Map<String, String> traceMap = ((LogbackMDCAdapter)mdc).getPropertyMap();
        if (traceMap != null) {
            String traceId = traceMap.get("traceId");
            if (response.getHeader(SysConf.LOG_ID) == null) {
                response.addHeader(SysConf.LOG_ID, traceId);
            }
        }

        //得到请求头信息authorization信息
        String authHeader = request.getHeader(tokenHeader);
        //获取IP
        String ip = IpUtils.getIpAddr(request);
        log.info("用户访问的URI：{}，用户的ip地址：{}", request.getRequestURI(), ip);
        //TODO 判断是否触发其它微服务发送的请求【图片上传鉴权，需要用户登录，携带token请求admin，后期考虑加入OAuth服务统一鉴权】
        final String serviceToken = request.getHeader(SysConf.SERVICE_TOKEN);
        // 是否是内部接口请求
        if (StringUtils.isNotEmpty(serviceToken)) {
            authHeader = serviceToken;
        } else if(StringUtils.isNotEmpty(request.getParameter(SysConf.TOKEN))) {
            // 从参数中获取token，编辑器上传图片的校验逻辑
            authHeader = request.getParameter(SysConf.TOKEN);
        } else if (request.getAttribute(SysConf.TOKEN) != null) {
            // 从参数中获取token，编辑器上传图片的校验逻辑
            authHeader = request.getAttribute(SysConf.TOKEN).toString();
        }


        //请求头 'Authorization': tokenHead + token
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String token = authHeader.substring(tokenHead.length());
            // 私钥
            String base64Secret = audience.getBase64Secret();
            // 检查token是否过期
            boolean isExpiration = jwtTokenUtil.isExpiration(token, base64Secret);
            if (isExpiration) {
                log.error("[JwtTokenFilter] 用户token已过期");
                chain.doFilter(request, response);
                return;
            }

            //解析token的uid
            String uid = jwtTokenUtil.getUserUid(token, base64Secret);
            String username = jwtTokenUtil.getUsername(token, base64Secret);
            //jwt加密时使用的是枚举 解析出来的userType是枚举常量名
            String userType = jwtTokenUtil.getUserType(token, base64Secret);

            /**
             * 查询缓存中token
             */
            String onlineUser = redisUtil.get(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, userType, authHeader));
            /**
             * 当缓存失效 或 原token 与 现token不一致
             * 则认为现token是过期token
             */
            if (StringUtils.isEmpty(onlineUser)){
                chain.doFilter(request, response);
                return;
            }
            /**
             *  JWT解析token
             *  当发现携带token的请求与原请求token的ip不一致时
             *  ip核验不通过直接返回
             */
//            if (!jwtTokenUtil.ipCheck(token,ip,base64Secret)){
//                chain.doFilter(request, response);
//                return;
//            }
            if (userType.equals(UserType.ADMIN.name())){
                //把adminUid存储到request中
                request.setAttribute(SysConf.ADMIN_UID, uid);
                request.setAttribute(SysConf.USER_NAME, username);
                request.setAttribute(SysConf.TOKEN, authHeader);
            } else if (userType.equals(UserType.USER.name())){
                //从Redis中获取内容
                String userInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + authHeader);
                if (!StringUtils.isEmpty(userInfo)) {
                    Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
                    //把userUid存储到 request中
                    request.setAttribute(SysConf.TOKEN, authHeader);
                    request.setAttribute(SysConf.CREATE_TIME,  map.get(SysConf.CREATE_TIME));
                    request.setAttribute(SysConf.USER_UID, map.get(SysConf.UID));
                    request.setAttribute(SysConf.USER_TAG, map.get(SysConf.USER_TAG));
                    request.setAttribute(SysConf.USER_NAME, map.get(SysConf.NICK_NAME));
                }
            }
            log.info("[JwtTokenFilter] 用户名:{}, 用户uid:{}", username, uid);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 通过用户名加载SpringSecurity用户
                UserDetails userDetails = new SecurityUser(uid, username, userType);
                // 校验Token的有效性
                if (jwtTokenUtil.validateToken(token, userDetails, base64Secret)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //以后可以security中取得SecurityUser信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}


