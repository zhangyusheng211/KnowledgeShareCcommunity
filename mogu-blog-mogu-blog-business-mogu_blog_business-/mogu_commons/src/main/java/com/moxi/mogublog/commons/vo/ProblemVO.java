package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 问题表
 *
 * @author 陌溪
 * @date 2022年3月7日21:42:26
 */
@Data
public class ProblemVO extends BaseVO<ProblemVO> {

    /**
     * 唯一oid，自动递增
     */
    private Integer oid;
    /**
     * 标题
     */
    private String title;
    /**
     * 简介
     */
    private String summary;
    /**
     * 内容
     */
    private String content;
    /**
     * 答案
     */
    private String answer;
    /**
     * 问题难度(0：默认，1: 简单，2：中等，3：困难)
     */
    private Integer problemDifficulty;
    /**
     * 问题类型(0：默认, 1: 简答题，2：选择题，3：不定项，4: 填空，5：编程题)
     */
    private Integer problemType;
    /**
     * 点击数
     */
    private Integer clickCount;
    /**
     * 问题标签uid
     */
    private String problemTagUid;
    /**
     * 收藏数
     */
    private Integer collectCount;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 通过数
     */
    private Integer passCount;
    /**
     * 未通过数
     */
    private Integer noPassCount;
    /**
     * 面试中出现的次数
     */
    private Integer visitCount;
    /**
     * 点赞数
     */
    private Integer praiseCount;
    /**
     * 点赞数
     */
    private Integer treadCount;
    /**
     * 文件uid
     */
    private String fileUid;
    /**
     * 用户uid
     */
    private String userUid;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 是否开启评论
     */
    private String openComment;
    /**
     * 问题来源
     */
    private String source;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 是否精选
     */
    private String isSelection;
    /**
     * 是否发布
     */
    private String isPublish;
    /**
     * 发布时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;
    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 是否显示答案
     */
    private String isShowAnswer;

    /**
     * 是否有答案
     */
    private String hasAnswer;

    /**
     * 转换后的tagUidList
     */
    private String[] tagUidList;

    /**
     * 掌握状态
     */
    private String degreeStatus;

    /**
     * 是否开启懒加载
     */
    private String isLazy;

    /**
     * 是否置顶
     */
    private Integer isTop;
}
