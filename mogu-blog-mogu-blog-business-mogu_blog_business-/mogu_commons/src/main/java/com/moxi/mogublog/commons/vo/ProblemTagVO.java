package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.List;

/**
 * 问题标签表
 *
 * @author 陌溪
 * @date 2022年3月7日21:42:26
 */
@Data
public class ProblemTagVO extends BaseVO<ProblemTagVO> {
    /**
     * 父uid
     */
    private String parentUid;
    /**
     * 标签名
     */
    private String name;
    /**
     * 标签简介
     */
    private String summary;
    /**
     * 标签类型（推荐、热门、语言、知识、岗位、公司、目标）
     */
    private Integer tagType;
    /**
     * 标签级别
     */
    private Integer tagLevel;
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
     * 标签试题数
     */
    private Integer problemCount;

    /**
     * 是否跳转外链
     */
    private String isJumpExternalUrl;
    /**
     * 链接
     */
    private String url;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序字段
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
     * 标签列表
     */
    private List<String> tagUidList;

    /**
     * 问题数大于等于某个值
     */
    private Integer problemCountGe;

}
