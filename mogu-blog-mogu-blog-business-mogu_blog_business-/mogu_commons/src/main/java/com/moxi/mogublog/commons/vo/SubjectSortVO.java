package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 专题分类VO
 *
 * @author: 陌溪
 * @create: 2022年9月25日22:06:56
 */
@Data
public class SubjectSortVO extends BaseVO<SubjectSortVO> {

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类简介
     */
    private String summary;

    /**
     * 是否精选
     */
    private String isSelection;
    /**
     * 是否上架
     */
    private String isPublish;
    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 分类图
     */
    private String fileUid;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 系统预设【0 否，1 是】
     */
    private Integer systemPreinstall;

    /**
     * 预设配置
     */
    private String preinstallConfig;
}
