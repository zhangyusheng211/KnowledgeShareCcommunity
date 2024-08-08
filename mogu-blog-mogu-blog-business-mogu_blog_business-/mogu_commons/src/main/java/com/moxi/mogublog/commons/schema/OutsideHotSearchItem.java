package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * 外部热搜Item结构体
 */
@Data
public class OutsideHotSearchItem {

    private Integer rank;

    private String keyword;

    private String url;

    private String summary;

    private String image;

    private String type;

    private Long hotValue;

    private String hotRankScore;

    private String pcHotRankScore;

    private Boolean isHot;

    private Boolean isNew;

    private Boolean isBoil;

}
