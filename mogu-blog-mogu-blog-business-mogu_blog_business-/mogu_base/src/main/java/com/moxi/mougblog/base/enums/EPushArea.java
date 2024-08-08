package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 推送范围枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年4月2日17:12:13
 */
public enum EPushArea {

    /**
     * 所有用户
     */
    ALL(1, "所有用户"),

    /**
     * 指定用户
     */
    USER_LIST(2, "指定用户"),

    /**
     * 标签用户
     */
    USER_TAG(3, "标签用户"),

    ;


    /**
     * 获取推送范围
     *
     * @param type
     * @return
     */
    public static EPushArea getPushArea(Integer type) {
        for (EPushArea pushArea : EPushArea.values()) {
            if (pushArea.getType() == type) {
                return pushArea;
            }
        }
        throw new BusinessException("获取推送范围失败");
    }
    private final int type;
    private final String name;

    EPushArea(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}