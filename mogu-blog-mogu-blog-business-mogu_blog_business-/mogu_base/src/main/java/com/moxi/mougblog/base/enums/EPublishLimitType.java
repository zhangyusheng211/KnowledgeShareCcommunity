package com.moxi.mougblog.base.enums;

import lombok.extern.slf4j.Slf4j;

// 发表类型枚举
@Slf4j
public enum EPublishLimitType {

    BLOG_COUNT("文章发表", 1),
    MOMENT_COUNT("动态发布", 2),
    QUESTION_COUNT("问答提出", 3),
    PROBLEM_COUNT("面经发布", 4),
    COMMENT_COUNT("评论提出", 5);

    private String content;
    private Integer type;

    EPublishLimitType() {
    }

    EPublishLimitType(String content, Integer type) {
        this.content = content;
        this.type = type;
    }

}
