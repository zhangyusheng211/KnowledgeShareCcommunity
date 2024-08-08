package com.moxi.mogublog.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 参数签名工具类
 * @date 2022年4月6日22:28:01
 * @author 陌溪
 */
@Slf4j
public class SignUtils {

    // 签名秘钥
    private static final String SIGNKEY = "www.moguit.cn";

    /**
     * 请求参数转Map
     *
     * @param paramMap
     * @return
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }

    /**
     * 请求数据获取签名
     *
     * @param paramMap 请求参数
     * @return
     */
    public static String getSign(Map<String, Object> paramMap) {
        paramMap.remove("sign");
        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");
        }
        str.append(SIGNKEY);
        log.info("加密前：" + str);
        String md5Str = MD5Utils.string2MD5(str.toString());
        log.info("加密后：" + md5Str);
        return md5Str;
    }

    /**
     * 签名校验
     *
     * @param paramMap 请求参数
     * @return
     */
    public static boolean isSignEquals(Map<String, Object> paramMap) {
        String sign = (String) paramMap.get("sign");
        String md5Str = getSign(paramMap);
        return sign.equals(md5Str);
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }
}
