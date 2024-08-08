package com.moxi.mogublog.admin.restapi;


import com.moxi.mogublog.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.moxi.mogublog.admin.annotion.OperationLogger.OperationLogger;
import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.xo.factory.BlogFactory;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客表 RestApi
 *
 * @author 陌溪
 * @date 2018-09-08
 */

@RestController
@RequestMapping("/blog")
@Api(value = "博客相关接口", tags = {"博客相关接口"})
@Slf4j
public class BlogRestApi {

    @Resource
    private BlogService blogService;

    @Resource
    private CommonManager commonManager;


    @AuthorityVerify
    @ApiOperation(value = "获取博客列表", notes = "获取博客列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 是否管理端查看
        blogVO.setIsBlack(Constants.NUM_ONE);
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加博客")
    @ApiOperation(value = "增加博客", notes = "增加博客", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        blogVO.setArticleSource(EContributeSource.ADMIN_PUBLISH);
        blogVO.setOpenLoadingValid(EOpenStatus.OPEN);
        // 后台上架，都是审批通过
        blogVO.setAuditStatus(EAuditStatus.AGREE);
        return BlogFactory.build(EContributeSource.ADMIN_PUBLISH).addBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "本地博客上传")
    @ApiOperation(value = "本地博客上传", notes = "本地博客上传", response = String.class)
    @PostMapping("/uploadLocalBlog")
    public String uploadPics(@RequestBody List<MultipartFile> filedatas) throws IOException {
        return blogService.uploadLocalBlog(filedatas);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑博客")
    @ApiOperation(value = "编辑博客", notes = "编辑博客", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return BlogFactory.build(EContributeSource.ADMIN_PUBLISH).editBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "发布/下架博客")
    @ApiOperation(value = "发布/下架博客", notes = "发布/下架博客", response = String.class)
    @PostMapping("/publish")
    public String publish(@RequestBody BlogVO blogVO) {
        blogVO.setArticleSource(EContributeSource.ADMIN_PUBLISH);
        return BlogFactory.build(EContributeSource.ADMIN_PUBLISH).publish(blogVO);
    }


    @AuthorityVerify
    @OperationLogger(value = "推荐博客排序调整")
    @ApiOperation(value = "推荐博客排序调整", notes = "推荐博客排序调整", response = String.class)
    @PostMapping("/editBatch")
    public String editBatch(@RequestBody List<BlogVO> blogVOList) {
        return blogService.editBatch(blogVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除博客")
    @ApiOperation(value = "删除博客", notes = "删除博客", response = String.class)
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        blogVO.setArticleSource(EContributeSource.ADMIN_PUBLISH);
        return BlogFactory.build(EContributeSource.ADMIN_PUBLISH).deleteBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除选中博客")
    @ApiOperation(value = "删除选中博客", notes = "删除选中博客", response = String.class)
    @PostMapping("/deleteBatch")
    public String deleteBatch(@RequestBody List<BlogVO> blogVoList) {
        return blogService.deleteBatchBlog(blogVoList);
    }

    @AuthorityVerify
    @OperationLogger(value = "审核博客")
    @ApiOperation(value = "审核博客", notes = "审核博客", response = String.class)
    @PostMapping("/audit")
    public String audit(@RequestBody BlogVO blogVO) {
        return blogService.auditBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量审核博客")
    @ApiOperation(value = "批量审核博客", notes = "批量审核博客", response = String.class)
    @PostMapping("/batchAuditBlog")
    public String batchAuditBlog(@RequestBody List<BlogVO> blogVoList) {
        return blogService.batchAuditBlog(blogVoList);
    }

    @AuthorityVerify
    @OperationLogger(value = "博客简介刷数")
    @ApiOperation(value = "博客简介刷数", notes = "博客简介刷数", response = String.class)
    @PostMapping("/flushBlogSummary")
    public String flushBlogSummary() {
        return blogService.flushBlogSummary();
    }

    @AuthorityVerify
    @OperationLogger(value = "推送选择博客")
    @ApiOperation(value = "推送选择博客", notes = "推送选择博客", response = String.class)
    @PostMapping("/pushBatch")
    public String pushBatch(@RequestBody List<BlogVO> blogVoList) {
        return commonManager.pushSEOByResource(EResourceType.BLOG, blogVoList.stream().map(BlogVO::getUid).collect(Collectors.toList()));
    }

}
