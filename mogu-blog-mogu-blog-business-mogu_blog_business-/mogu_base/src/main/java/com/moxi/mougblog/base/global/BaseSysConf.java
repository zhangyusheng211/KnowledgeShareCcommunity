package com.moxi.mougblog.base.global;

/**
 * 常量的基类
 * z 定义了一系列常量属性！
 *
 * @author 陌溪
 * @date 2018年9月25日00:06:54
 */
public class BaseSysConf {

    public final static String DEFAULT_UID = "uid00000000000000000000000000000";
    public final static String LIMIT_ONE = "LIMIT 1";
    public final static String OID = "oid";

    /**
     * picture相关
     */
    public final static String USER_UID = "userUid";

    public final static String TO_USER_UID = "toUserUid";
    public final static String BLOG = "blog";
    public final static String USER_NAME = "userName";
    public final static String ADMIN_UID = "adminUid";
    public final static String ROLE = "role";
    public final static String PROJECT_NAME = "projectName";
    public final static String SORT_NAME = "sortName";
    public final static String PIC_NAME = "picName";
    public final static String FILE_NAME = "fileName";
    public final static String UPLOADED = "uploaded";
    public final static String QI_NIU_URL = "qiNiuUrl";
    public final static String PIC_URL = "picUrl";
    public final static String MINIO_URL = "minioUrl";
    public final static String ALIOSS_URL = "aliOssUrl";
    public final static String URL = "url";
    public final static String PAY_ORDER_UID = "payOrderUid";
    public final static String M3U8_URL = "m3u8Url";
    public final static String MESSAGE = "message";
    public final static String EXPANDED_NAME = "expandedName";
    public final static String FILE_OLD_NAME = "fileOldName";
    public final static String PICTURE_LIST = "pictureList";
    public final static String NAME = "name";
    public final static String SOURCE = "source";
    public final static String LOADING_VALID = "loadingValid";
    public final static String CREDITS = "credits";
    public final static String EXP_VALUE = "expValue";
    public final static String AUTH_CODE_LIST = "authCodeList";

    /**
     * IP相关
     */
    public final static String OS = "OS";
    public final static String BROWSER = "BROWSER";
    public final static String IP = "ip";
    public final static String UTF_8 = "utf-8";


    public final static String SUCCESS = "success";
    public final static String ERROR = "error";
    public final static String STATUS = "status";
    public final static String CREATE_TIME = "createTime";
    public final static String UPDATE_TIME = "updateTime";
    public final static String TOKEN = "token";
    public final static String IS_NEW_USER = "isNewUser";

    public final static String USER_TYPE = "userType";

    public final static String TYPE = "type";

    public final static String USER = "user";
    public final static String LOG_ID = "LogID";
    public final static String PLATFORM = "platform";
    public final static String ACCESS_TOKEN = "accessToken";

    public final static String CAN_NOT_COMMENT = "0";
    public final static String CODE = "code";
    public final static String DATA = "data";
    public final static String UID = "uid";
    public final static String MEDIA_INFO_UID = "mediaInfoUid";
    public final static String PAGE_NAME = "pageName";
    public final static String LEFT = "left";
    public final static String RIGHT = "right";
    public final static String QUESTION_SOURCE = "questionSource";
    public final static String REPORT_CONTENT_UID = "reportContentUid";

    public final static String DEFAULT_VALUE = "defaultValue";

    /**
     * platform平台相关
     */
    public final static String WEB = "web";
    public final static String ADMIN = "admin";

    /**
     * 分页相关
     */
    public final static String TOTAL = "total";
    public final static String TOTAL_PAGE = "totalPage";
    public final static String CURRENT_PAGE = "currentPage";
    public final static String BLOG_LIST = "blogList";
    public final static String QUESTION_LIST = "questionList";
    public final static String PAGE_SIZE = "pageSize";

    /**
     * blog
     */
    public final static String BLOG_UID = "blogUid";
    public final static String LEVEL = "level";

    public final static String START_EMAIL_NOTIFICATION = "startEmailNotification";
    public final static String LEADER_BLOG = "leaderBlog";
    public final static String LEADER_MOMENT = "leaderMoment";
    public final static String LEADER_QUESTION = "leaderQuestion";
    public final static String LEADER_SIGN = "leaderSign";
    public final static String LEADER_COMMENT = "leaderComment";


    /**
     * RabbitMQ的命令操作
     */
    public final static String COMMAND = "command";

    public final static String AGG_ES_DOC = "aggEsDoc";
    public final static String EDIT = "edit";
    public final static String ADD = "add";
    public final static String DELETE = "delete";
    public final static String DELETE_BATCH = "deleteBatch";
    public final static String EDIT_BATCH = "editBatch";
    public final static String DELETE_ALL = "deleteAll";
    public final static String EXCHANGE_DIRECT = "exchange.direct";
    public final static String MOGU_BLOG = "mogu.blog";

    /**
     * redis相关
     */
    public final static String BLOG_SORT_BY_MONTH = "BLOG_SORT_BY_MONTH";
    /**
     * redis分割符
     */
    public final static String REDIS_SEGMENTATION = ":";
    public final static String EQUAL_TO = "=";
    /**
     * 月份集合
     */
    public final static String MONTH_SET = "MONTH_SET";
    /**
     * 博客等级
     */
    public final static String BLOG_LEVEL = "BLOG_LEVEL";


    /**
     * 字典类型
     */
    public final static String REDIS_DICT_TYPE = "REDIS_DICT_TYPE";

    /**
     * 密钥配置
     */
    public final static String REDIS_SECRET_CONFIG = "REDIS_SECRET_CONFIG";

    /**
     * 字典类型标签
     */
    public final static String REDIS_DICT_TYPE_LABEL = "REDIS_DICT_TYPE_LABEL";

    /**
     * 文件分割符
     */
    public final static String FILE_SEGMENTATION = ",";

    /**
     * 系统全局是否标识
     */
    public static final int YES = 1;
    public static final int NO = 0;

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;

    public static final int TWO_TWO_FIVE = 225;
    public static final int ONE_ZERO_TWO_FOUR = 1024;

    /**
     * SystemConfig相关
     */
    public final static String UPLOAD_QI_NIU = "uploadQiNiu";
    public final static String UPLOAD_ALIOSS = "uploadAlioss";
    public final static String UPLOAD_LOCAL = "uploadLocal";
    public final static String UPLOAD_MINIO = "uploadMinio";
    public final static String LOCAL_PICTURE_BASE_URL = "localPictureBaseUrl";
    public final static String QI_NIU_PICTURE_BASE_URL = "qiNiuPictureBaseUrl";
    public final static String ALI_OSS_PICTURE_BASE_URL = "aliossPictureBaseUrl";
    public final static String MINIO_PICTURE_BASE_URL = "minioPictureBaseUrl";

    public final static String QI_NIU_ACCESS_KEY = "qiNiuAccessKey";
    public final static String QI_NIU_SECRET_KEY = "qiNiuSecretKey";
    public final static String QI_NIU_BUCKET = "qiNiuBucket";
    public final static String QI_NIU_AREA = "qiNiuArea";

    public final static String ALI_YUN_BUCKET_NAME = "aliyunBucketName";
    public final static String ALI_YUN_ACCESS_KEY = "aliyunAccessKey";
    public final static String ALI_YUN_ACCESS_KEY_SECRET = "aliyunAccessKeySecret";
    public final static String ALI_YUN_ENDPOINT = "aliyunEndpoint";
    public final static String ALI_YUN_CATALOG = "aliyunCatalog";


    public final static String MINIO_END_POINT = "minioEndPoint";
    public final static String MINIO_ACCESS_KEY = "minioAccessKey";
    public final static String MINIO_SECRET_KEY = "minioSecretKey";
    public final static String MINIO_BUCKET = "minioBucket";

    public final static String PICTURE_PRIORITY = "picturePriority";
    public final static String CONTENT_PICTURE_PRIORITY = "contentPicturePriority";
    public final static String PICTURE = "picture";
    public final static String PICTURE_TOKEN = "pictureToken";
    public final static String SERVICE_TOKEN = "serviceToken";

    public final static String FEIGN_REQUEST = "feignRequest";

    public final static String LIST = "list";


    /**
     * AOP相关
     */
    public static final String AUTHOR = "author";
    public static final String BLOG_SORT_UID = "blogSortUid";
    public static final String TAG_UID = "tagUid";
    public static final String KEYWORDS = "keywords";
    public static final String MONTH_DATE = "monthDate";
    public static final String MODULE_UID = "moduleUid";
    public static final String OTHER_DATA = "otherData";
    public static final String COMMENT_VO = "commentVO";
    public static final String COLLECT_VO = "collectVO";
    public static final String MediaVideoVO = "mediaVideoVO";
    public static final String USER_PRAISE_RECORD_VO = "userPraiseRecordVO";
    public static final String BLOG_VO = "blogVO";
    public static final String QUESTION_VO = "questionVO";
    public static final String REPORT_VO = "reportVO";
    public static final String PROBLEM_VO = "problemVO";
    public static final String USER_MOMENT_VO = "userMomentVO";
    public static final String ProblemUserRecordVO = "problemUserRecordVO";
    public static final String VALID_CODE = "validCode";
    public static final String CONTENT = "content";
    public static final String TARGET = "target";

    /**
     * 参数配置相关
     */
    public static final String SYS_DEFAULT_PASSWORD = "SYS_DEFAULT_PASSWORD";
    public static final String BLOG_NEW_COUNT = "BLOG_NEW_COUNT";
    public static final String BLOG_FIRST_COUNT = "BLOG_FIRST_COUNT";
    public static final String BLOG_SECOND_COUNT = "BLOG_SECOND_COUNT";
    public static final String BLOG_THIRD_COUNT = "BLOG_THIRD_COUNT";
    public static final String BLOG_FOURTH_COUNT = "BLOG_FOURTH_COUNT";
    public static final String BLOG_HOT_COUNT = "BLOG_HOT_COUNT";
    public static final String HOT_TAG_COUNT = "HOT_TAG_COUNT";
    public static final String HOT_BLOG_SORT_COUNT = "HOT_BLOG_SORT_COUNT";
    public static final String FRIENDLY_LINK_COUNT = "FRIENDLY_LINK_COUNT";
    public final static String PROJECT_NAME_ = "PROJECT_NAME";
    public final static String MAX_STORAGE_SIZE = "MAX_STORAGE_SIZE";
    public final static String SYS_VALID_CODE = "SYS_VALID_CODE";
    public static final String SYS_SENSITIVE_WORD = "SYS_SENSITIVE_WORD";
    public static final String USER_PUBLISH_BLOG_COUNT = "USER_PUBLISH_BLOG_COUNT";
    public static final String USER_PUBLISH_QUESTION_COUNT = "USER_PUBLISH_QUESTION_COUNT";

    public static final String SYS_VERSION_PARAMS = "SYS_VERSION_PARAMS";
    public static final String USER_TOP_N = "USER_TOP_N";
    public static final String USER_TAG = "userTag";
    public static final String NICK_NAME = "nickName";
    public final static String BIZ_UID = "bizUid";
    public final static String COLLECT_TYPE = "collectType";
    public final static String RESOURCE_UID = "resourceUid";
    public final static String RESOURCE_Type = "resourceType";
    public final static String TITLE = "title";
    public final static String PROBLEM_UID = "problemUid";
    public static final String USER_PUBLISH_PROBLEM_COUNT = "USER_PUBLISH_PROBLEM_COUNT";
    public static final String USER_DEFAULT_AVATAR = "USER_DEFAULT_AVATAR";
    public static final String SYS_SEARCH_SIZE = "SYS_SEARCH_SIZE";
    public static final String SYS_WHITELIST_WORD = "SYS_WHITELIST_WORD";
    // 用户等级限制发布数量
    public static final String LEVEL_LIMIT_PUBLISH = "LEVEL_LIMIT_PUBLISH";
    public static final String SYS_HOTSEARCH_BLACKLIST = "SYS_HOTSEARCH_BLACKLIST";
    public static final String SYS_HOTSEARCH_COUNT = "SYS_HOTSEARCH_COUNT";
    public static final String SYS_SENSITIVE_SLOGAN = "SYS_SENSITIVE_SLOGAN";
    public static final String SYS_REGULAR_SLOGAN = "SYS_REGULAR_SLOGAN";
    // 聊天撤回时间
    public static final String MESSAGE_WITHDRAW_TIME = "MESSAGE_WITHDRAW_TIME";

    // ChatGPT配置
    public static final String SYS_CHATGPT_SETTING = "SYS_CHATGPT_SETTING";

    public static final String SYS_RECOURCE_RECOMMEND = "SYS_RECOURCE_RECOMMEND";

    public static final String SYS_AUTH_USER = "SYS_AUTH_USER";
    public static final String SYS_DEFAULT_USER_AUTH = "SYS_DEFAULT_USER_AUTH";

    public final static String STATE = "state";

    /**
     * 转载模板
     */
    public static final String SYS_REPRINTED_TEMPLATE = "SYS_REPRINTED_TEMPLATE";

    /**
     * 原创模板
     */
    public static final String SYS_ORIGINAL_TEMPLATE = "SYS_ORIGINAL_TEMPLATE";

    /**
     * 单次提现金额限制
     * 提现金额限制【单次提现金额数量必须大于当前配置】
     */
    public static final String SYS_WITHDRAW_LIMIT = "SYS_WITHDRAW_LIMIT";

    /**
     * 请求万历年数据接口
     */
    public final static String REQUEST_CALENDAR = "https://natescarlet.coding.net/p/github/d/holiday-cn/git/raw/master/";
    public final static String CALENDAR_SUFFIX = ".json";
    public final static String CALENDAR_DAYS = "days";


    /**
     * 请求头相关
     */
    public final static String X_USER_UID = "x-user-uid";
    public final static String Authorization = "Authorization";


}
