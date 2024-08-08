package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 勋章分类表VO
 *
 * @author 陌溪
 * @date 2022年12月28日09:00:30
 */
@Data
public class MedalClassifyVO extends BaseVO<MedalClassifyVO> {

    /**
     * 父uid
     */
    private String parentUid;

    /**
     * 勋章分类名称
     */
    private String name;

    /**
     * 勋章分类简介
     */
    private String summary;

    /**
     * 类型【创作，活跃，成就，限定】
     */
    private Integer type;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;


    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 需要查询勋章的用户id
     */
    private String userUid;

}
