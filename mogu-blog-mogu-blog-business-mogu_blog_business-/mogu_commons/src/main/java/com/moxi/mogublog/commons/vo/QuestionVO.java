package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * BlogVO
 *
 * @author: 陌溪
 * @create: 2019年12月4日12:26:36
 */
@Data
public class QuestionVO extends BaseVO<QuestionVO> {

    /**
     * 唯一oid【自动递增】
     */
    private Integer oid;

    /**
     * 问答标题
     */
    private String title;

    /**
     * 问答简介
     */
    private String summary;

    /**
     * 问答内容
     */
    private String content;

    /**
     * 问答标签uid
     */
    private String questionTagUid;

    /**
     * 问答模板UID
     */
    private String questionTemplateUid;

    /**
     * 问答点击数
     */
    private Integer clickCount;

    /**
     * 问答收藏数
     */
    private Integer collectCount;

    /**
     * 回答次数
     */
    private Integer replyCount;

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 是否原创
     */
    private String isOriginal;

    /**
     * 问答来源【0 后台添加，1 用户投稿】
     */
    private String questionSource;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 问答状态，0:创建，1:进行，2:已采纳
     */
    private int questionStatus;

    /**
     * 审核状态【0：未审核，1：审核拒绝，2：审核通过】
     */
    private String auditStatus;

    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
     * 审核人名称
     */
    private String auditName;

    /**
     * 审批拒绝原因
     */
    private String rejectReason;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 方法类型【用于区分 最新问答，最热问答 未回复问答】
     */
    private String methodType;

    /**
     * 是否开启懒加载
     */
    private String isLazy;

    /**
     * 是否前台请求 【0 前台 ，1 后台】
     */
    private Integer isBlack;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 无参构造方法，初始化默认值
     */
    public QuestionVO() {
        // 默认不开启懒加载
        this.isLazy = "0";
    }

}
