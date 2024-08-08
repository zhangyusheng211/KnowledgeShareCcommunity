package com.moxi.mougblog.base.holder;

import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.ServletUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.BaseMessageConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * RequestHolder
 *
 * @author 陌溪
 * @date 2020年2月27日08:44:28
 */
@Slf4j
public class RequestHolder extends ServletUtils {

    /**
     * 获取request
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        log.debug("getRequest -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取Response
     *
     * @return HttpServletRequest
     */
    public static HttpServletResponse getResponse() {
        log.debug("getResponse -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }

    /**
     * 获取session
     *
     * @return HttpSession
     */
    public static HttpSession getSession() {
        log.debug("getSession -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        HttpServletRequest request = null;
        if (null == (request = getRequest())) {
            return null;
        }
        return request.getSession();
    }

    /**
     * 获取session的Attribute
     *
     * @param name session的key
     * @return Object
     */
    public static Object getSession(String name) {
        log.debug("getSession -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 添加session
     *
     * @param name
     * @param value
     */
    public static void setSession(String name, Object value) {
        log.debug("setSession -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return;
        }
        servletRequestAttributes.setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 清除指定session
     *
     * @param name
     * @return void
     */
    public static void removeSession(String name) {
        log.debug("removeSession -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return;
        }
        servletRequestAttributes.removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 获取所有session key
     *
     * @return String[]
     */
    public static String[] getSessionKeys() {
        log.debug("getSessionKeys -- Thread id :{}, name : {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (null == servletRequestAttributes) {
            return null;
        }
        return servletRequestAttributes.getAttributeNames(RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 获取AdminUid
     *
     * @return
     */
    public static String getAdminUid() {
        // 优先从ThreadLocal中获取
        Object adminUid = ThreadLocalUtil.get(BaseSysConf.ADMIN_UID);
        if (adminUid != null) {
            return adminUid.toString();
        }
        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[getAdminUid] request is null");
            return null;
        }
        if (request.getAttribute(BaseSysConf.ADMIN_UID) != null) {
            return request.getAttribute(BaseSysConf.ADMIN_UID).toString();
        } else {
            // 若获取不到，从参数中获取
            return getParameter(BaseSysConf.ADMIN_UID, null);
        }
    }

    /**
     * 获取用户权限码
     * @return
     */
    public static String getAuthCodeList() {
        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[getAuthCodeList] request is null");
            return "";
        }
        if (request.getAttribute(BaseSysConf.AUTH_CODE_LIST) != null) {
            return request.getAttribute(BaseSysConf.AUTH_CODE_LIST).toString();
        } else {
            // 若获取不到，从参数中获取
            return getParameter(BaseSysConf.AUTH_CODE_LIST, "");
        }
    }

    /**
     * 从上下文中获取用户名
     * @return
     */
    public static String getUserName() {
        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[getAdminUid] request is null");
            return null;
        }
        if (request != null && request.getAttribute(BaseSysConf.USER_NAME) != null) {
            return request.getAttribute(BaseSysConf.USER_NAME).toString();
        } else {
            // 若获取不到，从参数中获取
            return getParameter(BaseSysConf.USER_NAME, null);
        }
    }

    /**
     * 获取AdminToken
     *
     * @return
     */
    public static String getAdminToken() {
        return getParameter(BaseSysConf.TOKEN, null);
    }

    /**
     * 检查当前用户是否登录【未登录操作将抛出QueryException异常】
     */
    public static String checkLogin() {
        if (StringUtils.isEmpty(getAdminUid())) {
            log.error("用户未登录");
            throw new QueryException(ErrorCode.INVALID_TOKEN, BaseMessageConf.INVALID_TOKEN);
        }
        return getAdminUid();
    }

    /**
     * 获取门户用户的UID
     *
     * @return
     */
    public static String getUserUid() {
        // 优先从ThreadLocal中获取
        Object userUid = ThreadLocalUtil.get(BaseSysConf.USER_UID);
        if (userUid != null) {
            return userUid.toString();
        }

        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            return null;
        }
        if (request.getAttribute(BaseSysConf.USER_UID) != null) {
            return request.getAttribute(BaseSysConf.USER_UID).toString();
        } else {
            // 若获取不到，从参数中获取
            return getParameter(BaseSysConf.USER_UID, null);
        }
    }

    /**
     * 获取ip地址
     * @return
     */
    public static String getIp() {
        // 优先从ThreadLocal中获取
        Object ip = ThreadLocalUtil.get(BaseSysConf.IP);
        if (ip != null) {
            return ip.toString();
        }
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            return "";
        }
        return IpUtils.getIpAddr(request);
    }

//    /**
//     * 获取用户的特权权限
//     *
//     * @return
//     */
//    public static double getUserTag() {
//        double userTag = 0;
//        HttpServletRequest request = RequestHolder.getRequest();
//        if (request == null) {
//            log.info("[checkUser] request is null");
//            return userTag;
//        }
//        if (request.getAttribute(BaseSysConf.USER_TAG) != null) {
//            userTag = (double) request.getAttribute(BaseSysConf.USER_TAG);
//        }
//        return userTag;
//    }

    /**
     *
     * @return
     */
    public static Integer getUserTag() {
        // 获取用户标签
        int userTag = 0;
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[checkUser] request is null");
            return userTag;
        }
        if (request.getAttribute(BaseSysConf.USER_TAG) != null) {
            Object userTagObj = request.getAttribute(BaseSysConf.USER_TAG);
            userTag = Double.valueOf(String.valueOf(userTagObj)).intValue();
        }
        return userTag;
    }


    /**
     * 判断登录用户是否是普通用户
     *
     * @return
     */
    public static boolean checkUser() {
        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[checkUser] request is null");
            return true;
        }
        if (request.getAttribute(BaseSysConf.USER_TYPE) == null) {
            throw new QueryException("[RequestHolder] userType not exist");
        }
        String userType = request.getAttribute(BaseSysConf.USER_TYPE).toString();
        if ("USER".equals(userType)) {
            return true;
        }
        return false;
    }

    /**
     * 当前用户是否是管理员
     *
     * @return
     */
    public static boolean checkAdmin() {
        // 优先从请求头中获取用户uid
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            log.info("[checkAdmin] request is null");
            return false;
        }
        if (request.getAttribute(BaseSysConf.USER_TYPE) == null) {
            return false;
        }
        String userType = request.getAttribute(BaseSysConf.USER_TYPE).toString();
        if ("ADMIN".equals(userType)) {
            return true;
        }
        return false;
    }
}
