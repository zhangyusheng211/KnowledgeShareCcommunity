package com.moxi.mogublog.picture.entity.QiNiuCallback;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2022-10-30 21:58:2
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Bounding_box {
    private List<List<Integer>> pts;
    private double score;
}