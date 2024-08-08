package com.moxi.mougblog.base.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户标签枚举
 *
 * @author thh
 * @date 2021/12/24 19:40
 */
@Slf4j
public enum EUserTag {

    /**
     * 用户标签
     */
    NORMAL_USER("普通用户", 0),
    VIP_USER("博主", 1),
    OFFICIAL_USER("管理员", 2),
    ROBOT("机器人", 3),
    SHAREHOLDER("股东", 4),

    VIP_DAY("单日会员", 10),
    VIP_WEEK("七日会员", 11),
    VIP_MONTH("月度会员", 12),
    VIP_QUARTER("季度会员", 13),
    VIP_YEAR("年度会员", 14),
    VIP_LIFE_LONG("终身会员", 15),

    ;


    private String name;

    private Integer value;

    EUserTag(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value返回枚举类型，主要在switch中使用
     *
     * @param value
     * @return
     */
    public static EUserTag getByValue(Integer value) {
        for (EUserTag eUserTag : values()) {
            if (value.equals(eUserTag.getValue())) {
                return eUserTag;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}