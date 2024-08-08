package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.internal.LinkedTreeMap;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.BlogManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户博客管理模块
 *
 * @author 遇见
 */
@Service(value = "userBlogManager")
@Slf4j
public class UserBlogManager implements BlogManager {
    /**
     * 博客服务
     */
    @Resource
    BlogService blogService;
    /**
     * 缓存工具
     */
    @Autowired
    private RedisUtil redisUtil;
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
     * 系统配置；服务
     */
    @Resource
    private SystemConfigService systemConfigService;
    /**
     * 博客分类服务
     */
    @Resource
    private BlogSortService blogSortService;
    /**
     * 标签服务
     */
    @Resource
    private TagService tagService;
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
        String addVerdictResult = this.addVerdict(count + 1, blogVO.getLevel());
        // 判断是否能够添加推荐
        if (StringUtils.isNotBlank(addVerdictResult)) {
            return addVerdictResult;
        }
        /**
         * 如果是原创，作者为用户的昵称
         */
        String projectName = sysParamsService.getSysParamsValueByKey(SysConf.PROJECT_NAME_);
        /**
         * 当为用户投稿的时候
         */
        String userUid = RequestHolder.getUserUid();
        /**
         * 查询当前用户
         */
        User user = userService.getById(userUid);
        if (user == null) {
            return ResultUtil.result(SysConf.ERROR, "用户未登录，无法投稿");
        }
        /**
         * 是否是普通用户
         */
        boolean isNormalUser = false;

        /**
         * 构建博客对象 【初始化默认配置】
         */
        blogVO.setVisitAuth(EVisitAuthType.PUBLIC.getType());
        blogVO.setLevel(0);
        Blog blog = blogVO.buildBlog(null, false, userUid.toString(), request.getAttribute(SysConf.USER_NAME).toString(), projectName);
        /**
         * 判断该用户是否超过今日投稿次数
         */
        Integer userMaxPublishCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.USER_PUBLISH_BLOG_COUNT));
        String countJson = redisUtil.get(RedisConf.USER_PUBLISH_BLOG_COUNT + Constants.SYMBOL_COLON + userUid);
        Integer userPublishCount = 0;
        if (StringUtils.isNotEmpty(countJson)) {
            userPublishCount = Integer.valueOf(countJson);
            if (userPublishCount >= userMaxPublishCount) {
                return ResultUtil.errorWithMessage("您发布文章次数已达今日上限");
            }
        }
        /**
         * 如果是选择发布的话 查看是否允许免审
         */
        if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
            // 判断用户是否具备免审权限
            String authCodeList = RequestHolder.getAuthCodeList();
            // 非普通用户，可直接免审核上架
            if (user != null && (user.getUserTag() > Constants.NUM_ZERO || authCodeList.contains(EAuthCode.BLOG_AUDIT.getCode()))) {
                blog.setAuditStatus(EAuditStatus.AGREE);
                blog.setAuditTime(new Date());
                isNormalUser = false;
            } else {
                blog.setAuditStatus(EAuditStatus.WAIT);
                isNormalUser = true;
            }
        } else {
            // 保存草稿，默认是
            blog.setAuditStatus(EAuditStatus.WAIT);
        }

        Boolean isSave = blogService.save(blog);
        /**
         * 保存成功才 ++;
         */
        if (isSave) {
            userPublishCount++;
            /**
             *   设置一天过期
             */
            redisUtil.setEx(RedisConf.USER_PUBLISH_BLOG_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(userPublishCount), 1, TimeUnit.DAYS);
        }


        if (isSave) {
            /**
             * 发布新增事件
             */
            domainEventUtil.publishEvent(EventAction.BLOG_ADD, blogService.setBlogInfo(blog));

            if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
                // 普通用户，提示需要审核
                if (isNormalUser) {
                    return ResultUtil.successWithMessage("博客提交成功，请等待管理员审核后上架~");
                } else {
                    // vip用户，直接免审
                    return ResultUtil.successWithMessage("博客发布成功，可以在 个人中心->我的博客，进行查看");
                }
            } else if (EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
                return ResultUtil.successWithMessage("保存草稿成功，可以在 个人中心->我的博客，进行提交发布！");
            }
        }


        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String uploadLocalBlog(List<MultipartFile> filedatas) {

        // 获取登录用户信息
        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        if (user.getCommentStatus() == Constants.NUM_ZERO) {
            throw new InsertException("账号已被禁言，无法进行投稿操作");
        }

        SystemConfig systemConfig = systemConfigService.getConfig();
        if (systemConfig == null) {
            return ResultUtil.errorWithMessage(MessageConf.SYSTEM_CONFIG_NOT_EXIST);
        } else {
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu()) && (StringUtils.isEmpty(systemConfig.getQiNiuPictureBaseUrl()) || StringUtils.isEmpty(systemConfig.getQiNiuAccessKey())
                    || StringUtils.isEmpty(systemConfig.getQiNiuSecretKey()) || StringUtils.isEmpty(systemConfig.getQiNiuBucket()) || StringUtils.isEmpty(systemConfig.getQiNiuArea()))) {
                return ResultUtil.errorWithMessage(MessageConf.PLEASE_SET_QI_NIU);
            }

            if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal()) && StringUtils.isEmpty(systemConfig.getLocalPictureBaseUrl())) {
                return ResultUtil.errorWithMessage(MessageConf.PLEASE_SET_LOCAL);
            }
        }

        List<MultipartFile> fileList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        for (MultipartFile file : filedatas) {
            String fileOriginalName = file.getOriginalFilename();
            if (FileUtils.isMarkdown(fileOriginalName)) {
                fileList.add(file);
                // 获取文件名
                fileNameList.add(FileUtils.getFileName(fileOriginalName));
            } else {
                return ResultUtil.errorWithMessage("目前仅支持Markdown文件");
            }
        }

        if (fileList.size() == 0) {
            return ResultUtil.errorWithMessage("请选中需要上传的文件");
        }

        // 文档解析
        List<String> fileContentList = new ArrayList<>();
        for (MultipartFile multipartFile : fileList) {
            try {
                Reader reader = new InputStreamReader(multipartFile.getInputStream(), "utf-8");
                BufferedReader br = new BufferedReader(reader);
                String line;
                String content = "";
                while ((line = br.readLine()) != null) {
                    content += line + "\n";
                }
                // 将Markdown转换成html
                String blogContent = FileUtils.markdownToHtml(content);
                fileContentList.add(blogContent);
            } catch (Exception e) {
                log.error("文件解析出错");
                log.error(e.getMessage());
            }
        }

        HttpServletRequest request = RequestHolder.getRequest();
        String pictureList = request.getParameter(SysConf.PICTURE_LIST);
        List<LinkedTreeMap<String, String>> list = (List<LinkedTreeMap<String, String>>) JsonUtils.jsonArrayToArrayList(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        for (LinkedTreeMap<String, String> item : list) {

            if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                // 获取七牛云上的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.QI_NIU_URL));
            } else if (EFilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                // 获取本地的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.PIC_URL));
            } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                // 获取MINIO的图片
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.MINIO_URL));

            } else if (EFilePriority.ALIOSS.equals(systemConfig.getContentPicturePriority())) {
                pictureMap.put(item.get(SysConf.FILE_OLD_NAME), item.get(SysConf.ALIOSS_URL));
            }
        }
        // 需要替换的图片Map
        Map<String, String> matchUrlMap = new HashMap<>();
        for (String blogContent : fileContentList) {
            List<String> matchList = RegexUtils.match(blogContent, "<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)>");
            for (String matchStr : matchList) {
                String[] splitList = matchStr.split("\"");
                // 取出中间的图片
                if (splitList.length >= 5) {
                    // alt 和 src的先后顺序
                    // 得到具体的图片路径
                    String pictureUrl = "";
                    if (matchStr.indexOf("alt") > matchStr.indexOf("src")) {
                        pictureUrl = splitList[1];
                    } else {
                        pictureUrl = splitList[3];
                    }

                    // 判断是网络图片还是本地图片
                    if (!pictureUrl.startsWith(SysConf.HTTP)) {
                        // 那么就需要遍历全部的map和他匹配
                        for (Map.Entry<String, String> map : pictureMap.entrySet()) {
                            // 查看Map中的图片是否在需要替换的key中
                            if (pictureUrl.indexOf(map.getKey()) > -1) {
                                if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取七牛云上的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getQiNiuPictureBaseUrl() + map.getValue());
                                } else if (EFilePriority.LOCAL.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取本地的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getLocalPictureBaseUrl() + map.getValue());
                                } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
                                    // 获取MINIO的图片
                                    matchUrlMap.put(pictureUrl, systemConfig.getMinioPictureBaseUrl() + map.getValue());
                                } else if (EFilePriority.ALIOSS.equals(systemConfig.getContentPicturePriority())) {
                                    matchUrlMap.put(pictureUrl, systemConfig.getAliossPictureBaseUrl() + map.getValue());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        // 获取一个排序最高的博客分类和标签
        BlogSort blogSort = blogSortService.getTopOne();
        Tag tag = tagService.getTopTag();

        if (blogSort == null || tag == null) {
            return ResultUtil.errorWithMessage("使用本地上传，请先确保博客分类和博客标签中含有数据");
        }

        // 存储需要上传的博客
        List<Blog> blogList = new ArrayList<>();
        // 开始进行图片替换操作
        Integer count = 0;
        String projectName = sysParamsService.getSysParamsValueByKey(SysConf.PROJECT_NAME_);

        for (String content : fileContentList) {
            // 循环替换里面的图片
            for (Map.Entry<String, String> map : matchUrlMap.entrySet()) {
                content = content.replace(map.getKey(), map.getValue());
            }
            Blog blog = new Blog();
            blog.setBlogSortUid(blogSort.getUid());
            blog.setTagUid(tag.getUid());
            blog.setUserUid(userUid);
            blog.setArticlesPart(projectName);
            blog.setLevel(ELevel.NORMAL);
            blog.setTitle(fileNameList.get(count));
            // 文章类型只能是博客类型
            blog.setType(Constants.STR_ZERO);

            // 前端投稿
            blog.setArticleSource(EContributeSource.USER_PUBLISH);
            blog.setAuthor(user.getNickName());

            /**
             * 判断该用户是否超过今日投稿次数
             */
            Integer userMaxPublishCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.USER_PUBLISH_BLOG_COUNT));
            String countJson = redisUtil.get(RedisConf.USER_PUBLISH_BLOG_COUNT + Constants.SYMBOL_COLON + userUid);
            Integer userPublishCount = 0;
            if (StringUtils.isNotEmpty(countJson)) {
                userPublishCount = Integer.valueOf(countJson);
                if (userPublishCount >= userMaxPublishCount) {
                    return ResultUtil.errorWithMessage("您发布文章次数已达今日上限");
                }
            }
            userPublishCount++;
            /**
             *   设置一天过期
             */
            redisUtil.setEx(RedisConf.USER_PUBLISH_BLOG_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(userPublishCount), 1, TimeUnit.DAYS);

            /**
             * 如果是选择发布的话 查看是否允许免审
             */
            if (EPublish.PUBLISH.equals(blog.getIsPublish())) {
                // 非普通用户，可直接免审核【需要用户手动设置上架】
                if (user != null && user.getUserTag() > Constants.NUM_ZERO) {
                    blog.setAuditStatus(EAuditStatus.AGREE);
                    blog.setAuditTime(new Date());
                } else {
                    blog.setAuditStatus(EAuditStatus.WAIT);
                }
            }

            blog.setAuditStatus(EAuditStatus.AGREE);
            // 用户投稿，默认不开启加载校验
            blog.setOpenLoadingValid(EOpenStatus.CLOSE);

            // 设置简介
            String summary = StringUtils.dealContent(content);
            if (summary.length() < 190) {
                blog.setSummary(summary);
            } else {
                blog.setSummary(summary.substring(0, 190) + "...");
            }
            blog.setContent(content);
            // 先不设置标题图了
            blog.setFileUid("");
            blog.setIsOriginal(EOriginal.ORIGINAL);
            blog.setIsPublish(EPublish.NO_PUBLISH);
            blog.setOpenComment(EOpenStatus.OPEN);
            blog.setType(Constants.STR_ZERO);
            // 默认所有人可见
            blog.setVisitAuth(EVisitAuthType.PUBLIC.getType());
            blogList.add(blog);
            count++;

        }
        // 批量添加博客
        blogService.saveBatch(blogList);

        // TODO 后期改成异步发送
        for (Blog blog : blogList) {
            /**
             * 发布新增事件
             */
            domainEventUtil.publishEvent(EventAction.BLOG_ADD, blogService.setBlogInfo(blog));
        }
        return ResultUtil.successWithMessage("博客上传成功，请在个人中心->我的文章进行提交上架吧~");
    }


    @Override
    public String editBlog(BlogVO blogVO) {

        HttpServletRequest request = RequestHolder.getRequest();
        /**
         * 判断用户是否能编辑博客
         */
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if (userUid == null) {
            throw new UpdateException("用户未登录，无法编辑文章");
        }
        Blog blog = blogService.getById(blogVO.getUid());
        if (!userUid.equals(blog.getUserUid())) {
            throw new UpdateException("您无权编辑其它用户的文章");
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.LEVEL, blogVO.getLevel());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer count = blogService.count(queryWrapper);
        if (blog != null) {
            //传递过来的和数据库中的不同，代表用户已经修改过等级了，那么需要将count数加1
            if (!blog.getLevel().equals(blogVO.getLevel())) {
                count += 1;
            }
        }
        /**
         * 查询当前用户
         */
        User user = userService.getById(userUid.toString());
        /**
         * 是否是普通用户
         */
        Boolean isNormalUser = false;
        String addVerdictResult = addVerdict(count, blogVO.getLevel());
        //添加的时候进行判断
        if (StringUtils.isNotBlank(addVerdictResult)) {
            return addVerdictResult;
        }
        //如果是原创，作者为用户的昵称
        String projectName = sysParamsService.getSysParamsValueByKey(SysConf.PROJECT_NAME_);
        Blog newBlog = blogVO.buildBlog(blog, false, userUid.toString(), request.getAttribute(SysConf.USER_NAME).toString(), projectName);
        newBlog.setUid(blog.getUid());
        // 判断是否原创
        if (EOriginal.ORIGINAL.equals(blogVO.getIsOriginal())) {
            newBlog.setAuthor(request.getAttribute(SysConf.USER_NAME).toString());
            newBlog.setArticlesPart(projectName);
        } else {
            newBlog.setAuthor(blogVO.getAuthor());
            newBlog.setArticlesPart(blogVO.getArticlesPart());
        }
        /**
         * 当修改之后 要求上架
         */
        if (EPublish.PUBLISH.equals(newBlog.getIsPublish())) {
            /**
             * 原博客 审核状态如果不是 未审核
             * 需要将审核状态初始为未审核
             */
            // 非普通用户，可直接免审核上架
            if (user != null && user.getUserTag() > Constants.NUM_ZERO) {
                newBlog.setAuditStatus(EAuditStatus.AGREE);
                newBlog.setAuditTime(new Date());
                isNormalUser = false;
            } else {
                newBlog.setAuditStatus(EAuditStatus.WAIT);
                isNormalUser = true;
            }
        }

        Boolean isSave = newBlog.updateById();
        /**
         * 发布修改事件
         */
        domainEventUtil.publishEvent(EventAction.BLOG_EDIT, newBlog);
        if (isSave && EPublish.PUBLISH.equals(newBlog.getIsPublish())) {

            if (isNormalUser) {
                return ResultUtil.successWithMessage("博客提交成功，请等待管理员审核后上架~");
            } else {
                return ResultUtil.successWithMessage("博客发布成功，可以在 个人中心->我的博客，进行查看");
            }
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String publish(BlogVO blogVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        Blog blog = blogService.getOne(new LambdaQueryWrapper<Blog>()
                .eq(Blog::getUid, blogVO.getUid())
        );
        // 判断用户是否能编辑博客
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if (userUid == null) {
            throw new UpdateException("用户未登录，无法发布/下架文章");
        }
        if (!userUid.equals(blog.getUserUid())) {
            throw new UpdateException("您无权发布/下架文章");
        }
        /**
         * 空判
         */
        if (blog == null) {
            throw new UpdateException("未查询到博客信息,请刷新页面后操作");
        }
        /**
         * 要求上架
         */
        if (EPublish.PUBLISH.equals(blogVO.getIsPublish())) {
            /**
             * 查询当前用户
             */
            User user = userService.getById(userUid.toString());
            /**
             * 非普通用户，可直接免审核上架
             */
            if (user != null && user.getUserTag() > Constants.NUM_ZERO) {
                blog.setAuditStatus(EAuditStatus.AGREE);
                blog.setAuditTime(new Date());
            }
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
            /**
             * 发布上下架事件
             */
            domainEventUtil.publishEvent(EventAction.BLOG_PUBLISH, blog);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBlog(BlogVO blogVO) {
        // 判断用户是否能编辑博客
        HttpServletRequest request = RequestHolder.getRequest();
        // 判断用户是否能编辑博客
        Object userUid = request.getAttribute(SysConf.USER_UID);
        if (userUid == null) {
            throw new UpdateException("用户未登录，无法发布/下架文章");
        }

        Blog blog = blogService.getById(blogVO.getUid());
        if (!userUid.equals(blog.getUserUid())) {
            throw new UpdateException("您无权删除其它用户的文章");
        }
        blog.setStatus(EStatus.DISABLED);
        Boolean save = blog.updateById();

        //保存成功后，需要发送消息到solr 和 redis, 同时从专题管理Item中移除该博客
        if (save) {
            /**
             * 发布删除事件
             */
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
