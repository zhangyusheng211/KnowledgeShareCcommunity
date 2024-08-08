package com.moxi.mogublog.sms.task.enums;


import com.moxi.mougblog.base.exception.exceptionType.BusinessException;

/**
 * 活动枚举
 */
public enum MarketNameEnum {
    EMPTY("", ""),
    TASK("task", "任务"),
    ACHIEVE("achieve", "成就"),
    ;

    private String code;
    private String name;

    MarketNameEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MarketNameEnum getMarketNameEnum(String marketName) {
        MarketNameEnum[] marketNameEnums = MarketNameEnum.values();
        for (MarketNameEnum nameEnum : marketNameEnums) {
            if (nameEnum.getCode().equals(marketName)) {
                return nameEnum;
            }
        }
        throw new BusinessException(String.format("活动id=%s不存在", marketName));
    }

    public static void main(String[] args) {
        MarketNameEnum sign = getMarketNameEnum("sign");
        System.out.println("sign = " + sign);
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
