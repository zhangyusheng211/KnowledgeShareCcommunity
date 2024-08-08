package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * MediaCategoryVO
 *
 * @author thh
 * @date 2021-12-14
 */
@ToString
@Data
public class MediaCategoryVO extends BaseVO<MediaCategoryVO> {

    /**
     * 分类内容
     */
    private String name;

    /**
     * 分类简介
     */
    private String content;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 创建人
     */
    private String createByUid;

    /**
     * 更新人
     */
    private String updateByUid;

    /**
     * 备注
     */
    private String remark;
}
