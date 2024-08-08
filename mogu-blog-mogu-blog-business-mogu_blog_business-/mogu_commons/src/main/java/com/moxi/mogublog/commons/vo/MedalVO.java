package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 勋章表VO
 *
 * @author 陌溪
 * @date 2022年12月27日09:10:09
 */
@Data
public class MedalVO extends BaseVO<MedalVO> {

    /**
     * 勋章文件url
     */
    private String fileUrl;

    /**
     * 勋章分类uid
     */
    private String medalClassifyUid;

    /**
     * 勋章名称
     */
    private String name;

    /**
     * 勋章简介
     */
    private String summary;

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
