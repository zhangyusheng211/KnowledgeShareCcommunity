package com.moxi.mogublog.commons.convert;

import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mougblog.base.enums.EResourceType;

/**
 * EsAgg转换类
 */
public class AggEsDocConvert {

    /**
     * 转换博客Agg
     * @param blog
     * @return
     */
    public static AggEsDoc convertBlog(Blog blog) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(blog.getUid());
        aggEsDoc.setUid(blog.getUid());
        aggEsDoc.setOid(blog.getOid());
        aggEsDoc.setResourceType(EResourceType.BLOG.getType());
        aggEsDoc.setTitle(blog.getTitle());
        aggEsDoc.setSummary(blog.getSummary());
        aggEsDoc.setContent(blog.getContent());
        aggEsDoc.setAuthor(blog.getAuthor());
        aggEsDoc.setUserUid(blog.getUserUid());
        if (blog.getUser() != null) {
            aggEsDoc.setUser(blog.getUser());
        }
        if (blog.getTagList() != null && blog.getTagList().size() > 0) {
            aggEsDoc.setTagName(blog.getTagList().get(0).getContent());
            aggEsDoc.setTagUid(blog.getTagList().get(0).getUid());
        }
        if (blog.getBlogSort() != null) {
            aggEsDoc.setSortName(blog.getBlogSort().getSortName());
        }
        if (blog.getPhotoList() != null && blog.getPhotoList().size() > 0) {
            aggEsDoc.setPhotoUrl(blog.getPhotoList().get(0));
        }
        aggEsDoc.setCreateTime(blog.getCreateTime());
        aggEsDoc.setUpdateTime(blog.getUpdateTime());
        return aggEsDoc;
    }

    /**
     * 转换问答Agg
     * @param question
     * @return
     */
    public static AggEsDoc convertQuestion(Question question) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(question.getUid());
        aggEsDoc.setUid(question.getUid());
        aggEsDoc.setOid(question.getOid());
        aggEsDoc.setResourceType(EResourceType.Question.getType());
        aggEsDoc.setTitle(question.getTitle());
        aggEsDoc.setSummary(question.getSummary());
        aggEsDoc.setUserUid(question.getUserUid());
        if (question.getUser() != null) {
            aggEsDoc.setAuthor(question.getUser().getNickName());
            aggEsDoc.setUser(question.getUser());
        }
        aggEsDoc.setContent(question.getContent());
        if (question.getTagList() != null && question.getTagList().size() > 0) {
            aggEsDoc.setTagName(question.getTagList().get(0).getContent());
            aggEsDoc.setTagUid(question.getTagList().get(0).getUid());
        }
        aggEsDoc.setCreateTime(question.getCreateTime());
        aggEsDoc.setUpdateTime(question.getUpdateTime());
        return aggEsDoc;
    }

    /**
     * 转换动态Agg
     * @param userMoment
     * @return
     */
    public static AggEsDoc convertMoment(UserMoment userMoment) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(userMoment.getUid());
        aggEsDoc.setUid(userMoment.getUid());
        aggEsDoc.setResourceType(EResourceType.MOMENT.getType());
        aggEsDoc.setTitle("动态");
        aggEsDoc.setSummary(userMoment.getContent());
        aggEsDoc.setUserUid(userMoment.getUserUid());
        if (userMoment.getUser() != null) {
            aggEsDoc.setAuthor(userMoment.getUser().getNickName());
            aggEsDoc.setUser(userMoment.getUser());
        }
        if (userMoment.getPhotoList() != null && userMoment.getPhotoList().size() > 0) {
            aggEsDoc.setPhotoUrl(userMoment.getPhotoList().get(0));
        }
        aggEsDoc.setContent(userMoment.getContent());
        aggEsDoc.setCreateTime(userMoment.getCreateTime());
        aggEsDoc.setUpdateTime(userMoment.getUpdateTime());
        return aggEsDoc;
    }


    /**
     * convertResource 转化资源
     * @param resource
     * @return
     */
    public static AggEsDoc convertResource(Resource resource) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(resource.getUid());
        aggEsDoc.setUid(resource.getUid());
        aggEsDoc.setResourceType(EResourceType.RESOURCE.getType());
        aggEsDoc.setTitle(resource.getName());
        aggEsDoc.setSummary(resource.getSummary());
        aggEsDoc.setUserUid(resource.getUserUid());
        if (resource.getUser() != null) {
            aggEsDoc.setAuthor(resource.getUser().getNickName());
            aggEsDoc.setUser(resource.getUser());
        }
        if (StringUtils.isNotEmpty(resource.getPhotoUrl())) {
            aggEsDoc.setPhotoUrl(resource.getPhotoUrl());
        }

        aggEsDoc.setContent(resource.getContent());
        aggEsDoc.setCreateTime(resource.getCreateTime());
        aggEsDoc.setUpdateTime(resource.getUpdateTime());
        return aggEsDoc;
    }

    /**
     * 转换问题Agg
     * @param problem
     * @return
     */
    public static AggEsDoc convertProblem(Problem problem) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(problem.getUid());
        aggEsDoc.setUid(problem.getUid());
        aggEsDoc.setOid(problem.getOid());
        aggEsDoc.setResourceType(EResourceType.PROBLEM.getType());
        aggEsDoc.setTitle(problem.getTitle());
        aggEsDoc.setSummary(problem.getSummary());
        aggEsDoc.setUserUid(problem.getUserUid());
        aggEsDoc.setContent(problem.getContent());
        aggEsDoc.setCreateTime(problem.getCreateTime());
        aggEsDoc.setUpdateTime(problem.getUpdateTime());
        if (problem.getUser() != null) {
            aggEsDoc.setAuthor(problem.getUser().getNickName());
            aggEsDoc.setUser(problem.getUser());
        }
        return aggEsDoc;
    }

    /**
     * 同步课程
     * @param mediaInfo
     * @return
     */
    public static AggEsDoc convertMedia(MediaInfo mediaInfo) {
        AggEsDoc aggEsDoc = new AggEsDoc();
        aggEsDoc.setId(mediaInfo.getUid());
        aggEsDoc.setUid(mediaInfo.getUid());
        aggEsDoc.setResourceType(EResourceType.MEDIA.getType());
        aggEsDoc.setTitle(mediaInfo.getTitle());
        aggEsDoc.setSummary(mediaInfo.getSummary());
        if (mediaInfo.getActorList() != null && mediaInfo.getActorList().size() > 0) {
            aggEsDoc.setAuthor(mediaInfo.getActorList().get(0).getName());
        }
        aggEsDoc.setContent(mediaInfo.getImages());
        aggEsDoc.setCreateTime(mediaInfo.getCreateTime());
        aggEsDoc.setUpdateTime(mediaInfo.getUpdateTime());
        aggEsDoc.setSortName(mediaInfo.getCategoryName());
        aggEsDoc.setSortUid(mediaInfo.getCategoryUid());
        aggEsDoc.setPhotoUrl(mediaInfo.getImages());
        return aggEsDoc;
    }

}
