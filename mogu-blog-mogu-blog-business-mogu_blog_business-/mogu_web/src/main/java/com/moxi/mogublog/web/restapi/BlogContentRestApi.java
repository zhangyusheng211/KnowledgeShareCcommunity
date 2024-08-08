package com.moxi.mogublog.web.restapi;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.manager.UserPraiseRecordManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.WebUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.holder.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章详情 RestApi
 *
 * @author 陌溪
 * @date 2018-09-04
 */
@RestController
@RefreshScope
@RequestMapping("/content")
@Api(value = "文章详情相关接口", tags = {"文章详情相关接口"})
@Slf4j
public class BlogContentRestApi {
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private BlogService blogService;
    @Autowired
    private SubjectItemService subjectItemService;
    @Autowired
    private SubjectService subjectService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AdminService adminService;
    @Resource
    private UserService userService;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectService collectService;
    @Resource
    private UserPraiseRecordManager userPraiseRecordManager;
    @Resource
    private SysParamsService sysParamsService;

    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;

    @ApiOperation(value = "通过Uid获取博客内容", notes = "通过Uid获取博客内容")
    @GetMapping("/getBlogByUid")
    public String getBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid,
                               @ApiParam(name = "isLazy", value = "是否开启图片懒加载", required = false) @RequestParam(name = "isLazy", required = false, defaultValue = "0") String isLazy,
                               @ApiParam(name = "oid", value = "博客OID", required = false) @RequestParam(name = "oid", required = false, defaultValue = "0") Integer oid,
                               @ApiParam(name = "subjectUid", value = "专栏UID", required = false) @RequestParam(name = "subjectUid", required = false, defaultValue = "") String subjectUid) {
        String userUid = RequestHolder.getUserUid();
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        if (StringUtils.isEmpty(uid) && oid == 0) {
            log.error("[getBlogByUid] 文章ID不能为空");
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }

        Blog blog = null;
        if (StringUtils.isNotEmpty(uid)) {
            blog = blogService.getById(uid);
        } else {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SysConf.OID, oid);
            queryWrapper.last(SysConf.LIMIT_ONE);
            blog = blogService.getOne(queryWrapper);
        }
        if(blog == null) {
            log.error("[getBlogByUid] 抱歉，文章不存在！, blogOid: {}, blogUid: {}", oid, uid);
            throw new BusinessException("抱歉，文章不存在！");
        }

        // 判断是否是登录的用户在预览自己的博客
        boolean isMyBlog = false;
        if (StringUtils.isNotEmpty(userUid)) {
            if (blog.getUserUid().equals(userUid)) {
                isMyBlog = true;
            } else {
                // 判断当前用户是否被附身操作
                QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
                queryWrapper.eq(SQLConf.USER_UID, userUid);
                int count = adminService.count(queryWrapper);
                isMyBlog = count > 0;
            }
        }

        // 自己的博客，可以预览进行未发布的预览，否则无法打开
        if (!isMyBlog && (blog.getStatus() == EStatus.DISABLED ||
                EPublish.NO_PUBLISH.equals(blog.getIsPublish()) ||
                !EAuditStatus.AGREE.equals(blog.getAuditStatus()))) {
            log.error("[getBlogByUid] 博客暂未发布或已被删除， blogUid: {}", blog.getUid());
            return ResultUtil.result(ECode.ERROR, MessageConf.BLOG_IS_DELETE);
        }

        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = stringRedisTemplate.opsForValue().get("BLOG_CLICK:" + ip + "#" + blog.getUid());
        if (StringUtils.isEmpty(jsonResult)) {
            //给博客点击数增加
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            blog.updateById();
            //将该用户点击记录存储到redis中, 第二天失效
            stringRedisTemplate.opsForValue().set(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + blog.getUid(), blog.getClickCount().toString(),
                    DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);

            // 过滤掉内容和简介【拷贝个对象，过滤掉无效信息】
            Blog tempBlog = new Blog();
            tempBlog.setUid(blog.getUid());
            tempBlog.setUserUid(blog.getUserUid());
            // 发布首次访问的事件
            domainEventUtil.publishEvent(EventAction.BLOG_VISIT, tempBlog);
        }

        // 设置文章版权申明
        setBlogCopyright(blog);

        // 设置博客信息
        blog = blogService.setBlogInfo(blog);

        // 设置用户发表的博客数以及访问数
        if (blog.getUser() != null) {
            Integer blogPublishCount = 0;
            Integer blogVisitCount = 0;
            if (EContributeSource.ADMIN_PUBLISH.equals(blog.getArticleSource())) {
                blogPublishCount = adminService.getBlogPublishCount(blog.getAdminUid());
                blogVisitCount = adminService.getBlogVisitCount(blog.getAdminUid());
            } else {
                blogPublishCount = userService.getBlogPublishCount(blog.getUserUid());
                blogVisitCount = userService.getBlogVisitCount(blog.getUserUid());
            }
            User user = blog.getUser();
            user.setBlogPublishCount(blogPublishCount);
            user.setBlogVisitCount(blogVisitCount);
            blog.setUser(user);
        }

        //设置博客标题图
        setPhotoListByBlog(blog);

        // 设置专题列表
        boolean checkSubjectVisit = setSubjectItemList(blog, subjectUid);
        // 访问权限格式化
        if (StringUtils.isNotEmpty(blog.getVisitAuthExtra())) {
            VisitAuthExtra visitAuthExtra = JsonUtils.jsonToPojo(blog.getVisitAuthExtra(), VisitAuthExtra.class);
            if (visitAuthExtra != null) {
                // 移除敏感数据
                visitAuthExtra.setPassword("");
            }
            blog.setVisitAuthExtra("");
            blog.setVisitAuthExtraVo(visitAuthExtra);
        }

        // 设置点赞相关信息
        UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
        userPraiseRecordVO.setResourceUid(blog.getUid());
        userPraiseRecordVO.setResourceType(EResourceType.BLOG.getType());
        Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
        blog.setPraiseInfo(praiseMap);

        // 获取点赞的用户列表【影响性能】
        List<User> userList = userPraiseRecordManager.getPraiseList(userPraiseRecordVO);
        blog.setPraiseUserList(userList);

        // 设置收藏相关信息
        CollectVO collectVO = new CollectVO();
        collectVO.setBizUid(blog.getUid());
        collectVO.setCollectType(EResourceType.BLOG.getType());
        Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
        blog.setCollectInfo(collectInfo);

        // 判断是否开启图片懒加载
        if (Constants.STR_ONE.equals(isLazy)) {
            String blogContent = blog.getContent();
            if (StringUtils.isNotEmpty(blogContent)) {
                // 定义正则表达式，匹配img标签的src属性
                Pattern pattern = Pattern.compile("<img\\s+[^>]*?\\bsrc\\s*=\\s*[\'\"]?([^\'\"]+)[\'\"]?");
                // 将匹配到的img标签的src属性替换为data-src
                Matcher matcher = pattern.matcher(blogContent);
                String newString = matcher.replaceAll("<img data-src=\"$1\"");
                blog.setContent(newString);
            }
        }

        // 如果走了专栏的校验，那么就无需再走文章本身的校验逻辑了
        if (!checkSubjectVisit) {
            blogService.checkVisitAuth(blog);
        }

        return ResultUtil.result(SysConf.SUCCESS, blog);
    }

    @ApiOperation(value = "通过Uid获取博客点赞数", notes = "通过Uid获取博客点赞数")
    @GetMapping("/getBlogPraiseCountByUid")
    public String getBlogPraiseCountByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogPraiseCountByUid(uid));
    }

    @BussinessLog(value = "通过Uid给博客点赞", behavior = EBehavior.BLOG_PRAISE)
    @ApiOperation(value = "通过Uid给博客点赞", notes = "通过Uid给博客点赞")
    @GetMapping("/praiseBlogByUid")
    public String praiseBlogByUid(@ApiParam(name = "uid", value = "博客UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        return blogService.praiseBlogByUid(uid);
    }

    @ApiOperation(value = "根据标签Uid获取相关的博客", notes = "根据标签获取相关的博客")
    @GetMapping("/getSameBlogByTagUid")
    public String getSameBlogByTagUid(@ApiParam(name = "tagUid", value = "博客标签UID", required = true) @RequestParam(name = "tagUid", required = true) String tagUid,
                                      @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.getSameBlogByTagUid(tagUid));
    }

    @ApiOperation(value = "根据BlogUid获取相关的博客", notes = "根据BlogUid获取相关的博客")
    @GetMapping("/getSameBlogByBlogUid")
    public String getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<Blog> blogList = blogService.getSameBlogByBlogUid(blogUid);
        IPage<Blog> pageList = new Page<>();
        pageList.setRecords(blogList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @ApiOperation(value = "根据BlogUid下载博客", notes = "根据BlogUid下载博客")
    @GetMapping("/downloadBlog")
    public String downloadBlog(@ApiParam(name = "blogUid", value = "博客UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.successWithData(blogService.downloadBlog(blogUid));
    }


    /**
     * 设置博客标题图
     *
     * @param blog
     */
    private void setPhotoListByBlog(Blog blog) {
        //获取标题图片
        if (blog != null && !StringUtils.isEmpty(blog.getFileUid())) {
            String result = this.pictureFeignClient.getPicture(blog.getFileUid(), Constants.SYMBOL_COMMA);
            List<String> picList = webUtil.getPicture(result);
            if (picList != null && picList.size() > 0) {
                blog.setPhotoList(picList);
            }
        }
    }

    /**
     * 设置博客版权
     *
     * @param blog
     */
    private void setBlogCopyright(Blog blog) {
        //如果是原创的话
        if (Constants.STR_ONE.equals(blog.getIsOriginal())) {
            String jsonResult = sysParamsService.getSysParamsValueByKey(SysConf.SYS_ORIGINAL_TEMPLATE);
            blog.setCopyright(jsonResult);
        } else {
            String reprintedTemplate = sysParamsService.getSysParamsValueByKey(SysConf.SYS_REPRINTED_TEMPLATE);
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, variable);
            blog.setCopyright(str);
        }
    }

    /**
     * 设置专题列表
     *
     * @param blog
     */
    public boolean setSubjectItemList(Blog blog, String subjectUid) {
        boolean checkSubjectVisit = false;
        // 如果前端传递了SubjectUid，那么直接查询一下，是否合法
        Subject subject = null;
        if (StringUtils.isNotEmpty(subjectUid)) {
            subject = subjectService.getById(subjectUid);
        }

        // 如果前端没有传，就自己计算出一个专栏
        if (subject == null) {
            // 查询出该博客所属的专栏，如果没有归属直接跳出
            QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.BLOG_UID, blog.getUid());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            List<SubjectItem> tempSubjectItemList = subjectItemService.list(queryWrapper);
            if (tempSubjectItemList.size() == 0) {
                return checkSubjectVisit;
            }
            // 找个该博客所处的专栏
            Map<String, Integer> subjectCountMap = new HashMap<>();

            tempSubjectItemList.forEach(item -> {
                subjectCountMap.merge(item.getSubjectUid(), 1, Integer::sum);
            });
            Integer tempCount = 0;
            for (String key : subjectCountMap.keySet()) {
                if (subjectCountMap.get(key) > tempCount) {
                    tempCount = subjectCountMap.get(key);
                    subjectUid = key;
                }
            }
            subject = subjectService.getById(subjectUid);
        }
        // 在判断一下专栏是否为空
        if (subject == null || subject.getStatus() == EStatus.DISABLED) {
            return checkSubjectVisit;
        }

        // 查询出该专题下全部的专题
        QueryWrapper<SubjectItem> subjectItemQueryWrapper = new QueryWrapper<>();
        subjectItemQueryWrapper.eq(SQLConf.SUBJECT_UID, subject.getUid());
        subjectItemQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        subjectItemQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        subjectItemQueryWrapper.orderByDesc(SQLConf.SORT);
        List<SubjectItem> subjectItemList = subjectItemService.list(subjectItemQueryWrapper);
        List<String> blogUidList = new ArrayList<>();
        SubjectItem currentBlogSubjectItem = null;
        for (SubjectItem subjectItem : subjectItemList) {
            // 获取当前博客所归属的专题
            if (subjectItem.getBlogUid().equals(blog.getUid())) {
                currentBlogSubjectItem = subjectItem;
            }
            blogUidList.add(subjectItem.getBlogUid());
        }
        // 设置专题以及封面
        subject = subjectService.convertSubject(subject);
        blog.setSubject(subject);

        // 获取到专栏后，进行专栏可见性校验【如果该专题项是免费试读，会跳过该文章的访问权限校验】
        blogService.checkSubjectVisitAuth(subject, blog, currentBlogSubjectItem != null && Constants.STR_ONE.equals(currentBlogSubjectItem.getTryRead()));

        // 设置专题列表
        if (blogUidList.size() > 0) {
            Map<String, Blog> blogMap = new HashMap<>();
            List<Blog> blogList = (List<Blog>) blogService.listByIds(blogUidList);
            for (Blog item : blogList) {
                if (item.getStatus() == EStatus.ENABLE
                        && EAuditStatus.AGREE.equals(item.getAuditStatus())
                        && EPublish.PUBLISH.equals(item.getIsPublish())) {
                    item.setContent("");
                    blogMap.put(item.getUid(), item);
                }
            }
            subjectItemList.forEach(item -> {
                item.setBlog(blogMap.get(item.getBlogUid()));
            });
        }

        blog.setSubjectItemList(subjectItemList);
        return true;
    }
}