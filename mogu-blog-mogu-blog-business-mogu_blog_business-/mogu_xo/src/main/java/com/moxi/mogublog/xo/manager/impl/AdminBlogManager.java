package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.BlogManager;
import com.moxi.mogublog.xo.service.AdminService;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author 遇见
 */
@Service(value = "adminBlogManager")
@Slf4j
public class AdminBlogManager implements BlogManager {

    /**
     * 博客服务
     */
    @Resource
    BlogService blogService;
    /**
     * 系统参数服务
     */
    @Resource
    private SysParamsService sysParamsService;
    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 管理员服务
     */
    @Resource
    private AdminService adminService;

    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public String addBlog(BlogVO blogVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.LEVEL, blogVO.getLevel());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer count = blogService.count(queryWrapper);
        // 判断插入博客的时候，会不会超过预期设置
        String addVerdictResult = addVerdict(count + 1, blogVO.getLevel());
        // 判断是否能够添加推荐
        if (StringUtils.isNotBlank(addVerdictResult)) {
            return addVerdictResult;
        }
        //如果是原创，作者为用户的昵称
        String projectName = sysParamsService.getSysParamsValueByKey(SysConf.PROJECT_NAME_);
        // 当为后台管理员添加的时候
        Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());
        if (admin == null) {
            return ResultUtil.errorWithMessage("管理员状态错误,请重新登录后发布!");
        }
        // 判断是否附身用户
        if (StringUtils.isEmpty(admin.getUserUid())) {
            return ResultUtil.errorWithMessage("您暂未附身用户，无法发表文章，请到 管理员管理-> 编辑, 选择要附身用户 再重新发表");
        }

        /**
         * 构建博客对象
         */
        Blog blog = blogVO.buildBlog(null, true, admin.getUid(), admin.getUserName(), projectName);

        // 判断是否是原创
        Boolean isOriginal = true;
        if (!EOriginal.ORIGINAL.equals(blogVO.getIsOriginal())) {
            isOriginal = false;
            blog.setAuthor(blogVO.getAuthor());
            blog.setArticlesPart(blogVO.getArticlesPart());
        } else {
            // 设置网站名称
            blog.setArticlesPart(projectName);
        }

        if (admin != null) {
            // 判断该管理员是否进行了权限附身
            Boolean isUserOp = false;
            if (StringUtils.isNotEmpty(admin.getUserUid())) {
                User user = userService.getById(admin.getUserUid());
                if (user != null) {
                    // 原创，设置作者
                    if (isOriginal) {
                        blog.setAuthor(user.getNickName());
                    }
                    blog.setUserUid(user.getUid());
                    blog.setUser(user);
                    // 被权限附身的管理员，改成以用户投稿的方式上传文章
                    blog.setArticleSource(EContributeSource.USER_PUBLISH);
                    isUserOp = true;
                }
            }
            // 未开启权限附身； 判断是否是原创，设置作者
            if (!isUserOp && isOriginal) {
                if (StringUtils.isNotEmpty(admin.getNickName())) {
                    blog.setAuthor(admin.getNickName());
                } else {
                    blog.setAuthor(admin.getUserName());
                }
            }
        }

        /**
         * 管理员新增无需审核 且自动发布
         */
        blog.setAuditStatus(EAuditStatus.AGREE);
        blog.setAuditTime(new Date());
        blog.setIsPublish(EPublish.PUBLISH);

        /**
         * 保存
         */
        Boolean isSave = blogService.save(blog);
        /**
         * 发布新增领域事件
         */
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.BLOG_ADD, blogService.setBlogInfo(blog));
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String uploadLocalBlog(List<MultipartFile> filedatas) {
        return null;
    }

    @Override
    public String editBlog(BlogVO blogVO) {

        HttpServletRequest request = RequestHolder.getRequest();
        Admin admin = adminService.getById(request.getAttribute(SysConf.ADMIN_UID).toString());
        if (admin == null) {
            return ResultUtil.errorWithMessage("管理员状态错误,请重新登录后发布!");
        }

        Blog blog = blogService.getById(blogVO.getUid());
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.LEVEL, blogVO.getLevel());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer count = blogService.count(queryWrapper);
        if (blog != null) {
            //传递过来的和数据库中的不同，代表用户已经修改过等级了，那么需要将count数加1
            if (!blog.getLevel().equals(blogVO.getLevel())) {
                count += 1;
            }
        } else {
            return ResultUtil.errorWithMessage("编辑的博客不存在");
        }

        // 未通过审核的文章，无法设置推荐
        if (blogVO.getLevel() != null && blogVO.getLevel() > 0 && !EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
            return ResultUtil.errorWithMessage("文章未通过审核，无法设置推荐等级!");
        }

        String addVerdictResult = addVerdict(count, blogVO.getLevel());
        //添加的时候进行判断
        if (StringUtils.isNotBlank(addVerdictResult)) {
            return addVerdictResult;
        }
        Blog newBlog = blogVO.buildBlog(blog, true, admin.getUid(), blogVO.getAuthor(), blogVO.getArticlesPart());
        Boolean isSave = newBlog.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.BLOG_EDIT, newBlog);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String publish(BlogVO blogVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Blog blog = blogService.getOne(new LambdaQueryWrapper<Blog>()
                .eq(Blog::getUid, blogVO.getUid())
        );

        if (!EContributeSource.ADMIN_PUBLISH.equals(blogVO.getArticleSource())) {
            // 判断用户是否能编辑博客
            Object userUid = request.getAttribute(SysConf.USER_UID);
            if (userUid == null) {
                throw new UpdateException("用户未登录，无法发布/下架文章");
            }
            if (!userUid.equals(blog.getUserUid())) {
                throw new UpdateException("您无权发布/下架文章");
            }

        }
        /**
         * 空判
         */
        if (blog == null) {
            throw new UpdateException("未查询到博客信息,请刷新页面后操作");
        }
        /**
         * 审核判断
         */
        if (!EAuditStatus.AGREE.equals(blog.getAuditStatus())) {
            throw new UpdateException("博客未审核或审核未通过,无法进行发布操作");
        }
        blog.setIsPublish(blogVO.getIsPublish());
        Boolean isSave = blog.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.BLOG_PUBLISH, blog);
        }

        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBlog(BlogVO blogVO) {
        Blog blog = blogService.getById(blogVO.getUid());
        blog.setAuditStatus(EAuditStatus.REJECT);
        blog.setIsPublish(EPublish.NO_PUBLISH);
        blog.setStatus(EStatus.DISABLED);
        Boolean save = blog.updateById();

        //保存成功后，需要发送消息到solr 和 redis, 同时从专题管理Item中移除该博客
        if (save) {
            // 发布删除事件
            domainEventUtil.publishEvent(EventAction.BLOG_DELETE, blog);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }


    /**
     * 添加时校验
     *
     * @param count
     * @param level
     * @return
     */
    private String addVerdict(Integer count, Integer level) {

        //添加的时候进行判断
        switch (level) {
            case ELevel.FIRST: {
                Long blogFirstCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FIRST_COUNT));
                if (count > blogFirstCount) {
                    return ResultUtil.errorWithMessage("一级推荐不能超过" + blogFirstCount + "个");
                }
            }
            break;

            case ELevel.SECOND: {
                Long blogSecondCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT));
                if (count > blogSecondCount) {
                    return ResultUtil.errorWithMessage("二级推荐不能超过" + blogSecondCount + "个");
                }
            }
            break;

            case ELevel.THIRD: {
                Long blogThirdCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_THIRD_COUNT));
                if (count > blogThirdCount) {
                    return ResultUtil.errorWithMessage("三级推荐不能超过" + blogThirdCount + "个");
                }
            }
            break;

            case ELevel.FOURTH: {
                Long blogFourthCount = Long.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FOURTH_COUNT));
                if (count > blogFourthCount) {
                    return ResultUtil.errorWithMessage("四级推荐不能超过" + blogFourthCount + "个");
                }
            }
            break;
            default: {

            }
        }
        return null;
    }

}
