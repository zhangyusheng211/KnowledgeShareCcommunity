package com.moxi.mougblog.base.enums;

/**
 * 通知动作类型枚举类  z网站通知
 *
 * @author: 陌溪
 * @date: 2021年8月8日15:26:43
 */
public enum EBusinessType {

    /**
     * 博客评论
     */
    BLOG_COMMENT(1, "博客评论"),

    /**
     * 问答评论
     */
    QUESTION_COMMENT(2, "问答评论"),

    /**
     * 博客回复
     */
    BLOG_REPLY(3, "博客回复"),

    /**
     * 问答回复
     */
    QUESTION_REPLY(4, "问答回复"),

    /**
     * 用户关注
     */
    USER_WATCH(5, "用户关注"),

    /**
     * 博客点赞
     */
    BLOG_PRAISE(6, "博客点赞"),

    /**
     * 问答点赞
     */
    QUESTION_PRAISE(7, "问答点赞"),

    /**
     * 博客
     */
    BLOG(8, "博客"),

    /**
     * 问答
     */
    QUESTION(9, "问答"),

    /**
     * 留言板回复
     */
    MESSAGE_BOARD_REPLY(10, "留言板回复"),

    /**
     * 关于我回复
     */
    ABOUT_ME_REPLY(11, "关于我回复"),

    /**
     * 用户动态评论
     */
    USER_MOMENT_COMMENT(12, "用户动态评论"),

    /**
     * 用户动态回复
     */
    USER_MOMENT_REPLY(13, "用户动态回复"),

    /**
     * 用户动态点赞
     */
    MOMENT_PRAISE(14, "用户动态点赞"),

    /**
     * 用户媒资回复
     */
    MEDIA_REPLY(15, "课程回复"),

    /**
     * 用户媒资点赞
     */
    MEDIA_PRAISE(16, "课程点赞"),

    /**
     * 评论点赞
     */
    COMMENT_PRAISE(17, "评论点赞"),

    RESOURCE_PRAISE(18, "资源点赞"),

    /**
     * 点赞实体类
     */
    PRAISE(20, "点赞"),

    /**
     * 收藏
     */
    COLLECT(21, "收藏"),

    /**
     * 点击动态
     */
    MOMENT(22, "动态"),

    /**
     * 问题
     */
    PROBLEM(23, "点击问题"),

    /**
     * 问题点赞
     */
    PROBLEM_PRAISE(24, "问题点赞"),

    /**
     * 问题回复
     */
    PROBLEM_REPLY(25, "问题回复"),

    /**
     * 问题回复
     */
    PROBLEM_COMMENT(26, "问题评论"),


    BLOG_COLLECT(27, "文章收藏"),
    QUESTION_COLLECT(28, "问答收藏"),
    PROBLEM_COLLECT(29, "面经收藏"),
    COMMENT_COLLECT(30, "评论收藏"),
    MOMENT_COLLECT(31, "动态收藏"),
    MEDIA_COLLECT(32, "课程收藏"),
    RESOURCE_COLLECT(33, "资源收藏"),

    BLOG_AUDIT(40, "文章审核"),
    QUESTION_AUDIT(41, "问答审核"),
    PROBLEM_AUDIT(42, "面经审核"),
    COMMENT_AUDIT(43, "评论审核"),
    MOMENT_AUDIT(44, "动态审核"),
    MEDIA_AUDIT(45, "课程审核"),
    RESOURCE_AUDIT(46, "资源审核"),

    /**
     * 获得签到卡
     */
    SEND_SIGN_IN_CARD(50, "获得签到卡"),

    /**
     * 友情链接
     */
    FRIENDLY_LINK(51, "友情链接"),

    /**
     * 用户反馈
     */
    FEED_BACK(52, "用户反馈"),

    /**
     * 勘误
     */
    GENERAL_EDIT(53, "勘误"),

    /**
     * 点踩
     */
    TREAD(54, "点踩"),
    COMMENT(55, "评论"),

    /**
     * 被AT
     */

    BLOG_AT_USER(60, "文章AT"),
    QUESTION_AT_USER(61, "问答AT"),
    PROBLEM_AT_USER(62, "面经AT"),
    COMMENT_AT_USER(63, "评论AT"),
    MOMENT_AT_USER(64, "动态AT"),
    CHAT_AT_USER(65, "聊天AT"),

    // 提现相关
    SUB_ACCOUNT(66, "资金分账"),
    WITHDRAW(67, "用户提现"),
    WITHDRAW_FAIL(68, "提现失败"),
    LUCKY(69, "转盘抽奖"),
    WITHDRAW_SUCCESS(70, "提现成功"),

    // 订阅相关
    SUBJECT_SUBSCRIBE(71, "专栏订阅"),

    ;


    private final Integer code;
    private final String name;

    EBusinessType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    // z返回code对应的通知类型
    public static EBusinessType getType(String resource) {
        for (EBusinessType type : EBusinessType.values()) {
            if (type.getCode().equals(Integer.valueOf(resource))) {
                return type;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
