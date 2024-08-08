package com.moxi.mogublog.sms.task.enums;

/**
 * 奖励标识
 *
 * @date 2022年12月24日16:08:37
 */
public enum AwardType {

    /**
     * 积分
     */
    CREDITS("credits", "积分"),

    /**
     * 签到卡
     */
    SIGN_IN_CARD("signInCard", "签到卡"),

    /**
     * 会员
     */
    VIP("vip", "会员"),

    MEDAL("medal", "勋章"),

    ;
    private final String type;
    private final String name;

    AwardType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static AwardType getByType(String type) {
        for (AwardType awardType : values()) {
            if (awardType.getType().equals(type)) {
                return awardType;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}