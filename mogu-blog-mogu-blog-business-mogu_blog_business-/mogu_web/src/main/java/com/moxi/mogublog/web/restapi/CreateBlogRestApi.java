package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.WebConfig;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.PublishLimitVerify.PublishLimitVerify;
import com.moxi.mogublog.web.annotion.SubmitVerify.SubmitVerify;
import com.moxi.mogublog.web.annotion.checkRegexVerify.CheckRegexVerify;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.factory.BlogFactory;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.BlogSortService;
import com.moxi.mogublog.xo.service.TagService;
import com.moxi.mogublog.xo.service.WebConfigService;
import com.moxi.mogublog.commons.vo.BlogSortVO;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mogublog.commons.vo.TagVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 博客创作相关
 *
 * @author: 陌溪
 * @create: 2021-04-11-9:58
 */
@RestController
@RefreshScope
@RequestMapping("/createBlog")
@Api(value = "博客创作相关接口", tags = {"博客创作相关接口"})
@Slf4j
public class CreateBlogRestApi {

    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private TagService tagService;
    @Autowired
    private WebConfigService webConfigService;

    @ApiOperation(value = "获取用户的博客列表", notes = "获取用户的博客列表", response = String.class)
    @PostMapping("/getUserBlogList")
    public String getUserBlogList(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }

    @ApiOperation(value = "获取当前用户的博客列表", notes = "获取用户的博客列表", response = String.class)
    @PostMapping("/getMeBlogList")
    public String getMeBlogList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 前端没有传递用户UID时，将查询在线用户的博客列表
        blogVO.setUserUid(RequestHolder.getUserUid());
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }


    @CheckRegexVerify(behavior = ERegexType.BLOG_PUBLISH)
    @PublishLimitVerify(behavior = EPublishLimitType.BLOG_COUNT)
    @SubmitVerify
    @UserAuth(EAuthCode.ADD_BLOG)
    @BussinessLog(value = "增加博客", behavior = EBehavior.ADD_BLOG)
    @AvoidRepeatableCommit
    @ApiOperation(value = "增加博客", notes = "增加博客", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Default.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        log.info("增加博客");
        // 判断是否开启投稿功能
        WebConfig webConfig = webConfigService.getWebConfig();
        if (Constants.STR_ZERO.equals(webConfig.getOpenCreateBlog())) {
            return ResultUtil.errorWithMessage("后台暂未开启投稿功能");
        }
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        // 文章类型只能是博客类型
        blogVO.setType(Constants.STR_ZERO);
        // 推荐类型默认为正常
        blogVO.setLevel(ELevel.NORMAL);
        blogVO.setArticleSource(EContributeSource.USER_PUBLISH);
        blogVO.setOpenLoadingValid(EOpenStatus.CLOSE);
        return BlogFactory.build(EContributeSource.USER_PUBLISH).addBlog(blogVO);
    }

    @UserAuth(EAuthCode.UPLOAD_BLOG)
    @SubmitVerify
    @BussinessLog(value = "本地博客上传", behavior = EBehavior.UPLOAD_BLOG)
    @AvoidRepeatableCommit
    @ApiOperation(value = "本地博客上传", notes = "本地博客上传", response = String.class)
    @PostMapping("/uploadLocalBlog")
    public String uploadLocalBlog(@RequestBody List<MultipartFile> filedatas) {
        log.info("本地博客上传");
        return BlogFactory.build(EContributeSource.USER_PUBLISH).uploadLocalBlog(filedatas);
    }

    @UserAuth(EAuthCode.ADD_BLOG)
    @BussinessLog(value = "编辑博客", behavior = EBehavior.EDIT_BLOG)
    @AvoidRepeatableCommit
    @ApiOperation(value = "编辑博客", notes = "编辑博客", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Default.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑博客");
        // 文章类型只能是博客类型
        blogVO.setType(Constants.STR_ZERO);
        blogVO.setArticleSource(EContributeSource.USER_PUBLISH);
        return BlogFactory.build(EContributeSource.USER_PUBLISH).editBlog(blogVO);
    }

    @UserAuth(EAuthCode.ADD_BLOG)
    @BussinessLog(value = "发布/下架博客", behavior = EBehavior.PUBLISH_BLOG)
    @AvoidRepeatableCommit
    @ApiOperation(value = "发布/下架博客", notes = "发布/下架博客", response = String.class)
    @PostMapping("/publish")
    public String publish(@RequestBody BlogVO blogVO) {
        log.info("发布/下架博客");
        blogVO.setArticleSource(EContributeSource.USER_PUBLISH);
        return BlogFactory.build(EContributeSource.USER_PUBLISH).publish(blogVO);
    }

    @UserAuth(EAuthCode.ADD_BLOG)
    @BussinessLog(value = "删除博客", behavior = EBehavior.DELETE_BLOG)
    @AvoidRepeatableCommit
    @ApiOperation(value = "删除博客", notes = "删除博客", response = String.class)
    @PostMapping("/delete")
    public String delete( @Validated({Delete.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        if (StringUtils.isEmpty(RequestHolder.getUserUid())) {
            return ResultUtil.errorWithMessage("登录后才可以删除博客！");
        }
        log.info("删除博客");
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        // 文章类型只能是博客类型
        blogVO.setType(Constants.STR_ZERO);
        blogVO.setArticleSource(EContributeSource.USER_PUBLISH);
        return BlogFactory.build(EContributeSource.USER_PUBLISH).deleteBlog(blogVO);
    }

    /**
     * 获取博客分类列表
     * @param blogSortVO
     * @param result
     * @return
     */
    @ApiOperation(value = "获取博客分类列表", notes = "获取博客分类列表", response = String.class)
    @PostMapping("/getBlogSortList")
    public String getBlogSortList(@Validated({GetList.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取博客分类列表");
        return ResultUtil.successWithData(blogSortService.getPageList(blogSortVO));
    }

    /**
     * 获取标签列表
     * @param tagVO
     * @param result
     * @return
     */
    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getBlogTagList")
    public String getList(@Validated({GetList.class}) @RequestBody TagVO tagVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, tagService.getPageList(tagVO));
    }

    /**
     * 随机一文
     * @return
     */
    @ApiOperation(value = "随机一文", notes = "随机一文", response = String.class)
    @PostMapping("/randomBlog")
    public String randomBlog() {
        log.info("随机一文");
        return ResultUtil.successWithData(blogService.randomBlog());
    }

    /**
     * 查询文章发表积分榜
     * @return
     */
    @ApiOperation(value = "查询文章发表积分榜", notes = "查询文章发表积分榜")
    @GetMapping(value = "/getLeaderBlog")
    public String getLeaderBlog(@ApiParam(name = "refresh", value = "是否刷新配置", required = false) @RequestParam(name = "refresh", required = false, defaultValue = "false") Boolean refresh) {
        List<Blog> leaderWeek = blogService.getLeaderBlog(refresh);
        return ResultUtil.result(SysConf.SUCCESS, leaderWeek);
    }
}
