package com.moxi.mougblog.base.global;

/**
 * Redis常量基类
 *   用于拼接出redis缓存数据的key
 * @author: 陌溪
 * @create: 2020-03-06-20:43
 */
public class BaseRedisConf {

    /**
     * 请求限制
     */
    public final static String REQUEST_LIMIT = "REQUEST_LIMIT";

    /**
     * 博客分类
     */
    public final static String BLOG_SORT_BY_MONTH = "BLOG_SORT_BY_MONTH";

    /**
     * Redis分隔符
     */
    public final static String SEGMENTATION = ":";

    /**
     * 等于
     */
    public final static String EQUAL_TO = "=";

    /**
     * 井号
     */
    public final static String WELL_NUMBER = "#";

    /**
     * 月份集合
     */
    public final static String MONTH_SET = "MONTH_SET";

    /**
     * 博客等级
     */
    public final static String BLOG_LEVEL = "BLOG_LEVEL";

    /**
     * 博客点击
     */
    public final static String BLOG_CLICK = "BLOG_CLICK";

    /**
     * 专栏点击
     */
    public final static String SUBJECT_CLICK = "SUBJECT_CLICK";

    /**
     * IP地址解析
     */
    public final static String IP_SOURCE = "IP_SOURCE";

    /**
     * 最热博客
     */
    public final static String HOT_BLOG = "HOT_BLOG";

    /**
     * 最新博客
     */
    public final static String NEW_BLOG = "NEW_BLOG";

    /**
     * 管理员Token
     */
    public final static String ADMIN_TOKEN = "ADMIN_TOKEN";

    /**
     * 系统配置
     */
    public final static String SYSTEM_CONFIG = "SYSTEM_CONFIG";

    /**
     * 表单重复提交
     */
    public final static String AVOID_REPEATABLE_COMMIT = "AVOID_REPEATABLE_COMMIT";

    /**
     * 登录限制
     */
    public final static String LOGIN_LIMIT = "LOGIN_LIMIT";

    /**
     * 字典类型
     */
    public final static String REDIS_DICT_TYPE = "REDIS_DICT_TYPE";

    /**
     * token令牌
     */
    public final static String USER_TOKEN = "USER_TOKEN";

    /**
     * 用户uid转token
     */
    public final static String USER_UID_TO_TOKEN = "USER_UID_TO_TOKEN";

    /**
     * 激活用户的时间
     */
    public final static String ACTIVATE_USER = "ACTIVATE_USER";

    /**
     * 登录用户的token
     */
    public final static String LOGIN_TOKEN_KEY = "LOGIN_TOKEN_KEY";

    /**
     * 抽奖加锁
     */
    public final static String LUCKY_LOCK = "LUCKY_LOCK";

    public final static String ONLINE_ADMIN_LIST = "ONLINE_ADMIN_LIST";


    /**
     * 登录用户的token
     */
    public final static String ROBOT_USER_LIST = "ROBOT_USER_LIST";

    /**
     * 登录的UUID
     */
    public final static String LOGIN_UUID_KEY = "LOGIN_UUID_KEY";

    /**
     * 用户提交无效评论的次数
     */
    public final static String USER_PUBLISH_SPAM_COMMENT_COUNT = "USER_PUBLISH_SPAM_COMMENT_COUNT";

    /**
     * 管理员访问菜单
     */
    public final static String ADMIN_VISIT_MENU = "ADMIN_VISIT_MENU";

    /**
     * 博客点赞
     */
    public final static String BLOG_PRAISE = "BLOG_PRAISE";

    /**
     * ALL
     */
    public final static String ALL = "ALL";

    /**
     * 标签点击
     */
    public final static String TAG_CLICK = "TAG_CLICK";

    /**
     * 系统参数配置
     */
    public final static String SYSTEM_PARAMS = "SYSTEM_PARAMS";

    /**
     * 网站配置
     */
    public final static String WEB_CONFIG = "WEB_CONFIG";

    /**
     * 首页展示
     */
    public final static String DASHBOARD = "DASHBOARD";

    /**
     * 一周访问量
     */
    public final static String WEEK_VISIT = "WEEK_VISIT";

    /**
     * 博客标签下包含的博客数量
     */
    public final static String BLOG_COUNT_BY_TAG = "BLOG_COUNT_BY_TAG";

    /**
     * 博客分类下包含的博客数量
     */
    public final static String BLOG_COUNT_BY_SORT = "BLOG_COUNT_BY_SORT";

    /**
     * 全年博客贡献数
     */
    public final static String BLOG_CONTRIBUTE_COUNT = "BLOG_CONTRIBUTE_COUNT";

    public final static String BLOG_LINK = "BLOG_LINK";

    public final static String BLOG_TAG = "BLOG_TAG";

    public final static String BLOG_SORT = "BLOG_SORT";

    /**
     * 用户收到评论数
     */
    public final static String USER_RECEIVE_COMMENT_COUNT = "USER_RECEIVE_COMMENT_COUNT";

    /**
     * 用户收到关注数
     */
    public final static String USER_RECEIVE_WATCH_COUNT = "USER_RECEIVE_WATCH_COUNT";

    /**
     * 用户收到点赞数
     */
    public final static String USER_RECEIVE_PRAISE_COUNT = "USER_RECEIVE_PRAISE_COUNT";

    /**
     * 用户收到系统通知数
     */
    public final static String USER_RECEIVE_SYSTEM_NOTICE_COUNT = "USER_RECEIVE_SYSTEM_NOTICE_COUNT";

    /**
     * 用户收到消息数
     */
    public final static String USER_RECEIVE_MESSAGE_COUNT = "USER_RECEIVE_MESSAGE_COUNT";

    /**
     * 用户收到收藏数
     */
    public final static String USER_RECEIVE_COLLECT_COUNT = "USER_RECEIVE_COLLECT_COUNT";

    /**
     * 用户收到的群组消息数
     */
    public final static String USER_RECEIVE_GROUP_MESSAGE_COUNT = "USER_RECEIVE_GROUP_MESSAGE_COUNT";

    /**
     * 问答点击
     */
    public final static String QUESTION_CLICK = "QUESTION_CLICK";

    /**
     * 文章加载校验
     */
    public final static String LOADING_VALID = "LOADING_VALID";

    /**
     * 文件上传限制大小【每日】
     */
    public final static String FILE_UPLOAD_LIMIT_SIZE = "FILE_UPLOAD_LIMIT_SIZE";

    /**
     * 用户单日文章发布次数
     */
    public final static String USER_PUBLISH_BLOG_COUNT = "USER_PUBLISH_BLOG_COUNT";

    /**
     * 用户单日问答发布次数
     */
    public final static String USER_PUBLISH_QUESTION_COUNT = "USER_PUBLISH_QUESTION_COUNT";

    /**
     * 首页展示
     */
    public final static String BLOG_VISIT_COUNT = "BLOG_VISIT_COUNT";

    /**
     * 发表的博客数
     */
    public final static String BLOG_PUBLISH_COUNT = "BLOG_PUBLISH_COUNT";

    /**
     * 发表的问题数
     */
    public final static String PROBLEM_PUBLISH_COUNT = "PROBLEM_PUBLISH_COUNT";

    /**
     * 用户积分
     */
    public final static String USER_CREDITS = "USER_CREDITS";

    /**
     * 用户积分新增限制
     */
    public final static String USER_CREDITS_ADD_LIMIT = "USER_CREDITS_ADD_LIMIT";

    /**
     * 首页TopN的用户
     */
    public final static String INDEX_TOP_N_USER = "INDEX_TOP_N_USER";

    /**
     * 首页TopN的用户数量
     */
    public final static String INDEX_TOP_N_USER_TOTAL = "INDEX_TOP_N_USER_TOTAL";

    /**
     * 用户本月签到记录
     */
    public final static String USER_SIGN_IN_RECORD_MONTH = "USER_SIGN_IN_RECORD_MONTH";

    /**
     * 文件UID转文件URL
     */
    public final static String FILE_UID_TO_URL = "FILE_UID_TO_URL";

    /**
     * 文件UID转文件DTO
     */

    public final static String FILE_UID_TO_FILE_DTO = "FILE_UID_TO_FILE_DTO";

    /**
     * 收藏数
     */
    public final static String COLLECT_COUNT = "COLLECT_COUNT";

    /**
     * 用户是否收藏
     */
    public final static String IS_USER_COLLECT = "IS_USER_COLLECT";

    /**
     * 点赞数
     */
    public final static String PRAISE_COUNT = "PRAISE_COUNT";

    /**
     * 用户是否点赞
     */
    public final static String IS_USER_PRAISE = "IS_USER_PRAISE";

    /**
     * 点踩数
     */
    public final static String TREAD_COUNT = "TREAD_COUNT";

    /**
     * 用户是否点踩
     */
    public final static String IS_USER_TREAD = "IS_USER_TREAD";

    /**
     * 用户活跃状态【是否被封号】
     */
    public final static String USER_ACTIVE_STATUS = "USER_ACTIVE_STATUS";

    /**
     * 开启聊天
     */
    public final static String OPEN_CHAT = "OPEN_CHAT";
    /**
     * 开启表情
     */
    public final static String OPEN_EMOJI = "OPEN_EMOJI";
    /**
     * 开启图片
     */
    public final static String OPEN_PICTURE = "OPEN_PICTURE";
    /**
     * 开启语音
     */
    public final static String OPEN_RECORD = "OPEN_RECORD";
    /**
     * 开启通话
     */
    public final static String OPEN_CALL = "OPEN_CALL";
    /**
     * 开启视频
     */
    public final static String OPEN_VEDIO = "OPEN_VEDIO";
    /**
     * 评论数
     */
    public final static String COMMENT_PUBLISH_COUNT = "COMMENT_PUBLISH_COUNT";
    /**
     * 点赞数
     */
    public final static String USER_PRAISE_RECORD_COUNT = "USER_PRAISE_RECORD_COUNT";
    /**
     * 动态发布数
     */
    public final static String USER_MOMENT_COUNT = "USER_MOMENT_COUNT";

    /**
     * 关注数
     */
    public final static String USER_WATCH_COUNT = "USER_WATCH_COUNT";

    /**
     * 新用户注册活跃间隔
     */
    public static final String NEW_USER_LIMIT_TIME = "NEW_USER_LIMIT_TIME";

    /**
     * 文章数量
     */
    public final static String BLOG_COUNT = "BLOG_COUNT";

    /**
     * 问答数量
     */
    public final static String QUESTION_COUNT = "QUESTION_COUNT";

    /**
     * 动态数量
     */
    public final static String MOMENT_COUNT = "MOMENT_COUNT";

    /**
     * 评论数量
     */
    public final static String COMMENT_COUNT = "COMMENT_COUNT";

    /**
     * 评论数量
     */
    public final static String USER_COUNT = "USER_COUNT";

    /**
     * 问题数量
     */
    public final static String PROBLEM_COUNT = "PROBLEM_COUNT";

    /**
     * 访问数量
     */
    public final static String VISIT_COUNT = "VISIT_COUNT";

    /**
     * 用户ip属地
     */
    public final static String USER_IP_POSSESSION = "USER_IP_POSSESSION";

    /**
     * 后台管理员站内信 小红点
     */
    public final static String ADMIN_NOTICE_POINT = "ADMIN_NOTICE_POINT";

    /**
     * 后台管理员站内信 小红点
     */
    public final static String ADMIN = "ADMIN";

    /**
     * 登录方式
     */
    public final static String LOGIN_TYPE = "LOGIN_TYPE";
    public final static String GITEE = "GITEE";
    public final static String GITHUB = "GITHUB";
    public final static String QQ = "QQ";
    public final static String PASSWORD = "PASSWORD";
    public final static String WECHAT = "WECHAT";

    /**
     * 用户单日面经发布次数
     */
    public final static String USER_PUBLISH_PROBLEM_COUNT = "USER_PUBLISH_PROBLEM_COUNT";

    /**
     * 管理员找回密码请求限制
     */
    public final static String FORGET_LIMIT = "FORGET_LIMIT";

    /**
     * 管理员找回密码的token
     */
    public final static String FORGET_PASSWORD = "FORGET_PASSWORD";

    /**
     * 热搜插入限制
     */
    public final static String HOT_SEARCH_LIMIT = "HOT_SEARCH_LIMIT";

    /**
     * 热搜列表
     */
    public final static String HOT_SEARCH_LIST = "HOT_SEARCH_LIST";

    /**
     * 动态排行列表
     */
    public final static String RANK_MOMENT_LIST = "RANK_MOMENT_LIST";

    /**
     * 博客排行列表
     */
    public final static String RANK_BLOG_LIST = "RANK_BLOG_LIST";

    /**
     * 总排行列表
     */
    public final static String RANK_ALL_LIST = "RANK_ALL_LIST";

    /**
     * 周排行列表
     */
    public final static String RANK_WEEK_LIST = "RANK_WEEK_LIST";

    /**
     * 月排行列表
     */
    public final static String RANK_MONTH_LIST = "RANK_MONTH_LIST";

    /**
     * 排行列表页
     */
    public final static String RANK_LIST = "RANK_LIST";


    /**
     * 用户等级限制发布数量
     */
    public final static String LEVEL_LIMIT_PUBLISH = "LEVEL_LIMIT_PUBLISH";

    /**
     * 用户博客每日发布限制
     */
    public final static String BLOG_LIMIT = "BLOG_LIMIT";

    /**
     * 用户问答每日发布限制
     */
    public final static String QUESTION_LIMIT = "QUESTION_LIMIT";

    /**
     * 用户面经每日发布限制
     */
    public final static String PROBLEM_LIMIT = "PROBLEM_LIMIT";

    /**
     * 用户动态每日发布限制
     */
    public final static String MOMENT_LIMIT = "MOMENT_LIMIT";

    /**
     * 用户评论每日发布限制
     */
    public final static String COMMENT_LIMIT = "COMMENT_LIMIT";
    /**
     * 万历年节假日数据
     */
    public final static String CALENDAR_LIST = "CALENDAR_LIST";

    /**
     * 外部热搜列表
     */
    public final static String OUTSIDE_HOT_SEARCH_LIST = "OUTSIDE_HOT_SEARCH_LIST";

    /**
     * 用户信息卡片数据
     */
    public final static String USER_CARD_DETAIL = "USER_CARD_DETAIL";

    /**
     * 用户排名
     */
    public final static String USER_RANK = "USER_RANK";

    /**
     * ChatGPT回复次数
     */
    public final static String CHATGPT_REPLY_COUNT = "CHATGPT_REPLY_COUNT";

    /**
     * 账户ID转用户
     */
    public final static String ACCOUNT_ID_TO_USER = "ACCOUNT_ID_TO_USER";

    /**
     * 资源点击
     */
    public final static String RESOURCE_CLICK = "RESOURCE_CLICK";

    /**
     * 全量勋章出现频率
     */
    public final static String ALL_MEDAL_FREQUENCY = "ALL_MEDAL_FREQUENCY";

    /**
     * 聊天过于频繁
     */
    public final static String CHAT_FREQUENTLY = "CHAT_FREQUENTLY";

    /**
     * 聊天频控
     */
    public final static String CHAT_FREQUENTLY_BLOCK = "CHAT_FREQUENTLY_BLOCK";

    /**
     * 抽奖奖品
     */
    public final static String LUCKY_PRODUCT_FOR_THANKS = "LUCKY_PRODUCT_FOR_THANKS";

    /**
     * 访问权限
     */
    public final static String VISIT_AUTH = "VISIT_AUTH";
}
