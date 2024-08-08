package com.moxi.mogublog.commons.domainEvent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 领域事件实体类型
 */
public enum EntityType {

    BLOG("BLOG", "博客"),
    QUESTION("QUESTION", "问答"),
    MEDIA("MEDIA", "课程"),
    MOMENT("MOMENT", "动态"),
    COMMENT("COMMENT", "评论"),
    PROBLEM("PROBLEM", "面经"),
    FILE("FILE", "文件"),
    SIGN_IN("SIGN_IN", "签到"),
    PRAISE("PRAISE", "点赞"),
    COLLECT("COLLECT", "收藏"),
    EMOTICON("EMOTICON", "表情包"),
    GENERAL_EDIT("GENERATE_EDIT", "勘误"),
    NOTICE("NOTICE", "通知"),
    WATCH("WATCH", "关注"),
    CHAT("CHAT", "聊天"),
    REPORT("REPORT", "举报"),
    RESOURCE("RESOURCE", "资源"),
    MESSAGE_PUSH("MESSAGE_PUSH", "消息触达"),
    ORDER("ORDER", "订单"),
    CREDITS("CREDITS", "积分"),
    LUCKY("LUCKY", "积分抽奖"),
    WITHDRAW("WITHDRAW", "用户提现"),
    SUBJECT("SUBJECT", "专栏"),
    SUBSCRIBE("SUBSCRIBE", "订阅"),
    ;
    private final String type;
    private final String name;

    EntityType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
