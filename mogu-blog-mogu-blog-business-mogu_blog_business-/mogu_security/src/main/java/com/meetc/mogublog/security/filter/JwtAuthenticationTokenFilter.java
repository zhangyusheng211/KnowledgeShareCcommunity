package com.meetc.mogublog.security.filter;


import ch.qos.logback.classic.util.LogbackMDCAdapter;
import cn.hutool.core.text.AntPathMatcher;
import com.meetc.mogublog.security.jwt.Audience;
import com.meetc.mogublog.security.jwt.JwtTokenUtil;
import com.meetc.mogublog.security.jwt.UserType;
import com.moxi.mogublog.commons.global.RedisConf;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.utils.*;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.spi.MDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Resource
    private Audience audience;

    @Resource
    private UserDetailsService userDetailsService;

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
        String ip = IpUtils.getIpAddr(request);
        //TODO 判断是否触发其它微服务发送的请求【图片上传鉴权，需要用户登录，携带token请求admin，后期考虑加入OAuth服务统一鉴权】
        final String pictureToken = request.getHeader(BaseSysConf.SERVICE_TOKEN);
        // 是否是内部接口请求
        Boolean isServiceRequest = false;
        if (StringUtils.isNotEmpty(pictureToken)) {
            isServiceRequest = true;
            authHeader = pictureToken;
        }

        //请求头 'Authorization': tokenHead + token
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String token = authHeader.substring(tokenHead.length());
            // 私钥
            String base64Secret = audience.getBase64Secret();

            //解析token的uid
            String uid = "";
            String username = "";
            String userType = "";
            try {
                uid = jwtTokenUtil.getUserUid(token, base64Secret);
                username = jwtTokenUtil.getUsername(token, base64Secret);
                //jwt加密时使用的是枚举 解析出来的userType是枚举常量名
                userType = jwtTokenUtil.getUserType(token, base64Secret);
            } catch (Exception e) {
                log.error("[JwtTokenFilter] 用户token校验失败");
                chain.doFilter(request, response);
                return;
            }

            /**
             * 查询缓存中token
             */
            String onlineUser = redisUtil.get(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, userType, authHeader));

            /**
             * 当缓存失效, 则认为现token是过期token
             */
            if (StringUtils.isEmpty(onlineUser)) {
                log.error("[JwtTokenFilter] 用户token已过期");
                chain.doFilter(request, response);
                return;
            }

            /**
             *  JWT解析token
             *  当发现携带token的请求与原请求token的ip不一致时
             *  ip核验不通过直接返回
             */
//            if (!jwtTokenUtil.ipCheck(token, ip, base64Secret) && !isServiceRequest){
//                chain.doFilter(request, response);
//                return;
//            }

            /**
             * token续期
             */
//            if (StringUtils.isNotEmpty(onlineUser) && !jwtTokenUtil.isExpiration(token, base64Secret)) {
//                /**
//                 * 得到过期时间
//                 */
//                Date expirationDate = jwtTokenUtil.getExpiration(token, base64Secret);
//                long nowMillis = System.currentTimeMillis();
//                Date nowDate = new Date(nowMillis);
//                // 得到两个日期相差的间隔，秒
//                Integer survivalSecond = DateUtils.getSecondByTwoDay(expirationDate, nowDate);
//                // 当存活时间小于更新时间，那么将颁发新的Token到客户端，同时重置新的过期时间
//                // 而旧的Token将会在不久之后从Redis中过期
//                if (survivalSecond < refreshSecond) {
//                    // 生成一个新的Token
//                    String newToken = tokenHead + jwtTokenUtil.refreshToken(token, base64Secret, expiresSecond * 1000);
//                    // 生成新的token，发送到客户端
//                    if (userType.equals(UserType.ADMIN.name())){
//                        CookieUtils.setCookie("Admin-Token", newToken, expiresSecond.intValue());
//                    } else if (userType.equals(UserType.USER.name())){
//                        CookieUtils.setCookie("token", newToken, expiresSecond.intValue());
//                    }
//                    // 移除原来的旧Token和TokenUid【1分钟后自动删除】
//                    redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY, userType, authHeader), onlineUser, 1, TimeUnit.MINUTES);
//                    // 将新token赋值，用于后续使用
//                    authHeader = newToken;
//                    // 将新的Token存入Redis中
//                    redisUtil.setEx(String.format("%s:%s:%s", RedisConf.LOGIN_TOKEN_KEY,userType, newToken), onlineUser, expiresSecond, TimeUnit.SECONDS);
//                }
//            }

            if (userType.equals(UserType.ADMIN.name())) {
                //把adminUid存储到request中
                request.setAttribute(SysConf.ADMIN_UID, uid);
                request.setAttribute(SysConf.USER_NAME, username);
                request.setAttribute(SysConf.TOKEN, authHeader);
                request.setAttribute(SysConf.USER_TYPE, userType);
            } else if (userType.equals(UserType.USER.name())) {
                //从Redis中获取内容
                String userInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + authHeader);
                if (!StringUtils.isEmpty(userInfo)) {
                    Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
                    //把userUid存储到 request中
                    request.setAttribute(SysConf.TOKEN, authHeader);
                    request.setAttribute(SysConf.CREATE_TIME, map.get(SysConf.CREATE_TIME));
                    request.setAttribute(SysConf.USER_UID, map.get(SysConf.UID));
                    request.setAttribute(SysConf.USER_TAG, map.get(SysConf.USER_TAG));
                    request.setAttribute(SysConf.USER_NAME, map.get(SysConf.NICK_NAME));
                    request.setAttribute(SysConf.CREDITS, map.get(SysConf.CREDITS));
                    Object expObj = map.get(SysConf.EXP_VALUE);
                    if (expObj == null) {
                        expObj = map.get(SysConf.CREDITS);
                    }
                    request.setAttribute(SysConf.AUTH_CODE_LIST, map.get(SysConf.AUTH_CODE_LIST));
                    request.setAttribute(SysConf.EXP_VALUE, expObj);
                    request.setAttribute(SysConf.USER_TYPE, userType);
                }
            }

            // 清空前端传递过来的异常Cookie
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (!"subjectRecord".equals(cookie.getName())) {
                        continue;
                    }
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }

            log.info("[JwtTokenFilter] 用户名:{}, 用户uid:{}", username, uid);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 通过用户名加载SpringSecurity用户
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
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


