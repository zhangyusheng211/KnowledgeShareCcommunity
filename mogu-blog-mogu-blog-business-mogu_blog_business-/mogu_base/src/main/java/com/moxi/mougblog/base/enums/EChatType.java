package com.moxi.mougblog.base.enums;

/**
 * 聊天类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2022年3月20日19:07:13
 */
public enum EChatType {

    /**
     * 表情
     */
    Emoji("1", "表情"),

    /**
     * 语音
     */
    Record("2", "语音"),

    /**
     * 图片
     */
    Picture("3", "图片"),

    /**
     * 通话
     */
    Call("4", "通话"),

    /**
     * 视频
     */
    Vedio("5", "视频");


    private final String code;
    private final String name;

    EChatType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}