package com.moxi.mogublog.sms.task.enums;

import com.alibaba.fastjson.JSONObject;

public enum TextType {
    text,
    json {
        @Override
        public Object format(String s) {
            return JSONObject.parse(s);
        }
    },
    html,
    ;

    public Object format(String s) {
        return s;
    }
}