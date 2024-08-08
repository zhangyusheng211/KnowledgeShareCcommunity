package com.moxi.mogublog.commons.schema.ChatResponse;

import lombok.Data;

import java.util.List;


/**
 * ChatResponse
 */
@Data
public class ChatGPTSetting {

    /**
     * 是否开启机器人自动回复
     */
    private boolean openRobotReply;


    /**
     * 普通用户回复数
     */
    private int replyCount;

    /**
     * vip用户回复数
     */
    private int vipReplyCount;

    /**
     * 限制次数
     */
    private int limitCount;

    /**
     * 是否阻塞
     */
    private boolean isBlock;

    /**
     * 阻塞文案
     */
    private String blockMessage;
}