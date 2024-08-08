package com.moxi.mogublog.wechat.form;

import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.wechat.common.exception.RRException;
import lombok.Data;

@Data
public class TemplateMsgForm {
    private String openid;
    private String msg;
    private String template;

    @Override
    public String toString() {
        return JsonUtils.objectToJson(this);
    }

    public boolean isValid() {
        if (openid == null || openid.isEmpty() || msg == null || msg.isEmpty() || template == null || template.isEmpty()) {
            throw new RRException("缺少必要参数");
        }
        return true;
    }
}
