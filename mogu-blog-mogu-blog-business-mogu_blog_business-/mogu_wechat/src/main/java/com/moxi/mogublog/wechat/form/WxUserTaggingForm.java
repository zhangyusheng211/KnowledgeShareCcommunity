package com.moxi.mogublog.wechat.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WxUserTaggingForm {
    @NotNull(message = "标签ID不得为空")
    private Long tagid;
}