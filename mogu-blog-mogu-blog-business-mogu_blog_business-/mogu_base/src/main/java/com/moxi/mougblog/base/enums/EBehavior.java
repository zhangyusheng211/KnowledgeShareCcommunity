package com.moxi.mougblog.base.enums;

import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mougblog.base.global.BaseSysConf;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 行为枚举类
 *
 * @author 陌溪
 * @date 2020/9/14 10:40
 */
// z @Slf4j 是 Lombok 提供的一个注解，用于简化日志记录的代码。通过使用 @Slf4j 注解，
// Lombok 会自动为类生成一个 org.slf4j.Logger 类型的 log 字段，供你在类中使用。
@Slf4j
public enum EBehavior {

    /**
     * 用户行为
     */
    BLOG_TAG("点击标签", "blog_tag"),
    BLOG_SORT("点击博客分类", "blog_sort"),
    BLOG_CONTNET("点击博客", "blog_content"),
    BLOG_PRAISE("点赞", "blog_praise"),
    FRIENDSHIP_LINK("点击友情链接", "friendship_link"),
    BLOG_SEARCH("点击搜索", "blog_search"),
    STUDY_VIDEO("点击学习视频", "study_video"),
    VISIT_PAGE("访问页面", "visit_page"),
    CLICK_BUTTON("点击按钮", "clickButton"),
    VISIT_CLASSIFY("点击博客分类", "visit_classify"),
    VISIT_SORT("点击归档", "visit_sort"),
    BLOG_AUTHOR("点击作者", "blog_author"),
    LOADING_VERIFY("加载校验", "loading_verify"),
    PUBLISH_COMMENT("发表评论", "publish_comment"),
    DELETE_COMMENT("删除评论", "delete_comment"),
    REPORT_COMMENT("举报评论", "report_comment"),
    VISIT_TAG("点击博客标签页面", "visit_tag"),
    COLLECT("收藏", "collect"),
    CANCEL_COLLECT("取消收藏", "cancel_collect"),
    PRAISE("点赞", "praise"),
    CANCEL_PRAISE("取消点赞", "cancel_praise"),
    TREAD("点踩", "tread"),
    CANCEL_TREAD("取消点踩", "cancel_tread"),
    QUESTION("点击问答", "question"),
    ADD_QUESTION("增加问答", "addQuestion"),
    EDIT_QUESTION("编辑问答", "editQuestion"),
    DELETE_QUESTION("删除问答", "deleteQuestion"),
    PROBLEM("点击问题", "problem"),
    ADD_PROBLEM("增加问题", "addProblem"),
    EDIT_PROBLEM("编辑问题", "editProblem"),
    DELETE_PROBLEM("删除问题", "deleteProblem"),
    EDIT_ANSWER("修改答案", "editAnswer"),
    BUILDER_EXAM("生成试卷", "builderExam"),
    ADD_BLOG("增加博客", "addBlog"),
    UPLOAD_BLOG("上传博客", "uploadBlog"),
    PUBLISH_BLOG("发布/下架博客", "publishBlog"),
    EDIT_BLOG("编辑博客", "editBlog"),
    DELETE_BLOG("删除博客", "deleteBlog"),
    GET_NOTICE("获取通知", "getNotice"),
    DELETE_NOTICE("删除通知", "deleteNotice"),
    DELETE_BATCH_NOTICE("删除选中通知", "deleteBatchNotice"),
    REPORT("举报", "report"),
    SIGN_IN("签到", "signIn"),
    RETROACTIVE_SIGN_IN("签到", "retroactiveSignIn"),
    UPDATE_BACKGROUND_IMAGE("更新背景图片", "updateBackgroundImage"),
    ADD_MOMENT("发表动态", "addMoment"),
    DELETE_MOMENT("删除动态", "deleteMoment"),
    PARSE_URL("解析URL信息", "parseUrl"),
    REPLY_MOMENT("动态回复", "add_moment"),
    MEDIA_VIDEO("点击媒资", "media_video"),
    PROBLEM_DEGREE("点击问题掌握程度", "problem_degree"),
    PROBLEM_TAG("点击问题分类", "problemTag"),
    SEARCH_USER_LIST("搜索用户列表", "search_user_list"),
    CHANGE_SEARCH_TYPE("切换外部搜索方式", "change_search_type"),
    SUBMIT_OUTSIDE_SEARCH("外部搜索", "submit_outside_search"),
    PROBLEM_QUICK("问题快问快答", "problemQuick"),

    VISIT_SUBJECT("点击专栏详情", "visitSubject"),
    CLICK_SUBJECT_SORT("点击专栏分类", "clickSubjectSort"),

    ;

    private String content;
    private String behavior;

    EBehavior(String content, String behavior) {
        this.content = content;
        this.behavior = behavior;
    }

    /**
     * 根据value返回枚举类型，主要在switch中使用
     *
     * @param value
     * @return
     */
    public static EBehavior getByValue(String value) {
        for (EBehavior behavior : values()) {
            if (behavior.getBehavior() == value) {
                return behavior;
            }
        }
        return null;
    }

    public static Map<String, String> getModuleAndOtherData(EBehavior behavior,
                                                            Map<String, Object> nameAndArgsMap,
                                                            String bussinessName) {
        String otherData = "";
        String moduleUid = "";
        String bizType = ""; // 业务类型
        switch (behavior) {
            case BLOG_AUTHOR: {
                // 判断是否是点击作者
                if (nameAndArgsMap.get(BaseSysConf.AUTHOR) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConf.AUTHOR).toString();
                }
            }
            break;
            case BLOG_SORT: {
                // 判断是否点击博客分类
                if (nameAndArgsMap.get(BaseSysConf.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.BLOG_SORT_UID).toString();
                }
            }
            break;
            case BLOG_TAG:
            case VISIT_TAG: {
                // 判断是否点击博客标签
                if (nameAndArgsMap.get(BaseSysConf.TAG_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.TAG_UID).toString();
                }
            }
            break;
            case BLOG_SEARCH: {
                // 判断是否进行搜索
                if (nameAndArgsMap.get(BaseSysConf.KEYWORDS) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConf.KEYWORDS).toString();
                }
            }
            break;
            case VISIT_CLASSIFY: {
                // 判断是否点击分类
                if (nameAndArgsMap.get(BaseSysConf.BLOG_SORT_UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.BLOG_SORT_UID).toString();
                }
            }
            break;
            case VISIT_SORT: {
                // 判断是否点击归档
                if (nameAndArgsMap.get(BaseSysConf.MONTH_DATE) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConf.MONTH_DATE).toString();
                }
            }
            break;
            case BLOG_CONTNET: {
                // 判断是否博客详情
                if (nameAndArgsMap.get(BaseSysConf.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.UID).toString();
                } else if (nameAndArgsMap.get(BaseSysConf.OID) != null) {
                    // 当收到的是oid的时候，存储到otherData处
                    otherData = nameAndArgsMap.get(BaseSysConf.OID).toString();
                }
            }
            break;
            case BLOG_PRAISE: {
                // 判断是否给博客点赞
                if (nameAndArgsMap.get(BaseSysConf.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.UID).toString();
                }
            }
            break;
            case FRIENDSHIP_LINK: {
                // 判断是否点击友链
                if (nameAndArgsMap.get(BaseSysConf.UID) != null) {
                    moduleUid = nameAndArgsMap.get(BaseSysConf.UID).toString();
                }
                otherData = bussinessName;
            }
            break;
            case VISIT_PAGE: {
                // 访问页面
                if (nameAndArgsMap.get(BaseSysConf.PAGE_NAME) != null) {
                    otherData = nameAndArgsMap.get(BaseSysConf.PAGE_NAME).toString();
                } else {
                    otherData = bussinessName;
                }
            }
            break;
            case PUBLISH_COMMENT: {
                // 发表评论
                Object object = nameAndArgsMap.get(BaseSysConf.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.CONTENT) != null) {
                    otherData = map.get(BaseSysConf.CONTENT).toString();
                }
            }
            break;
            case REPORT_COMMENT: {
                // 举报评论
                Object object = nameAndArgsMap.get(BaseSysConf.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.CONTENT) != null) {
                    otherData = map.get(BaseSysConf.CONTENT).toString();
                }
            }
            break;
            case DELETE_COMMENT: {
                // 删除评论
                Object object = nameAndArgsMap.get(BaseSysConf.COMMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.CONTENT) != null) {
                    otherData = map.get(BaseSysConf.CONTENT).toString();
                }
            }
            break;
            case LOADING_VERIFY: {
                // 删除评论
                Object object = nameAndArgsMap.get(BaseSysConf.VALID_CODE);
                if (object != null) {
                    otherData = object.toString();
                }
            }
            break;
            case COLLECT:
            case CANCEL_COLLECT: {
                // 收藏内容
                Object object = nameAndArgsMap.get(BaseSysConf.COLLECT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.BIZ_UID) != null) {
                    moduleUid = map.get(BaseSysConf.BIZ_UID).toString();
                }
                if (map.get(BaseSysConf.COLLECT_TYPE) != null) {
                    otherData = map.get(BaseSysConf.COLLECT_TYPE).toString();
                }
            }
            break;
            case TREAD:
            case CANCEL_TREAD:
            case PRAISE:
            case CANCEL_PRAISE: {
                // 点赞内容
                Object object = nameAndArgsMap.get(BaseSysConf.USER_PRAISE_RECORD_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.RESOURCE_UID) != null) {
                    moduleUid = map.get(BaseSysConf.RESOURCE_UID).toString();
                }
                if (map.get(BaseSysConf.RESOURCE_Type) != null) {
                    otherData = map.get(BaseSysConf.RESOURCE_Type).toString();
                }
            }
            break;

            /**
             * 创作博客相关
             */
            case ADD_BLOG:
            case EDIT_BLOG:
            case PUBLISH_BLOG:
            case DELETE_BLOG: {
                // 问答
                Object object = nameAndArgsMap.get(BaseSysConf.BLOG_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.UID) != null) {
                    moduleUid = map.get(BaseSysConf.UID).toString();
                }
                if (map.get(BaseSysConf.TITLE) != null) {
                    otherData = map.get(BaseSysConf.TITLE).toString();
                }
            }
            break;

            /**
             * 创作问答相关
             */
            case ADD_QUESTION:
            case EDIT_QUESTION:
            case DELETE_QUESTION: {
                // 收藏内容
                Object object = nameAndArgsMap.get(BaseSysConf.QUESTION_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.UID) != null) {
                    moduleUid = map.get(BaseSysConf.UID).toString();
                }
                if (map.get(BaseSysConf.TITLE) != null) {
                    otherData = map.get(BaseSysConf.TITLE).toString();
                }
            }
            break;

            /**
             * 举报相关
             */
            case REPORT: {
                // 举报内容
                Object object = nameAndArgsMap.get(BaseSysConf.REPORT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.REPORT_CONTENT_UID) != null) {
                    moduleUid = map.get(BaseSysConf.REPORT_CONTENT_UID).toString();
                }
                if (map.get(BaseSysConf.CONTENT) != null) {
                    otherData = map.get(BaseSysConf.CONTENT).toString();
                }
            }
            break;

            /**
             * 用户动态相关
             */
            case ADD_MOMENT:
            case DELETE_MOMENT: {
                // 用户动态相关
                Object object = nameAndArgsMap.get(BaseSysConf.USER_MOMENT_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.CONTENT) != null) {
                    otherData = map.get(BaseSysConf.CONTENT).toString();
                }
            }
            break;
            case MEDIA_VIDEO: {
                // 判断是否媒资详情
                Object object = nameAndArgsMap.get(BaseSysConf.MediaVideoVO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.MEDIA_INFO_UID) != null) {
                    moduleUid = map.get(BaseSysConf.MEDIA_INFO_UID).toString();
                }
            }
            break;
            /**
             * 问题相关
             */
            case PROBLEM:
            case DELETE_PROBLEM:
            case EDIT_PROBLEM: {
                // 问题
                Object object = nameAndArgsMap.get(BaseSysConf.PROBLEM_VO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.OID) != null) {
                    moduleUid = map.get(BaseSysConf.OID).toString();
                } else if (map.get(BaseSysConf.UID) != null) {
                    moduleUid = map.get(BaseSysConf.UID).toString();
                }
                if (map.get(BaseSysConf.TITLE) != null) {
                    otherData = map.get(BaseSysConf.TITLE).toString();
                }
            }
            break;
            /**
             * 问题掌握程度相关
             */
            case PROBLEM_DEGREE: {
                // 问题
                Object object = nameAndArgsMap.get(BaseSysConf.ProblemUserRecordVO);
                Map<String, Object> map = JsonUtils.objectToMap(object);
                if (map.get(BaseSysConf.PROBLEM_UID) != null) {
                    moduleUid = map.get(BaseSysConf.PROBLEM_UID).toString();
                }
                if (map.get(BaseSysConf.TITLE) != null) {
                    otherData = map.get(BaseSysConf.TITLE).toString();
                }
            }
            break;

            default: {
                log.info("其它行为");
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put(BaseSysConf.MODULE_UID, moduleUid);
        result.put(BaseSysConf.OTHER_DATA, otherData);
        return result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }


}