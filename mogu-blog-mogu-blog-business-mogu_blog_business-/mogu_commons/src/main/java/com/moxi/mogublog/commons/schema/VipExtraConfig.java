package com.moxi.mogublog.commons.schema;

import com.moxi.mougblog.base.enums.EUserTag;
import lombok.Data;

import java.util.List;

/**
 * 额外扩展配置
 */
@Data
public class VipExtraConfig {

    /**
     * 渐变背景色
     */
    private String backgroundImage;

    /**
     * 背景色
     */
    private String backgroundColor;

    /**
     * 边框颜色
     */
    private String borderColor;

    /**
     * 字体颜色
     */
    private String textColor;

    /**
     * 权益字体颜色
     */
    private String equityTextColor;

    /**
     * 按钮颜色
     */
    private String buttonBackgroundColor;
    /**
     * 按钮字体颜色
     */
    private String buttonTextColor;

    /**
     * 权益列表
     */
    private List<EquityItem> equityList;
}


/**
 * 权益列表
 */
@Data
class EquityItem {
    /**
     * 权益名称
     */
    private String name;

    /**
     * 权益值
     */
    private String value;
}