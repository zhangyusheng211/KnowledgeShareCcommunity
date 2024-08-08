package com.moxi.mogublog.commons.schema.ChatResponse;
import lombok.Data;

@Data
public class Choice {
    private String text;
    private int index;
    private String finish_reason;
    private Message message;
    private boolean block;
}