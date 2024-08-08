package com.moxi.mogublog.wechat.form;


import com.moxi.mogublog.utils.JsonUtils;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WxH5OuthrizeForm {
    @NotEmpty(message = "code不得为空")
    private String code;

    @Override
    public String toString() {
        return JsonUtils.objectToJson(this);
    }
}
