package com.moxi.mogublog.admin.restapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.SearchFeignClient;
import com.moxi.mogublog.commons.global.MessageConf;
import com.moxi.mogublog.commons.global.SQLConf;
import com.moxi.mogublog.commons.global.SysConf;
import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.WebUtils;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.global.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 索引维护 ReastApi
 *
 * @author 陌溪
 * @date 2020年1月15日16:44:27
 */
@RestController
@RequestMapping("/search")
@Api(value = "索引维护相关接口", tags = {"索引维护相关接口"})
@Slf4j
public class SearchIndexRestApi {

    @Resource
    private BlogService blogService;
    @Resource
    private ProblemService problemService;
    @Resource
    private UserMomentService userMomentService;
    @Resource
    private QuestionService questionService;
    @Resource
    private SearchFeignClient searchFeignClient;
    @Resource
    private MediaInfoService mediaInfoService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private SysDictDataService sysDictDataService;


    @AuthorityVerify
    @OperationLogger(value = "清空Es索引")
    @ApiOperation(value = "清空Es索引", notes = "清空Es索引")
    @PostMapping("/cleanIndex")
    public String cleanIndex() {
        String result = searchFeignClient.cleanEsIndex();
        Map<String, Object> blogMap = (Map<String, Object>) JsonUtils.jsonToObject(result, Map.class);
        if (SysConf.SUCCESS.equals(blogMap.get(SysConf.CODE))) {
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(blogMap.get(SysConf.MESSAGE).toString());
        }
    }

    @AuthorityVerify
    @OperationLogger(value = "批量同步博客索引")
    @ApiOperation(value = "批量同步博客索引", notes = "批量同步博客索引")
    @GetMapping("/batchInitElasticIndex")
    public String initBlogElasticIndex(@RequestParam("resourceType") EResourceType resourceType) {
        String result = "";
        switch (resourceType) {
            case BLOG: {
                result = buildBlogAggDoc();
            }
            break;
            case Question: {
                result = buildQuestionAggDoc();
            }
            break;
            case MEDIA: {
                buildMediaAggDoc();
            }
            break;
            case MOMENT: {
                result = buildMomentAggDoc();
            }
            break;
            case PROBLEM: {
                result = buildProblemAggDoc();
            }
            break;
            case RESOURCE: {
                result = buildResourceAggDoc();
            }
            break;
            default:
                return ResultUtil.errorWithMessage("请输入正确的参数");
        }

        return ResultUtil.successWithMessage(result);
    }

    private String buildBlogAggDoc() {
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        blogQueryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        blogQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        Page<Blog> page = new Page<>();
        long pageSize = 20L;
        long currentPage = 1L;
        int successCount = 0;
        int failCount = 0;
        while (true) {
            page.setCurrent(currentPage);
            page.setSize(pageSize);
            IPage<Blog> iPage = blogService.page(page, blogQueryWrapper);
            List<Blog> blogList = iPage.getRecords();
            if (blogList.size() == 0) {
                break;
            }
            blogList = blogService.setTagAndSortAndPictureByBlogList(blogList);
            blogList = blogService.convertBlogInfoList(blogList);
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (Blog blog : blogList) {
                // 判断是否是推广链接，如果是的话，直接跳出
                if (Constants.STR_ONE.equals(blog.getType())) {
                    continue;
                }
                AggEsDoc aggEsDoc = AggEsDocConvert.convertBlog(blog);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += blogList.size();
            } else {
                failCount += blogList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }

    /**
     * 构建问题聚合文档
     */
    private String buildQuestionAggDoc() {
        long pageSize = 20L;
        long currentPage = 1L;
        QuestionVO questionVO = new QuestionVO();
        int successCount = 0;
        int failCount = 0;
        while (true) {
            questionVO.setCurrentPage(currentPage);
            questionVO.setPageSize(pageSize);
            questionVO.setIsPublish(EPublish.PUBLISH);
            questionVO.setAuditStatus(EAuditStatus.AGREE);
            IPage<Question> iPage = questionService.getPageList(questionVO);
            List<Question> questionList = iPage.getRecords();
            if (questionList.size() == 0) {
                break;
            }
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (Question question : questionList) {
                AggEsDoc aggEsDoc = AggEsDocConvert.convertQuestion(question);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += questionList.size();
            } else {
                failCount += questionList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }

    /**
     * 构建动态索引
     *
     * @return
     */
    private String buildMomentAggDoc() {
        long pageSize = 20L;
        long currentPage = 1L;
        UserMomentVO userMomentVO = new UserMomentVO();
        int successCount = 0;
        int failCount = 0;
        while (true) {
            userMomentVO.setCurrentPage(currentPage);
            userMomentVO.setPageSize(pageSize);
            userMomentVO.setAuditStatus(EAuditStatus.AGREE);
            userMomentVO.setIsPublish(EPublish.PUBLISH);
            IPage<UserMoment> iPage = userMomentService.getPageList(userMomentVO);
            List<UserMoment> userMomentList = iPage.getRecords();
            if (userMomentList.size() == 0) {
                break;
            }
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (UserMoment userMoment : userMomentList) {
                AggEsDoc aggEsDoc = AggEsDocConvert.convertMoment(userMoment);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += userMomentList.size();
            } else {
                failCount += userMomentList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }


    /**
     * 构建面经索引
     *
     * @return
     */
    private String buildProblemAggDoc() {
        long pageSize = 20L;
        long currentPage = 1L;
        int successCount = 0;
        int failCount = 0;
        ProblemVO problemVO = new ProblemVO();
        while (true) {
            problemVO.setCurrentPage(currentPage);
            problemVO.setPageSize(pageSize);
            problemVO.setAuditStatus(EAuditStatus.AGREE);
            problemVO.setIsPublish(EPublish.PUBLISH);
            IPage<Problem> iPage = problemService.getPageList(problemVO);
            List<Problem> problemList = iPage.getRecords();
            if (problemList.size() == 0) {
                break;
            }
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (Problem problem : problemList) {
                AggEsDoc aggEsDoc = AggEsDocConvert.convertProblem(problem);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += problemList.size();
            } else {
                failCount += problemList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }

    /**
     * 构建资源索引
     *
     * @return
     */
    private String buildResourceAggDoc() {
        long pageSize = 20L;
        long currentPage = 1L;
        int successCount = 0;
        int failCount = 0;
        ResourceVO resourceVO = new ResourceVO();

        // 获取字典数据标签
        Map<String, String> dictDataLabelMap = sysDictDataService.getDictDataLabelMapByType(EDictType.SYS_RESOURCE_SORT.getType());
        while (true) {
            resourceVO.setCurrentPage(currentPage);
            resourceVO.setPageSize(pageSize);
            IPage<com.moxi.mogublog.commons.entity.Resource> iPage = resourceService.getPageList(resourceVO);
            List<com.moxi.mogublog.commons.entity.Resource> resourceList = iPage.getRecords();
            if (resourceList.size() == 0) {
                break;
            }
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (com.moxi.mogublog.commons.entity.Resource resource : resourceList) {
                resource.setName("【"+ dictDataLabelMap.get(String.valueOf(resource.getResourceSort())) + "】" + resource.getName());
                AggEsDoc aggEsDoc = AggEsDocConvert.convertResource(resource);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += resourceList.size();
            } else {
                failCount += resourceList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }

    /**
     * 构建课程索引
     *
     * @return
     */
    private String buildMediaAggDoc() {
        long pageSize = 20L;
        long currentPage = 1L;
        int successCount = 0;
        int failCount = 0;
        MediaInfoVO mediaInfoVO = new MediaInfoVO();
        mediaInfoVO.setStatus(EStatus.ENABLE);
        while (true) {
            mediaInfoVO.setCurrentPage(currentPage);
            mediaInfoVO.setPageSize(pageSize);
            IPage<MediaInfo> iPage = mediaInfoService.getPageList(mediaInfoVO);
            List<MediaInfo> mediaInfoList = iPage.getRecords();
            if (mediaInfoList.size() == 0) {
                break;
            }
            List<AggEsDoc> aggEsDocList = new ArrayList<>();
            for (MediaInfo mediaInfo : mediaInfoList) {
                AggEsDoc aggEsDoc = AggEsDocConvert.convertMedia(mediaInfo);
                aggEsDocList.add(aggEsDoc);
            }
            // 同步索引
            String result = searchFeignClient.batchCreateEsIndex(aggEsDocList);
            if (WebUtils.checkSuccess(result)) {
                successCount += mediaInfoList.size();
            } else {
                failCount += mediaInfoList.size();
            }
            log.info(result);
            currentPage++;
        }
        return String.format("同步索引执行完成，成功：%s 条，失败：%s 条", successCount, failCount);
    }


}
