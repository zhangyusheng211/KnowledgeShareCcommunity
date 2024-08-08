package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.entity.UserMoment;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mogublog.commons.vo.UserMomentVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.ESearchType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/search")
@Api(value = "SQL搜索相关接口", tags = {"SQL搜索相关接口"})
@Slf4j
public class SearchRestApi {
    @Autowired
    private BlogService blogService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ProblemService problemService;
    @Resource
    private SysParamsService sysParamsService;
    @Resource
    private UserMomentService userMomentService;
    @Resource
    private ResourceService resourceService;

    /**
     * 使用SQL语句搜索博客，如需使用Solr或者ElasticSearch 需要启动 mogu-search
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "搜索Blog", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "搜索Blog", notes = "搜索Blog")
    @GetMapping("/sqlSearchBlog")
    public String sqlSearchBlog(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                                @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 100) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~100个字符");
        }

        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogByKeyword(keywords, currentPage, Long.valueOf(searchSize)));
    }

    /**
     * 通过用户名搜索动态圈
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "通过用户名搜索动态圈", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "通过用户名搜索动态圈", notes = "通过用户名搜索动态圈")
    @GetMapping("/searchMomentByUser")
    public String searchBlogByUser(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                                   @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                   @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~20个字符");
        }
        if (!StringUtils.checkNormalStr(keywords)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索内容包含特殊字符");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);

        UserMomentVO userMomentVO = new UserMomentVO();
        userMomentVO.setKeyword(keywords);
        userMomentVO.setPageSize(Long.valueOf(searchSize));
        userMomentVO.setCurrentPage(currentPage);
        IPage<UserMoment> iPage = userMomentService.getPageList(userMomentVO);

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, iPage.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, iPage.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, iPage.getSize());
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, iPage.getCurrent());
        // 返回数据
        map.put(SysConf.BLOG_LIST, iPage.getRecords());

        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    /**
     * 通过用户名查询相关用户信息
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "通过用户名搜索用户", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "通过用户名搜索用户", notes = "通过用户名搜索用户")
    @GetMapping("/searchByUser")
    public String searchByUser(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                               @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                               @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~20个字符");
        }
        if (!StringUtils.checkNormalStr(keywords)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索内容包含特殊字符");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getByUser(keywords, currentPage, Long.valueOf(searchSize)));

    }

    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "问题搜索", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "问题搜索", notes = "问题搜索")
    @GetMapping("/searchProblem")
    public String searchProblem(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                                @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~20个字符");
        }
        if (!StringUtils.checkNormalStr(keywords)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索内容包含特殊字符");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, problemService.searchProblem(keywords, currentPage, Long.valueOf(searchSize)));
    }

    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "资源搜索", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "资源搜索", notes = "资源搜索")
    @GetMapping("/searchResource")
    public String searchResource(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                                @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~20个字符");
        }
        if (!StringUtils.checkNormalStr(keywords)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索内容包含特殊字符");
        }
        ResourceVO resourceVO = new ResourceVO();
        resourceVO.setCurrentPage(currentPage);
        resourceVO.setPageSize(pageSize);
        resourceVO.setKeyword(keywords);
        IPage<com.moxi.mogublog.commons.entity.Resource> resourceIPage = resourceService.getPageList(resourceVO);
        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, resourceIPage.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, resourceIPage.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, resourceIPage.getSize());
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, resourceIPage.getCurrent());
        // 返回数据
        map.put(SysConf.BLOG_LIST, resourceIPage.getRecords());
        return ResultUtil.result(SysConf.SUCCESS, map);
    }


    /**
     * 使用SQL语句搜索问答
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "搜索问答", behavior = EBehavior.BLOG_SEARCH)
    @ApiOperation(value = "搜索问答", notes = "搜索问答")
    @GetMapping("/sqlSearchQuestion")
    public String sqlSearchQuestion(@ApiParam(name = "keywords", value = "关键字", required = true) @RequestParam(required = true) String keywords,
                                    @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                    @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        if (keywords.trim().length() <= 1 || keywords.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在2~20个字符");
        }
        if (!StringUtils.checkNormalStr(keywords)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索内容包含特殊字符");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, questionService.getQuestionByKeyword(keywords, currentPage, Long.valueOf(searchSize)));
    }


    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "根据标签获取相关的博客", behavior = EBehavior.BLOG_TAG)
    @ApiOperation(value = "根据标签获取相关的博客", notes = "根据标签获取相关的博客")
    @GetMapping("/searchBlogByTag")
    public String searchBlogByTag(@ApiParam(name = "tagUid", value = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                  @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "标签不能为空");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, Long.valueOf(searchSize)));
    }

    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "根据分类获取相关的博客", behavior = EBehavior.BLOG_SORT)
    @ApiOperation(value = "根据分类获取相关的博客", notes = "根据标签获取相关的博客")
    @GetMapping("/searchBlogBySort")
    public String searchBlogBySort(@ApiParam(name = "blogSortUid", value = "博客分类UID", required = true) @RequestParam(name = "blogSortUid", required = true) String blogSortUid,
                                   @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                   @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "uid不能为空");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByBlogSort(blogSortUid, currentPage, Long.valueOf(searchSize)));
    }

    @AvoidRepeatableCommit(timeout = 500)
    @BussinessLog(value = "根据作者获取相关的博客", behavior = EBehavior.BLOG_AUTHOR)
    @ApiOperation(value = "根据作者获取相关的博客", notes = "根据作者获取相关的博客")
    @GetMapping("/searchBlogByAuthor")
    public String searchBlogByAuthor(@ApiParam(name = "author", value = "作者名称", required = true) @RequestParam(name = "author", required = true) String author,
                                     @ApiParam(name = "searchType", value = "搜索类型", required = false) @RequestParam(name = "searchType", required = false, defaultValue = "1") String searchType,
                                     @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                     @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(author)) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "作者不能为空");
        }
        if (author.trim().length() > 20) {
            return ResultUtil.resultWithMessage(SysConf.ERROR, "搜索长度必须在1~20个字符");
        }
        String searchSize = sysParamsService.getSysParamsValueByKey(SysConf.SYS_SEARCH_SIZE);
        if (ESearchType.BLOG.equals(searchType)) {
            return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByAuthor(author, currentPage, Long.valueOf(searchSize)));
        } else if (ESearchType.QUESTION.equals(searchType)) {
            return ResultUtil.result(SysConf.SUCCESS, questionService.getQuestionListByAuthor(author, currentPage, Long.valueOf(searchSize)));
        }
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "获取搜索模式", notes = "获取搜索模式", response = String.class)
    @GetMapping(value = "/getSearchModel")
    public String getSearchModel() {
        return ResultUtil.successWithData(systemConfigService.getSearchModel());
    }

}
