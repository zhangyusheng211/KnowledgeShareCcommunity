package com.moxi.mougblog.base.enums;

/**
 * 用户权益枚举类
 *
 * @author: 陌溪
 * @date: 2021年8月8日15:26:43
 */
public enum EquityType {

    /**
     * VIP特权
     */
    VIP("1", "VIP特权"),

    /**
     * 签到卡
     */
    SIGN_IN_CARD("2", "签到卡"),

    /**
     * 勋章
     */
    MEDAL("3", "勋章"),

    /**
     * 谢谢参与
     */
    Thanks("4", "谢谢参与"),

    /**
     * 现金红包
     */
    RedPacket("5", "现金红包"),

    /**
     * 积分
     */
    Credits("6", "积分"),

    /**
     * 实体物品
     */
    Entity("7", "实体物品"),

    ;


    private final String type;
    private final String name;

    EquityType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
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
    public static EquityType getEquityTypeByType(String type) {
        for (EquityType equityType : values()) {
            if (equityType.type.equals(type)) {
                return equityType;
            }
        }
        return null;
    }
}