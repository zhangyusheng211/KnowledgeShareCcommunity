package com.moxi.mogublog.wechat.form;

import com.moxi.mogublog.utils.JsonUtils;
import lombok.Data;

@Data
public class AccountBindForm {
    String phoneNum;
    String idCodeSuffix;

    @Override
    public String toString() {
        return JsonUtils.objectToJson(this);
    }
}
