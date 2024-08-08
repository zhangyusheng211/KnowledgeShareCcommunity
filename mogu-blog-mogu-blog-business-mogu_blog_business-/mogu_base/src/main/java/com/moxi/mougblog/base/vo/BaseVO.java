package com.moxi.mougblog.base.vo;

import com.moxi.mougblog.base.validator.annotion.IdValid;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.Update;
import lombok.Data;

/**
 * BaseVO   view object 表现层 基类对象
 *
 * @author: 陌溪
 * @create: 2019-12-03-22:38
 */
@Data
public class BaseVO<T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    @IdValid(groups = {Update.class, Delete.class})
    private String uid;

    private Integer status;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;
}
