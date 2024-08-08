package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * 通用修改表（面经答案修改）
 *
 * @author 陌溪
 * @date 2022年4月12日21:42:26
 */
@Data
public class GeneralEditVO extends BaseVO<GeneralEditVO> {

    /**
     * 唯一oid，自动递增
     */
    private Integer oid;
    /**
     * 用户id
     */
    private String userUid;
    /**
     * 博客、问答、媒资、动态、评论、问题
     */
    private String bizType;
    /**
     * 博客/面经id
     */
    private String bizUid;
    /**
     * 修改原因
     */
    private String reason;

    /**
     * 题目
     */
    private String summary;
    /**
     * 修改前内容
     */
    private String oldContent;
    /**
     * 修改内容
     */
    private String content;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 管理员uid
     */
    private String adminUid;
    /**
     * 是否通过审核
     */
    private String auditStatus;
    /**
     * 审批拒绝原因
     */
    private String rejectReason;
    /**
     * 审核人
     */
    private String auditName;
    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
