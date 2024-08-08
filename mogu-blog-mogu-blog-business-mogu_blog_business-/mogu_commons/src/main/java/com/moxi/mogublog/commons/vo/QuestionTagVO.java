package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * BlogVO
 *
 * @author: 陌溪
 * @create: 2019年12月4日12:26:36
 */
@Data
public class QuestionTagVO extends BaseVO<QuestionTagVO> {

    /**
     * 父标签Uid
     */
    private String parentUid;

    /**
     * 标签内容
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String name;

    /**
     * 标签简介
     */
    private String summary;

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
     * 无参构造方法，初始化默认值
     */
    QuestionTagVO() {

    }

}
