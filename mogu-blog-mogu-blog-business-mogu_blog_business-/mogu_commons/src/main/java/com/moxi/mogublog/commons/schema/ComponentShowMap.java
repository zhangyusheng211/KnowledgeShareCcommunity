package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * 模块展示Map
 */
@Data
public class ComponentShowMap {
    /**
     * 展示文章分类
     */
    private boolean showBlogSort;

    /**
     * 展示用户卡片
     */
    private boolean showUserCard;

    /**
     * 展示任务广场
     */
    private boolean showUserTask;

    /**
     * 展示轮播图
     */
    private boolean showFirstLevel;

    /**
     * 展示用户签到
     */
    private boolean showUserSign;


    /**
     * 展示用户排行
     */
    private boolean showUserRank;

    /**
     * 展示热门标签
     */
    private boolean showHotTag;


    /**
     * 展示特别推荐
     */
    private boolean showThreeRecommend;


    /**
     * 展示推荐文章
     */
    private boolean showFourRecommend;


    /**
     * 展示点击排行
     */
    private boolean showHotClick;


    /**
     * 展示关注我们
     */
    private boolean showFollowUs;


    /**
     * 展示聊天室
     */
    private boolean showChatRoom;

    /**
     * 展示暗黑模式
     */
    private boolean showBlackModel;

    /**
     * 展示评论
     */
    private boolean showUserComment;

    /**
     * 展示友情链接
     */
    private boolean showUserLink;

    /**
     * 展示用户通知
     */
    private boolean showUserNotice;

    /**
     * 用户搜索
     */
    private boolean showUserSearch;

    /**
     * 创作按钮
     */
    private boolean showCreateButton;

    /**
     * 创作文章
     */
    private boolean showCreateArticle;

    /**
     * 上传文章
     */
    private boolean showUploadArticle;

    /**
     * 创建问答
     */
    private boolean showCreateQuestion;

    /**
     * 发布面经
     */
    private boolean showCreateProblem;

    /**
     * 发表动态
     */
    private boolean showCreateMoment;


    /**
     * 用户收藏
     */
    private boolean showUserCollect;

    /**
     * 用户点赞
     */
    private boolean showUserPrise;

    /**
     * 用户点踩
     */
    private boolean showUserTread;

    /**
     * 用户举报
     */
    private boolean showUserReport;

    /**
     * 删除评论
     */
    private boolean showDeleteComment;

    /**
     * 展示勘误
     */
    private boolean showGeneralEdit;

    /**
     * 展示会员模块
     */
    private boolean showVipModel;

}
