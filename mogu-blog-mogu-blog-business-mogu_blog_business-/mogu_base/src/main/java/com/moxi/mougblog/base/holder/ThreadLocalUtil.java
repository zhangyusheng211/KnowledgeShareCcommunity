package com.moxi.mougblog.base.holder;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal工具类
 */
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new HashMap<>(10));

    public static Map<String, Object> getThreadLocal() {
        return threadLocal.get();
    }

    public static Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        return map.get(key);
    }

    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        map.put(key, value);
    }

    public static void set(Map<String, Object> keyValueMap) {
        Map<String, Object> map = threadLocal.get();
        map.putAll(keyValueMap);
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static <T> T remove(String key) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.remove(key);
    }

    /**
     * 获取用户uid
     *
     * @return
     */
    public static String getUserUid() {
        Map<String, Object> map = threadLocal.get();
        Object userUid = map.get("userUid");
        if (userUid == null) {
            return RequestHolder.getUserUid();
        }
        return userUid.toString();
    }

    /**
     * 获取用户uid
     *
     * @return
     */
    public static String getAdminUid() {
        Map<String, Object> map = threadLocal.get();
        Object adminUid = map.get("adminUid");
        if (adminUid == null) {
            return RequestHolder.getAdminUid();
        }
        return adminUid.toString();
    }

    /**
     * 获取用户uid
     *
     * @return
     */
    public static String getIp() {
        Map<String, Object> map = threadLocal.get();
        Object ip = map.get("ip");
        if (ip == null) {
            return RequestHolder.getIp();
        }
        return ip.toString();
    }
}