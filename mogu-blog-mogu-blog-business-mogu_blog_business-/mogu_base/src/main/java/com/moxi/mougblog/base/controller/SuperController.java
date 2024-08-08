package com.moxi.mougblog.base.controller;

import com.moxi.mougblog.base.global.Constants;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller基类
 *
 * @author 陌溪
 * @date 2017年9月30日18:12:56
 */
public class SuperController {

    /**
     * 获取一个map - z初始容量为1，key是字符串对象，value是Object对象
     *
     * @return
     */
    public static Map<String, Object> getMap() {
        // z指定了初始容量-1
        Map<String, Object> map = new HashMap<>(Constants.NUM_ONE);
        return map;
    }

    /**
     * 将map转换成json字符串
     *
     * @param map
     * @return
     */
    public String toJson(Map<String, Object> map) {
        // z将传入的 Map 对象转换为 JSONObject，然后调用 toString() 方法将其转换为 JSON 字符串并返回。
        return JSONObject.fromObject(map).toString();
    }

    public <T> String toJson(List<T> list) {
        // z将传入的 List 对象转换为 JSONObject，然后调用 toString() 方法将其转换为 JSON 字符串并返回。
        return JSONObject.fromObject(list).toString();
    }
}
