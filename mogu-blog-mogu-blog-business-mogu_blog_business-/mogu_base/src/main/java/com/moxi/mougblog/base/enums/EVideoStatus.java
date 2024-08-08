package com.moxi.mougblog.base.enums;

/**
 * 视频状态枚举
 *
 * @author 陌溪
 * @date 2024年1月7日13:16:48
 */
public enum EVideoStatus {
    READY_CONVERT("1", "待转换"),
    CONVERTING("2", "正在转换"),
    CONVERT_SUCCESS("3", "转换成功"),
    CONVERT_FAIL("4", "转换失败"),

    ;
    private final String code;
    private final String info;

    EVideoStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
