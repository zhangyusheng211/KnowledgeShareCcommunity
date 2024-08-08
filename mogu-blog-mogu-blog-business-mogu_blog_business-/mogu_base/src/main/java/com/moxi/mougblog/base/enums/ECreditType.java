package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.QueryException;

/**
 * 获取/消耗积分的枚举类型
 *
 * @author: 陌溪
 * @create: 2021-11-27-19:23
 */

class CreditValue {
    // 积分补偿
    public static final Integer Compensation = 0;
    // 活动奖励
    public static final Integer Activity_Reward = 0;
    // 任务奖励
    public static final Integer Task_Award = 0;
    // 签到
    public static final Integer SignIn = 5;
    // 博客
    public static final Integer Blog = 5;
    // 问答
    public static final Integer Question = 2;
    // 评论
    public static final Integer Comment = 1;
    // 动态
    public static final Integer Moment = 2;
    // 问答
    public static final Integer Problem = 2;

    /**
     * 购买商品
     */
    public static final Integer BUY_PRODUCT = 0;
    /**
     * 赞助
     */
    public static final Integer SPONSOR = 0;
    /**
     * 抽奖
     */
    public static final Integer LUCKY = 0;
    /**
     * 系统变更
     */
    public static final Integer SYSTEM = 0;
}

public enum ECreditType {

    /**
     * 积分补偿
     */
    Compensation("-1", "积分补偿", CreditValue.Compensation, 0),

    /**
     * 活动奖励
     */
    Activity_Reward("0", "活动奖励", CreditValue.Activity_Reward, 0),

    /**
     * 签到
     */
    SignIn("1", "每日签到", CreditValue.SignIn, 1),

    /**
     * 文章
     */
    Blog("2", "创作文章", CreditValue.Blog, 5),

    /**
     * 问答
     */
    Question("3", "发起问答", CreditValue.Question, 5),

    /**
     * 评论
     */
    Comment("4", "参与评论", CreditValue.Comment, 10),

    /**
     * 发表动态
     */
    Moment("5", "发表动态", CreditValue.Moment, 5),

    /**
     * 发表面经
     */
    Problem("6", "发表面经", CreditValue.Problem, 5),

    /**
     * 删除文章
     */
    Delete_Blog("10", "删除文章", -CreditValue.Blog, 10),
    /**
     * 删除问答
     */
    Delete_Question("11", "删除问答", -CreditValue.Question, 10),
    /**
     * 删除评论
     */
    Delete_Comment("12", "删除评论", -CreditValue.Comment, 10),
    /**
     * 删除动态
     */
    Delete_Moment("13", "删除动态", -CreditValue.Moment, 10),
    /**
     * 删除面经
     */
    Delete_Problem("14", "删除面经", -CreditValue.Problem, 10),

    /**
     * 支付相关【可能会扣除积分】
     */
    BUY_RESOURCE("20", "解锁资源", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),
    BUY_ARTICLE("21", "解锁文章", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),
    BUY_MEDIA("22", "解锁课程", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),
    BUY_SUBJECT("23", "解锁专栏", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),
    BUY_VIP("24", "购买会员", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),
    SPONSOR("25", "社区赞助", CreditValue.SPONSOR, Integer.MAX_VALUE),
    CREDITS_LUCKY("26", "积分抽奖", CreditValue.LUCKY, Integer.MAX_VALUE),
    LUCKY_AWARD("27", "抽奖奖励", CreditValue.LUCKY, Integer.MAX_VALUE),
    BUY_PRODUCT("28", "购买商品", CreditValue.BUY_PRODUCT, Integer.MAX_VALUE),

    /**
     * 系统变更
     */
    SYSTEM("99", "系统操作", CreditValue.SYSTEM, Integer.MAX_VALUE),
    /**
     * 任务奖励
     */
    TASK_AWARD("100", "任务奖励", CreditValue.Task_Award, 100);


    private final String code;
    private final String action;
    private final Integer credit;
    private final Integer limit;

    ECreditType(String code, String action, Integer credit, Integer limit) {
        this.code = code;
        this.action = action;
        this.credit = credit;
        this.limit = limit;
    }

    public String getCode() {
        return code;
    }

    public String getAction() {
        return action;
    }

    public Integer getCredit() {
        return credit;
    }

    public Integer getLimit() {
        return limit;
    }

    /**
     * 根据value返回枚举类型，主要在switch中使用 - action->中文描述
     *
     * @param value
     * @return
     */
    public static ECreditType getByValue(String value) {
        for (ECreditType creditType : values()) {
            if (creditType.getAction() == value) {
                return creditType;
            }
        }
        return null;
    }

    /**
     * 根据code返回枚举类型，主要在switch中使用 - code->字符串形式的数字代码
     *
     * @param code
     * @return
     */
    public static ECreditType getByCode(String code) {
        for (ECreditType creditType : values()) {
            if (creditType.getCode().equals(code)) {
                return creditType;
            }
        }
        return null;
    }

    /**
     * 通过资源类型，获取对应的积分变更类型
     * @param resourceType
     * @return
     */
    public static ECreditType getCreditsByResourceType(EResourceType resourceType) {
        switch (resourceType) {
            case BLOG:
                return ECreditType.BUY_ARTICLE;
            case VIP:
                return ECreditType.BUY_VIP;
            case MEDIA:
                return ECreditType.BUY_MEDIA;
            case SUBJECT:
                return ECreditType.BUY_SUBJECT;
            case RESOURCE:
                return ECreditType.BUY_RESOURCE;
            case PRODUCT:
                return ECreditType.BUY_PRODUCT;
            default:
                throw new QueryException("类型转化异常：" + resourceType);
        }
    }
}
