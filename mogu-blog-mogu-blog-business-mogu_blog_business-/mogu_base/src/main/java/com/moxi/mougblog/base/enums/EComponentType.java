package com.moxi.mougblog.base.enums;

/**
 * 门户模块类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2020年2月12日11:59:04
 */
public enum EComponentType {

    /**
     * 文章分类
     */
    BlogSort("1", "文章分类"),

    /**
     * 用户卡片
     */
    UserCard("2", "用户卡片"),

    /**
     * 任务广场
     */
    UserTask("3", "任务广场"),

    /**
     * 轮播图
     */
    FirstLevel("4", "轮播图"),

    /**
     * 用户签到
     */
    UserSign("5", "用户签到"),

    /**
     * 用户排行
     */
    UserRank("6", "用户排行"),

    /**
     * 热门标签
     */
    HotTag("7", "热门标签"),

    /**
     * 特别推荐
     */
    ThreeRecommend("8", "特别推荐"),

    /**
     * 推荐文章
     */
    FourRecommend("9", "推荐文章"),

    /**
     * 点击排行
     */
    HotClick("10", "点击排行"),

    /**
     * 关注我们
     */
    FollowUs("11", "关注我们"),

    /**
     * 聊天室
     */
    ChatRoom("12", "聊天室"),

    /**
     * 暗黑模式
     */
    BlackModel("13", "暗黑模式"),

    /**
     * 用户评论
     */
    UserComment("14", "用户评论"),

    /**
     * 友情链接
     */
    UserLink("15", "友情链接"),

    /**
     * 用户通知
     */
    UserNotice("16", "用户通知"),

    /**
     * 用户搜索
     */
    UserSearch("17", "用户搜索"),

    /**
     * 创建文章
     */
    CreateButton("18", "创建按钮"),

    /**
     * 创建文章
     */
    CreateArticle("19", "创建文章"),

    /**
     * 上传文章
     */
    UploadArticle("20", "上传文章"),

    /**
     * 创建问答
     */
    CreateQuestion("21", "发布问答"),

    /**
     * 创建问题
     */
    CreateProblem("22", "发布面经"),

    /**
     * 创建动态
     */
    CreateMoment("23", "发表动态"),

    /**
     * 用户收藏
     */
    UserCollect("24", "用户收藏"),

    /**
     * 用户点赞
     */
    UserPrise("25", "用户点赞"),

    /**
     * 用户点踩
     */
    UserTread("26", "用户点踩"),

    /**
     * 创建评论
     */
    UserReport("27", "用户举报"),

    /**
     * 删除评论
     */
    DeleteComment("28", "删除评论"),

    /**
     * 内容勘误
     */
    GeneralEdit("29", "内容勘误"),

    /**
     * 会员模块
     */
    VipModel("30", "会员模块"),


    ;


    private final String code;
    private final String name;

    EComponentType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    // 类型code匹配，返回对应的枚举类
    public static EComponentType getComponentType(String type) {
        for (EComponentType componentType : values()) {
            if (type.equals(componentType.code)) {
                return componentType;
            }
        }
        return null;
    }
}