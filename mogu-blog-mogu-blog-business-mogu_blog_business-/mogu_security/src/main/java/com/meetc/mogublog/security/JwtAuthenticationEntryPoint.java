package com.meetc.mogublog.security;

import com.alibaba.fastjson.JSON;
import com.moxi.mogublog.utils.ServletUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 认证进入点 【认证失败处理类 返回未授权】
 *
 * @author 陌溪
 * @date 2020年9月19日10:04:54
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // response.setStatus(ECode.SUCCESS);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> result = new HashMap<>(Constants.NUM_THREE);
        result.put(BaseSysConf.CODE, ECode.UNAUTHORIZED);
//        String msg = StringUtils.format("token无效或过期,请重新登录");
        result.put(BaseSysConf.MESSAGE, "token无效或过期,请重新登录");
        result.put(BaseSysConf.DATA, "token无效或过期,请重新登录");
        log.error("[JwtAuthenticationEntryPoint] 请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(result));
    }
}

