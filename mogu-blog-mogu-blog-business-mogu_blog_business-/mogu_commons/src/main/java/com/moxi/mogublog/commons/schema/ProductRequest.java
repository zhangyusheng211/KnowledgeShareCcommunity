package com.moxi.mogublog.commons.schema;

import lombok.Data;
import lombok.ToString;

/**
 * 获取商品请求类
 */
@Data
@ToString
public class ProductRequest {
    /**
     * 资源uid
     */
    private String resourceUid;

    /**
     * 资源类型
     */
    private String resourceType;
}
