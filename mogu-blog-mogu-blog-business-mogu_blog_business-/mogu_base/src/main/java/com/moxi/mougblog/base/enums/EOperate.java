package com.moxi.mougblog.base.enums;

/**
 * 用户操作枚举类
 */
public enum EOperate {

    /**
     * 新增
     */
    ADD("1", "新增"),

    /**
     * 编辑
     */
    EDIT("2", "编辑"),

    /**
     * 删除
     */
    DELETE("3", "删除"),

    /**
     * 审核
     */
    AUDIT("4", "审核");


    private final String operate;
    private final String name;

    EOperate(String operate, String name) {
        this.operate = operate;
        this.name = name;
    }
}
