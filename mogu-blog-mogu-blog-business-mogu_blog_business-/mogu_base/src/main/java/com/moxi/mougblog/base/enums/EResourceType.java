package com.moxi.mougblog.base.enums;

import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;


/**
 * 资源类型枚举类
 *
 * @Author: 陌溪
 * @Date: 2021年12月31日23:04:06
 */
public enum EResourceType {

    /**
     * 博客
     */
    BLOG("1", "BLOG","博客"),

    /**
     * 问答
     */
    Question("2", "QUESTION", "问答"),

    /**
     * 课程
     */
    MEDIA("3", "MEDIA","课程"),

    /**
     * 动态
     */
    MOMENT("4", "MOMENT","动态"),

    /**
     * 评论
     */
    COMMENT("5", "COMMENT","评论"),

    /**
     * 问题
     */
    PROBLEM("6", "PROBLEM","问题"),

    /**
     * 用户
     */
    USER("7", "USER", "用户"),

    /**
     * 问题标签
     */
    PROBLEM_TAG("8", "PROBLEM_TAG", "问题标签"),

    /**
     * 博客标签
     */
    BLOG_TAG("9", "BLOG_TAG", "博客标签"),

    /**
     * 博客分类
     */
    BLOG_SORT("10", "BLOG_SORT", "博客分类"),

    /**
     * 网盘资源
     */
    RESOURCE("11", "RESOURCE", "网盘资源"),

    /**
     * 会员
     */
    VIP("12", "VIP", "会员"),

    /**
     * 专栏
     */
    SUBJECT("13", "SUBJECT", "专栏"),

    /**
     * 社区订单
     */
    SPONSOR("14", "SPONSOR", "社区赞助"),

    /**
     * 订单
     */
    ORDER("15", "ORDER", "订单"),

    /**
     * 提现单
     */
    WITHDRAW("16", "WITHDRAW", "提现单"),

    Lucky("17", "LUCKY", "转盘抽奖"),

    /**
     * 普通商品
     */
    PRODUCT("18", "PRODUCT", "普通商品"),

    ;


    private final String type;
    private final String code;
    private final String name;

    EResourceType(String type, String code, String name) {
        this.type = type;
        this.code = code;
        this.name = name;
    }

    /**
     * 获取资源类型
     *  根据数字找到对应的枚举类
     * @param resourceType
     * @return
     */
    public static EResourceType getType(String resourceType) {
        for (EResourceType type : EResourceType.values()) {
            if (type.getType().equals(resourceType)) {
                return type;
            }
        }
        throw new BusinessException("类型转换失败");
    }

    /**
     * 根据Code获取Resource
     * @param code
     * @return
     */
    public static EResourceType getCode(String code) {
        for (EResourceType type : EResourceType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new BusinessException("类型转换失败");
    }


    /**
     * 业务通知类型转换
     *
     * @param resourceType
     * @param action:      动作， 1：点赞，2 收藏，3: 被AT
     * @return
     */
    public static EBusinessType resourceType2BusinessType(EResourceType resourceType, Integer action) {
        if (action == 1) {
            switch (resourceType) {
                case BLOG: {
                    return EBusinessType.BLOG_PRAISE;
                }
                case Question: {
                    return EBusinessType.QUESTION_PRAISE;
                }
                case MEDIA: {
                    return EBusinessType.MEDIA_PRAISE;
                }
                case MOMENT: {
                    return EBusinessType.MOMENT_PRAISE;
                }
                case COMMENT: {
                    return EBusinessType.COMMENT_PRAISE;
                }
                case PROBLEM: {
                    return EBusinessType.PROBLEM_PRAISE;
                }
                case RESOURCE: {
                    return EBusinessType.RESOURCE_PRAISE;
                }
                default:
                    throw new InsertException("类型转换异常: resourceType:" + resourceType);
            }
        }
        if (action == 2) {
            switch (resourceType) {
                case BLOG: {
                    return EBusinessType.BLOG_COLLECT;
                }
                case Question: {
                    return EBusinessType.QUESTION_COLLECT;
                }
                case MEDIA: {
                    return EBusinessType.MEDIA_COLLECT;
                }
                case MOMENT: {
                    return EBusinessType.MOMENT_COLLECT;
                }
                case COMMENT: {
                    return EBusinessType.COMMENT_COLLECT;
                }
                case PROBLEM: {
                    return EBusinessType.PROBLEM_COLLECT;
                }
                case RESOURCE: {
                    return EBusinessType.RESOURCE_COLLECT;
                }
                default:
                    throw new InsertException("类型转换异常: resourceType:" + resourceType);
            }
        }

        if (action == 3) {
            switch (resourceType) {
                case BLOG: {

                    return EBusinessType.BLOG_AT_USER;
                }
                case Question: {
                    return EBusinessType.QUESTION_AT_USER;
                }
                case MOMENT: {
                    return EBusinessType.MOMENT_AT_USER;
                }
                case COMMENT: {
                    return EBusinessType.COMMENT_AT_USER;
                }
                case PROBLEM: {
                    return EBusinessType.PROBLEM_AT_USER;
                }
                default:
                    throw new InsertException("类型转换异常: resourceType:" + resourceType);
            }
        }
        throw new InsertException("非法的Action: action:" + action);
    }

    /**
     * 转化资源类型
     * @param resource
     * @return
     */
    public static EResourceType getResourceByComment(String resource) {
        switch (resource) {
            case "MESSAGE_BOARD":
            case "ABOUT": {
                return EResourceType.COMMENT;
            }
            case "USER_MOMENT": {
                return EResourceType.MOMENT;
            }
            case "BLOG_INFO": {
                return EResourceType.BLOG;
            }
            case "QUESTION_INFO": {
                return EResourceType.Question;
            }
            case "MEDIA_INFO": {
                return EResourceType.MEDIA;
            }
            default:
                throw new InsertException("类型转换异常: resource:" + resource);
        }
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}