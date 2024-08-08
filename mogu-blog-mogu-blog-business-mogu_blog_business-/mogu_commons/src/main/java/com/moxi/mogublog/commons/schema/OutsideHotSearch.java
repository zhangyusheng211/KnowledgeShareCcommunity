package com.moxi.mogublog.commons.schema;

import lombok.Data;

import java.util.List;

/**
 * 外部热搜结构体
 */
@Data
public class OutsideHotSearch {

    private Boolean success;

    private Integer code;

    private String msg;

    private Integer cost;

    private List<OutsideHotSearchItem> data;
}
