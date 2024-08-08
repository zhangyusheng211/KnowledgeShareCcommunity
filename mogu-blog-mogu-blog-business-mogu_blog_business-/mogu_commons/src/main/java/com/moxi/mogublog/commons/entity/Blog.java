package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.enums.EVisitAuthType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 博客表
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@Data
@TableName("t_blog")
public class Blog extends SuperEntity<Blog> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一oid【自动递增】
     */
    private Integer oid;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客简介
     * updateStrategy = FieldStrategy.IGNORED ：表示更新时候忽略非空判断
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 标签uid
     */
    private String tagUid;

    /**
     * 博客分类UID
     */
    private String blogSortUid;

    /**
     * 博客点击数
     */
    private Integer clickCount;

    /**
     * 点赞数
     */
    private Integer praiseCount;

    /**
     * 点踩数量
     */
    private Integer treadCount;

    /**
     * 博客收藏数
     */
    private Integer collectCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 标题图片UID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fileUid;

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
     * 如果原创，作者为管理员名
     */
    private String author;
    /**
     * 前端使用的标红的作者名
     */
    @TableField(exist = false)
    private String authorName;

    /**
     * 文章出处
     */
    private String articlesPart;

    /**
     * 推荐级别，用于首页推荐
     * 0：正常
     * 1：一级推荐(轮播图)
     * 2：二级推荐(top)
     * 3：三级推荐 ()
     * 4：四级 推荐 (特别推荐)
     */
    private Integer level;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 分数，用于推荐排序
     */
    private Integer score;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 文章类型【0 博客， 1：推广】
     */
    private String type;

    /**
     * 外链【如果是推广，那么将跳转到外链】
     */
    private String outsideLink;

    /**
     * 投稿用户UID
     */
    private String userUid;

    /**
     * 文章来源【0 后台添加，1 用户投稿】
     */
    private String articleSource;

    /**
     * 审核状态【0：审核未通过，1：审核通过】
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
     * 是否开启加载校验
     */
    private String openLoadingValid;

    /**
     * 是否是特权文章  '0' 普通文章， '1' 特权文章
     */
    private String isVip;

    /**
     * 是否仅专栏可见：0 否， 1 是
     */
    private Integer isOnlySubjectShow;

    /**
     * 文章字符数
     */
    private Integer charCount;

    /**
     * 阅读权限：1 公开、2 登录后可见、3 评论后可见、4 输入验证码可见、5 会员可见、6 付费可见
     */
    private String visitAuth;

    /**
     * 访问可见扩展字段【如：存储哪些会员可见、密码可见对应的密码】 对应 VisitAuthExtra 序列化得到
     */
    private String visitAuthExtra;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 支付类型：1:积分支付，2: 现金支付
     */
    private Integer payType;

    /**
     * 加载范围： 2 都不可见，1 部分可见
     */
    private Integer loadingArea;


    /**
     *  博客内容复制权限：false表示可复制博客内容，true表示不可复制博客内容
     */
    private Integer isCopy;

    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标签,一篇博客对应多个标签
     */
    @TableField(exist = false)
    private List<Tag> tagList;

    /**
     * 标题图
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * 博客分类
     */
    @TableField(exist = false)
    private BlogSort blogSort;

    /**
     * 博客分类名
     */
    @TableField(exist = false)
    private String blogSortName;

    /**
     * 博客标题图
     */
    @TableField(exist = false)
    private String photoUrl;

    /**
     * 版权申明
     */
    @TableField(exist = false)
    private String copyright;

    /**
     * 博客所属专题
     */
    @TableField(exist = false)
    private Subject subject;

    /**
     * 博客所属专题列表【1个博客可能属于多个专题】
     */
    @TableField(exist = false)
    private List<Subject> subjectList;

    /**
     * 专题列表
     */
    @TableField(exist = false)
    private List<SubjectItem> subjectItemList;

    /**
     * 投稿人
     */
    @TableField(exist = false)
    private User user;

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

    /**
     * 积分合计
     */
    @TableField(exist = false)
    private String sumCredits;

    /**
     * 点赞用户列表
     */
    @TableField(exist = false)
    private List<User> praiseUserList;

    /**
     * 访问鉴权成功
     */
    @TableField(exist = false)
    private Boolean visitAuthSuccess;

    /**
     * 支付订单数
     */
    @TableField(exist = false)
    private Integer payOrderCount;

    /**
     *  访问可见扩展字段
     */
    @TableField(exist = false)
    private VisitAuthExtra visitAuthExtraVo;


}
