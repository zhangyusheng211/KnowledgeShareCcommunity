package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * ResourceSortVO
 *
 * @author: 陌溪
 * @create: 2020年1月9日19:09:00
 */
@ToString
@Data
public class EmoticonVO extends BaseVO<EmoticonVO> {

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 表情包文件uid
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String fileUid;

    /**
     * 是否精选
     */
    private String isSelection;

    /**
     * 点击数
     */
    private Integer clickCount;

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
     * 依据排序
     */
    private String orderBy;

    /**
     * 无参构造方法
     */
    EmoticonVO() {

    }

}
