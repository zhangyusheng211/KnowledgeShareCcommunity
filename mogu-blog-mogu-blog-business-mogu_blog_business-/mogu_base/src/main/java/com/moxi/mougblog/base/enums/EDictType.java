package com.moxi.mougblog.base.enums;

/**
 * 字典类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年3月26日10:41:15
 */
public enum EDictType {

    /**
     * 资源分类
     */
    SYS_RESOURCE_SORT("sys_resource_sort", "资源分类"),

;


    private final String type;
    private final String name;

    EDictType(String type, String name) {
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