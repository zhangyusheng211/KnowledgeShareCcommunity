package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 通用分类实体类
 *
 * @author 陌溪
 * @date 2024年6月1日21:42:41
 */
@ToString
@Data
public class CommonCategoryVO extends BaseVO<CommonCategoryVO> {

    // 业务分类：1：商品类别
    private Integer bizType;

    // 分类名称
    private String name;

    // 分类简介
    private String summary;

    // 父uid
    private String parentUid;

    // 分类icon图标
    private String icon;

    // 备注
    private String remark;

    // 排序字段
    private Integer sort;

    // 是否发布(1:是，0:否)
    private String isPublish;
}
