package com.moxi.mougblog.base.enums;

/**
 * 审批状态枚举类
 *
 * @author 陌溪
 * @date 2021年11月29日23:25:50
 */
public class EAuditStatus {
    /**
     * 等待审批
     */
    public static final String WAIT = "0";

    /**
     * 审批拒绝
     */
    public static final String REJECT = "1";

    /**
     * 审批通过
     */
    public static final String AGREE = "2";
}
