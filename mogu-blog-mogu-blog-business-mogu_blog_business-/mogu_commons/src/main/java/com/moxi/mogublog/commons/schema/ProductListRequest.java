package com.moxi.mogublog.commons.schema;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 获取商品列表请求类
 */
@Data
@ToString
public class ProductListRequest {
    /**
     * 资源列表uid
     */
    private List<String> resourceUidList;

    /**
     * 资源类型
     */
    private String resourceType;
}
