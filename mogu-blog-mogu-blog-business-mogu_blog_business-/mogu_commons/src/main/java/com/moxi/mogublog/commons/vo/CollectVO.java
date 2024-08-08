package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * CollectVO
 *
 * @author: 蓝毛怪
 * @create: 2021年12月5日9:56:08
 */
@Data
public class CollectVO extends BaseVO<CollectVO> {

    /**
     * 收藏人UID
     */
    private String userUid;

    /**
     * 被收藏用户uid
     */
    private String toUserUid;

    /**
     * 博客UID（收藏东西的UID）
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String bizUid;

    /**
     * 收藏类型
     */
    private String collectType;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;
}
