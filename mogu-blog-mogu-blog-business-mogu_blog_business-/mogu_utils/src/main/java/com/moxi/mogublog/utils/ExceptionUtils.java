package com.moxi.mogublog.utils;

import com.moxi.mogublog.utils.text.Convert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * 异常
 *
 * @author thh
 */
public class ExceptionUtils {

    /**
     * 异常校验
     * @param result
     * @return
     */
    public static boolean checkException(String result) {
        Map<String, Object> resultMap = JsonUtils.jsonToMap(result);
        Object code = resultMap.get("code");
        if ("error".equals(code)) {
            return true;
        }
        return false;
    }

}
