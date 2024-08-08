/**
  * Copyright 2022 bejson.com 
  */
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
public class QiNiuAuditCallback {
    // 处理任务的persistentID
    private String id;

    // 处理队列名
    private String pipeline;

    // 状态码0成功，1等待处理，2正在处理，3处理失败，4通知提交失败。
    private int code;

    // 与状态码相对应的详细描述
    private String desc;

    // 云处理请求的请求id，主要用于七牛技术人员的问题排查。
    private String reqid;

    // 处理源文件所在的空间名。
    private String inputBucket;

    // 处理源文件的文件名。
    private String inputKey;

    // 每个审核建议
    private List<Items> items;
}