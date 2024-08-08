package com.moxi.mogublog.sms.task.dto;

import com.moxi.mogublog.sms.task.enums.Action;
import lombok.Data;

import java.io.Serializable;

@Data
public class Button implements Serializable {

    /**
     * 按钮文案
     */
    private String text;

    /**
     * 动作
     */
    private Action action;

    /**
     * 点击按钮的消息
     */
    private String message;

    /**
     * 跳转的路由【只支持站内跳转】
     */
    private String router;
}