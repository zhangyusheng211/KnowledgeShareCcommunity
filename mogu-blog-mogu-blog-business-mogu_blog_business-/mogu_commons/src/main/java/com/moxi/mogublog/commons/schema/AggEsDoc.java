package com.moxi.mogublog.commons.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.entity.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AggEsDoc 聚合Es文档实体，汇聚文章、问答、动态、评论
 */
@Data
public class AggEsDoc {

    @Id
    private String id;

    private String uid;

    private Integer oid;

    /**
     * 资源类型: 文章、动态、问答、面经
     */
    private String resourceType;

    private String title;

    private String summary;

    private String content;

    private String sortName;

    private String sortUid;

    private String isPublish;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 作者
     */
    private String author;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 用户uid
     */
    private String userUid;


    private String photoUrl;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签id
     */
    private String tagUid;
}
