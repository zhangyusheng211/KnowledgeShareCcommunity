package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 问题表
 * @author 陌溪
 * @date 2022年3月7日21:42:26
 */
@Data
@TableName("t_problem")
public class Problem  extends SuperEntity<Problem> {

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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;
    /**
     * 内容
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String content;
    /**
     * 答案
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
     * 问题标签uid【冗余一份，方便查询】
     */
    private String problemTagUid;
    /**
     * 点击数
     */
    private Integer clickCount;
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
     * 来源
     */
    private String source;
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
     * 是否有答案
     */
    private String hasAnswer;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 问题标签列表
     */
    @TableField(exist = false)
    private List<ProblemTag> problemTagList;

    /**
     * 上传用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 排序权重
     */
    @TableField(exist = false)
    private Integer weight;

    /**
     * 相似的问题
     */
    @TableField(exist = false)
    private List<Problem> sameProblemList;

    /**
     * 点赞相关信息
     */
    @TableField(exist = false)
    private Map<String, Object> praiseInfo;

    /**
     * 收藏相关信息
     */
    @TableField(exist = false)
    private Map<String, Object> collectInfo;
}
