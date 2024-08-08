package com.moxi.mougblog.base.enums;

/**
 * 奖励类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年7月28日22:10:45
 */
public enum EAwardType {

    /**
     * 谢谢参与
     */
    Thanks(1, "谢谢参与"),

    /**
     * VIP体验卡
     */
    VIP(2, "VIP体验卡"),

    /**
     * 签到卡
     */
    SignCard(3, "签到卡"),

    /**
     * 现金红包
     */
    RedPacket(4, "现金红包"),

    /**
     * 积分
     */
    Credits(5, "积分"),

    /**
     * 实体物品
     */
    Entity(6, "实物");


    private final int type;
    private final String name;

    EAwardType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    /**
     * 根据type返回枚举类型，主要在switch中使用
     *
     * @param type
     * @return
     */
    public static EAwardType getEAwardTypeByType(int type) {
        for (EAwardType awardType : values()) {
            if (awardType.type == type) {
                return awardType;
            }
        }
        return null;
    }
}