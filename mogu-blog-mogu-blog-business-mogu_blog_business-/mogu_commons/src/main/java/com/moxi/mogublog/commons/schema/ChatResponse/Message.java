package com.moxi.mogublog.commons.schema.ChatResponse;

import lombok.Data;

@Data
public class Message {
    // 在实际使用中，可能需要根据具体的消息信息定义更多的字段
    private String content;
    private String role;
}