package com.moxi.mogublog.commons.domainEvent;

/**
 * 领域事件实体类型
 */
public enum EventAction {

    // 博客相关
    BLOG_ADD("BLOG_ADD", "添加博客", EntityType.BLOG),
    BLOG_EDIT("BLOG_EDIT", "编辑博客", EntityType.BLOG),
    BLOG_DELETE("BLOG_DELETE", "删除博客", EntityType.BLOG),
    BLOG_AUDIT("BLOG_AUDIT", "审核博客", EntityType.BLOG),
    BLOG_PUBLISH("BLOG_PUBLISH", "发布博客", EntityType.BLOG),
    BLOG_VISIT("BLOG_VISIT", "博客访问", EntityType.BLOG),

    // 问答相关
    QUESTION_ADD("QUESTION_ADD", "添加问答", EntityType.QUESTION),
    QUESTION_EDIT("QUESTION_EDIT", "编辑问答", EntityType.QUESTION),
    QUESTION_DELETE("QUESTION_DELETE", "删除问答", EntityType.QUESTION),
    QUESTION_AUDIT("QUESTION_AUDIT", "审核问答", EntityType.QUESTION),
    QUESTION_PUBLISH("QUESTION_PUBLISH", "发布问答", EntityType.QUESTION),

    // 课程相关
    MEDIA_ADD("MEDIA_ADD", "添加课程", EntityType.MEDIA),
    MEDIA_EDIT("MEDIA_EDIT", "编辑课程", EntityType.MEDIA),
    MEDIA_DELETE("MEDIA_DELETE", "删除课程", EntityType.MEDIA),
    MEDIA_AUDIT("MEDIA_AUDIT", "审核课程", EntityType.MEDIA),
    MEDIA_PUBLISH("MEDIA_PUBLISH", "发布课程", EntityType.MEDIA),

    // 动态相关
    MOMENT_ADD("MOMENT_ADD", "添加动态", EntityType.MOMENT),
    MOMENT_EDIT("MOMENT_EDIT", "编辑动态", EntityType.MOMENT),
    MOMENT_DELETE("MOMENT_DELETE", "删除动态", EntityType.MOMENT),
    MOMENT_AUDIT("MOMENT_DELETE", "删除动态", EntityType.MOMENT),
    MOMENT_PUBLISH("MOMENT_PUBLISH", "发布动态", EntityType.MOMENT),

    // 评论相关
    COMMENT_ADD("COMMENT_ADD", "添加评论", EntityType.COMMENT),
    COMMENT_EDIT("COMMENT_EDIT", "编辑评论", EntityType.COMMENT),
    COMMENT_DELETE("COMMENT_DELETE", "删除评论", EntityType.COMMENT),
    COMMENT_AUDIT("COMMENT_AUDIT", "审核评论", EntityType.COMMENT),
    COMMENT_PUBLISH("COMMENT_PUBLISH", "发布评论", EntityType.COMMENT),

    // 问题相关
    PROBLEM_ADD("PROBLEM_ADD", "添加问题", EntityType.PROBLEM),
    PROBLEM_EDIT("PROBLEM_EDIT", "编辑问题", EntityType.PROBLEM),
    PROBLEM_DELETE("PROBLEM_DELETE", "删除问题", EntityType.PROBLEM),
    PROBLEM_AUDIT("PROBLEM_AUDIT", "审核问题", EntityType.PROBLEM),
    PROBLEM_PUBLISH("PROBLEM_PUBLISH", "发布问题", EntityType.PROBLEM),
    PROBLEM_HOLD("PROBLEM_HOLD", "掌握问题", EntityType.PROBLEM),
    PROBLEM_VISIT("PROBLEM_VISIT", "访问问题", EntityType.PROBLEM),

    // 文件相关
    FILE_ADD("FILE_ADD", "添加文件", EntityType.FILE),
    FILE_EDIT("FILE_EDIT", "编辑文件", EntityType.FILE),
    FILE_DELETE("FILE_DELETE", "删除文件", EntityType.FILE),
    FILE_PUBLISH("FILE_PUBLISH", "发布文件", EntityType.FILE),

    // 签到相关
    SIGN_ADD("SIGN_ADD", "用户签到", EntityType.SIGN_IN),
    RETROACTIVE_ADD("RETROACTIVE_ADD", "用户补签", EntityType.SIGN_IN),

    // 点赞相关
    PRAISE_ADD("PRAISE_ADD", "新增点赞", EntityType.PRAISE),
    PRAISE_CANCEL("PRAISE_CANCEL", "取消点赞/踩", EntityType.PRAISE),
    TREAD_ADD("TREAD_ADD", "新增点踩", EntityType.PRAISE),
    TREAD_CANCEL("TREAD_CANCEL", "取消点踩", EntityType.PRAISE),

    // 收藏相关
    COLLECT_ADD("COLLECT_ADD", "新增收藏", EntityType.COLLECT),
    COLLECT_CANCEL("COLLECT_CANCEL", "取消收藏", EntityType.COLLECT),

    // 表情包相关
    EMOTICON_ADD("EMOTICON_ADD", "新增表情包", EntityType.EMOTICON),
    EMOTICON_EDIT("EMOTICON_ADD", "编辑表情包", EntityType.EMOTICON),
    EMOTICON_STICKY("EMOTICON_STICKY", "置顶表情包", EntityType.EMOTICON),
    EMOTICON_DELETE("EMOTICON_DELETE", "删除表情包", EntityType.EMOTICON),

    // 勘误相关
    GENERAL_EDIT_ADD("GENERAL_EDIT_ADD", "新增勘误", EntityType.GENERAL_EDIT),
    GENERAL_EDIT_AUDIT("GENERAL_EDIT_AUDIT", "审核勘误", EntityType.GENERAL_EDIT),
    GENERAL_EDIT_DELETE("GENERAL_EDIT_DELETE", "删除勘误", EntityType.GENERAL_EDIT),

    // 通知相关
    NOTICE_VIEW("NOTICE_VIEW", "阅读通知", EntityType.NOTICE),
    NOTICE_DELETE("NOTICE_DELETE", "删除通知", EntityType.NOTICE),

    // 关注相关
    WATCH_ADD("WATCH_ADD", "增加关注", EntityType.WATCH),
    WATCH_CANCEL("WATCH_CANCEL", "取消关注", EntityType.WATCH),

    // 举报相关
    REPORT_ADD("REPORT_ADD", "添加举报", EntityType.REPORT),
    REPORT_HANDLE("REPORT_HANDLE", "处理举报", EntityType.REPORT),
    REPORT_DELETE("REPORT_DELETE", "删除举报", EntityType.REPORT),

    // 发布聊天
    CHAT_ADD("CHAT_ADD", "发布聊天", EntityType.CHAT),
    CHAT_CANCEL("CHAT_CANCEL", "撤回聊天", EntityType.WATCH),

    // 资源相关
    RESOURCE_ADD("RESOURCE_ADD", "新增资源", EntityType.RESOURCE),
    RESOURCE_EDIT("RESOURCE_EDIT", "编辑资源", EntityType.RESOURCE),
    RESOURCE_DELETE("RESOURCE_DELETE", "删除资源", EntityType.RESOURCE),
    RESOURCE_VISIT("RESOURCE_VISIT", "访问资源", EntityType.RESOURCE),
    RESOURCE_DOWNLOAD("RESOURCE_DOWNLOAD", "下载资源", EntityType.RESOURCE),

    // 消息触达相关
    MESSAGE_PUSH_ADD("MESSAGE_PUSH_ADD", "添加触达", EntityType.MESSAGE_PUSH),
    MESSAGE_PUSH_EDIT("MESSAGE_PUSH_EDIT", "编辑触达", EntityType.MESSAGE_PUSH),
    MESSAGE_PUSH_DELETE("MESSAGE_PUSH_DELETE", "删除触达", EntityType.MESSAGE_PUSH),
    MESSAGE_PUSH("MESSAGE_PUSH", "消息推送", EntityType.MESSAGE_PUSH),


    // 订单支付成功
    ORDER_CREATE_SUCCESS("ORDER_CREATE_SUCCESS", "订单创建成功", EntityType.ORDER),
    ORDER_PAY_SUCCESS("ORDER_PAY_SUCCESS", "订单支付成功", EntityType.ORDER),

    // 积分变更事件
    CREDITS_CHANGE("CREDITS_CHANGE", "积分变更", EntityType.CREDITS),

    // 积分抽奖
    LUCKY("LUCKY", "积分抽奖", EntityType.LUCKY),

    // 用户提现相关
    ADD_WITHDRAW("ADD_WITHDRAW", "新增提现", EntityType.WITHDRAW),
    AUDIT_WITHDRAW("AUDIT_WITHDRAW", "审核提现", EntityType.WITHDRAW),

    // 专栏相关
    SUBJECT_ADD("SUBJECT_ADD", "新增专栏", EntityType.SUBJECT),
    SUBJECT_EDIT("SUBJECT_EDIT", "编辑专栏", EntityType.SUBJECT),
    SUBJECT_DELETE("SUBJECT_DELETE", "删除专栏", EntityType.SUBJECT),
    SUBJECT_VISIT("SUBJECT_VISIT", "访问专栏", EntityType.SUBJECT),

    // 用户订阅相关
    SUBSCRIBE_ADD("SUBSCRIBE_ADD", "新增订阅", EntityType.SUBSCRIBE),
    SUBSCRIBE_DELETE("SUBSCRIBE_DELETE", "取消订阅", EntityType.SUBSCRIBE),

    ;

    private final String actionName;
    private final String describe;
    private final EntityType entityType;

    EventAction(String actionName, String describe, EntityType entityType) {
        this.actionName = actionName;
        this.describe = describe;
        this.entityType = entityType;
    }

    public String getActionName() {
        return actionName;
    }

    public String getDescribe() {
        return describe;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
