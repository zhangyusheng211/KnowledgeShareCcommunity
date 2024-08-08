package com.moxi.mogublog.commons.schema.ChatResponse;

import lombok.Data;

import java.util.List;


/**
 * ChatResponse
 */
@Data
public class ChatResponse {
    private int created;
    private String object;
    private String id;
    private List<Choice> choices;
    private String error;
}