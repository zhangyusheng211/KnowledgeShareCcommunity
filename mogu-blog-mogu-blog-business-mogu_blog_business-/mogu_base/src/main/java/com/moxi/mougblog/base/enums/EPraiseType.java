package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 点赞类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2022年1月15日08:45:04
 */
public enum EPraiseType {

    /**
     * 点赞
     */
    PRAISE("1", "点赞"),

    /**
     * 点踩
     */
    TREAD("2", "点踩");


    private final String type;
    private final String name;

    /**
     * 获取点赞类型
     *
     * @param praiseType
     * @return
     */
    public static EPraiseType getType(String praiseType) {
        for (EPraiseType type : EPraiseType.values()) {
            if (type.getType().equals(praiseType)) {
                return type;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    EPraiseType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}