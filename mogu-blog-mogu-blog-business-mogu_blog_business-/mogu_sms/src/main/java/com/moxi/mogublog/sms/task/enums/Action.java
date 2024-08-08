package com.moxi.mogublog.sms.task.enums;

public enum Action {
    // 点击事件
    CLICK,

    BLOG,  // 发表博客

    TO_BLOG_VISIT, // 博客被访问

    BLOG_VISIT, // 主动访问博客

    QUESTION, // 发表问答

    QUESTION_VISIT, // 问答访问

    PROBLEM, // 发表面经

    PROBLEM_VISIT, // 面经访问

    TO_PROBLEM_VISIT, // 面经被访问

    RESOURCE, // 上传资源

    RESOURCE_VISIT, // 资源访问

    TO_RESOURCE_VISIT, // 资源被访问

    SIGN_IN,  // 连续签到

    MOMENT,  // 发表动态

    COMMENT, // 发表评论

    TO_COMMENT, // 被评论

    PRAISE,  // 点赞

    TO_PRAISE, // 被点赞

    COLLECT, // 收藏

    TO_COLLECT, // 被收藏

    FILE, // 上传文件

    REPORT, // 举报

    BE_REPORT, // 被举报

    WATCH, // 关注

    TO_WATCH, // 被关注

    AWARD, // 奖励

    CHAT, // 聊天

    RESOURCE_DOWNLOAD, // 资源下载

    // 订单支付金额
    SPONSOR_FEE,

    // 订单支付次数
    SPONSOR_COUNT,

    CREDITS, // 积分

    LUCKY, // 抽奖

}