package com.moxi.mougblog.base.enums;

/**
 * 提现状态枚举类
 *
 * @Author: 陌溪
 * @Date: 2023年8月5日10:48:08
 */
public enum EWithdrawStatus {

    /**
     * 提现中
     */
    Init(1, "提现中"),

    /**
     * 提现失败
     */
    Fail(2, "提现失败"),

    /**
     * 提现完成
     */
    Finish(3, "提现完成"),

    ;


    private final int status;
    private final String name;

    EWithdrawStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}