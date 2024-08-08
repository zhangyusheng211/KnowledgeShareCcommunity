package com.moxi.mougblog.base.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户等级枚举
 *
 * @author 陌溪
 * @date 2022年5月15日10:58:58
 */
@Slf4j
public enum EUserLevel {

    /**
     * 用户等级
     */
    Lv1("初出茅庐", 1, 0, 300),
    Lv2("崭露头角", 2, 300, 1000),
    Lv3("小有名气", 3, 1000, 2000),
    Lv4("游刃有余", 4, 2000, 5000),
    Lv5("大显身手", 5, 5000, 10000),
    Lv6("登峰造极", 6, 10000, 30000);

    // 名称
    private String name;
    // 用户等级
    private Integer level;
    // 最小积分值
    private Integer minCredit;
    // 最大积分值
    private Integer maxCredit;

    EUserLevel(String name, Integer level, Integer minCredit, Integer maxCredit) {
        this.name = name;
        this.level = level;
        this.minCredit = minCredit;
        this.maxCredit = maxCredit;
    }

    /**
     * 根据level返回枚举类型，主要在switch中使用
     *
     * @param level
     * @return
     */
    public static EUserLevel getByValue(Integer level) {
        for (EUserLevel userLevel : values()) {
            if (level.equals(userLevel.getLevel())) {
                return userLevel;
            }
        }
        return Lv5;
    }

    /**
     * 根据积分大小获取等级枚举类型
     *
     * @param credit
     * @return
     */
    public static EUserLevel getLvByCredit(Integer credit) {
        if (credit != null) {
            for (EUserLevel userLevel : values()) {
                if (userLevel.maxCredit > credit && userLevel.minCredit <= credit) {
                    return userLevel;
                }
            }
        }
        return Lv1;
    }

    /**
     * 根据经验值获取用户等级
     * @param expValue
     * @return
     */
    public static EUserLevel getLvByExpValue(Integer expValue) {
        if (expValue != null) {
            for (EUserLevel userLevel : values()) {
                if (userLevel.maxCredit > expValue && userLevel.minCredit <= expValue) {
                    return userLevel;
                }
            }
        }
        return Lv1;
    }

    /**
     * 获取用户下一个等级
     *
     * @param credit
     * @return
     */
    public static EUserLevel getNextLvByCredit(Integer credit) {
        EUserLevel level = getLvByCredit(credit);
        return getByValue(level.getLevel() + 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getMaxCredit() {
        return maxCredit;
    }

    public void setLevel(Integer value) {
        this.level = value;
    }
}