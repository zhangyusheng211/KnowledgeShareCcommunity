package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 掌握状态枚举
 *
 * @Author: 陌溪
 * @Date: 2022年3月12日09:52:01
 */
public enum EDegreeStatus {

    /**
     * 未掌握
     */
    NO_MASTERY("1", "未掌握"),

    /**
     * 问答
     */
    VISIT("2", "已看过"),

    /**
     * 掌握
     */
    MASTERY("3", "掌握");

    private final String type;
    private final String name;

    EDegreeStatus(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * 获取掌握状态
     * type
     * @param resourceType
     * @return
     */
    public static EDegreeStatus getType(String resourceType) {
        for (EDegreeStatus type : EDegreeStatus.values()) {
            if (type.getType().equals(resourceType)) {
                return type;
            }
        }
        throw new BusinessException("用户掌握状态转换失败");
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}