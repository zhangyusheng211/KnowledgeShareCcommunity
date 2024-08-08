package com.moxi.mogublog.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 万历年信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayVO {

    private String name;
    private String date;
    private String isOffDay;

}
