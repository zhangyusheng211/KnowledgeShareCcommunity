package com.moxi.mogublog.web.restapi;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.annotion.UserAuth.UserAuth;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.RedisConf;
import com.moxi.mogublog.web.global.SQLConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.*;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.CommentVO;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.GetOne;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 评论RestApi
 *
 * @author 陌溪
 * @date 2020年1月11日16:11:55
 */
@RestController
@RefreshScope
@RequestMapping("/web/comment")
@Api(value = "评论相关接口", tags = {"评论相关接口"})
@Slf4j
public class CommentRestApi {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private BlogService blogService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private CommentReportService commentReportService;
    @Autowired
    private SensitiveUtils sensitiveUtils;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;
    @Value(value = "${data.website.url}")
    private String dataWebsiteUrl;
    @Resource
    private FileFeignUtil fileFeignUtil;
    @Resource
    private UserMomentService momentService;
    @Resource
    private ProblemService problemService;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    private CommonService commonService;
    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @param result
     * @return
     */
    @ApiOperation(value = "获取评论列表", notes = "获取评论列表")
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);

        IPage<Comment> commentIPage = commentService.getUserCommentList(commentVO);
        List<Comment> commentList = commentIPage.getRecords();

        List<Comment> firstComment = new ArrayList<>();
        commentList.forEach(item -> {
            if (StringUtils.isEmpty(item.getToUid())) {
                firstComment.add(item);
            }
        });
        // 设置一级评论下的子评论
        if (commentVO.isOpenSecondLevel()) {
            // 获取所有二级
            Map<String, List<Comment>> secondCommentMap = new HashMap<>();
            for (Comment comment: commentList) {
                if (StringUtils.isEmpty(comment.getToUid())) {
                    continue;
                }
                List<Comment> comments = secondCommentMap.get(comment.getFirstCommentUid());
                if (comments == null) {
                    comments = new ArrayList<>();
                }
                comments.add(comment);
                secondCommentMap.put(comment.getFirstCommentUid(), comments);
            }
            for (Comment comment: firstComment) {
                comment.setReplyList(secondCommentMap.get(comment.getUid()));
            }
            commentIPage.setRecords(firstComment);
        } else {
            Map<String, List<Comment>> toCommentListMap = new HashMap<>();
            for (int a = 0; a < commentList.size(); a++) {
                List<Comment> tempList = new ArrayList<>();
                for (int b = 0; b < commentList.size(); b++) {
                    if (commentList.get(a).getUid().equals(commentList.get(b).getToUid())) {
                        tempList.add(commentList.get(b));
                    }
                }
                toCommentListMap.put(commentList.get(a).getUid(), tempList);
            }

            commentIPage.setRecords(getCommentReplys(firstComment, toCommentListMap));
        }
        return ResultUtil.result(SysConf.SUCCESS, commentIPage);
    }

    @ApiOperation(value = "App端获取评论列表", notes = "获取评论列表")
    @PostMapping("/getListByApp")
    public String getListByApp(@Validated({GetList.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            queryWrapper.like(SQLConf.BLOG_UID, commentVO.getBlogUid());
        }
        queryWrapper.eq(SQLConf.SOURCE, commentVO.getSource());
        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        // 查询出该文章下所有的评论
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> toCommentUidList = new ArrayList<>();
        // 判断回复评论的UID
        list.forEach(item -> {
            toCommentUidList.add(item.getToUid());
        });

        // 定义一个数组，用来存放全部的评论
        List<Comment> allCommentList = new ArrayList<>();
        allCommentList.addAll(list);

        // 查询出回复的评论
        Collection<Comment> toCommentList = null;
        if (toCommentUidList.size() > 0) {
            toCommentList = commentService.listByIds(toCommentUidList);
            allCommentList.addAll(toCommentList);
        }

        // 查询出评论用户的基本信息
        List<String> userUidList = new ArrayList<>();
        allCommentList.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            user.setUserTag(item.getUserTag());
            filterUserList.add(user);
        });

        // 获取用户头像
        List<String> fileUidList = new ArrayList<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUidList.add(item.getAvatar());
            }
        });
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);

        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        // 定义一个评论Map键值对
        Map<String, Comment> commentMap = new HashMap<>();
        allCommentList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            commentMap.put(item.getUid(), item);
        });

        // 给查询出来的评论添加基本信息
        List<Comment> returnCommentList = new ArrayList<>();
        list.forEach(item -> {
            String commentUid = item.getUid();
            String toCommentUid = item.getToUid();
            Comment comment = commentMap.get(commentUid);
            if (StringUtils.isNotEmpty(toCommentUid)) {
                comment.setToComment(commentMap.get(toCommentUid));
            }
            returnCommentList.add(comment);
        });
        pageList.setRecords(returnCommentList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @ApiOperation(value = "获取用户的评论列表", notes = "获取用户的评论列表")
    @PostMapping("/getCommentListByUser")
    public String getCommentListByUser(HttpServletRequest request, @Validated({GetList.class}) @RequestBody UserVO userVO) {

        String requestUserUid = request.getAttribute(SysConf.USER_UID).toString();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        // 查找出 我的评论
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_UID, requestUserUid));
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
        });

        // 获取用户列表
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }
        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            filterUserList.add(user);
        });
        // 获取用户头像
        StringBuffer fileUids = new StringBuffer();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        // 将评论列表划分为 我的评论 和 我的回复
        List<Comment> commentList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            // 设置sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                try {
                    item.setSourceName(ECommentSource.valueOf(item.getSource()).getName());
                } catch (Exception e) {
                    log.error("ECommentSource转换异常");
                }
            }
            if (requestUserUid.equals(item.getUserUid())) {
                commentList.add(item);
            }
        });

        pageList.setRecords(commentList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @ApiOperation(value = "获取用户的回复", notes = "获取用户的回复")
    @PostMapping("/getReplyListByUser")
    public String getReplyListByUser(HttpServletRequest request, @Validated({GetList.class}) @RequestBody UserVO userVO) {

        String requestUserUid = RequestHolder.getUserUid();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        // 查找出我的回复
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.TO_USER_UID, requestUserUid));
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(userUid);
            }
        });

        // 获取用户列表
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        //设置用户头像
        userService.setUserAvatar(userList);
        // 过滤用户敏感信息
        userService.convertUserList(userList);

        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });

        List<Comment> commentList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            // 设置sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                try {
                    item.setSourceName(ECommentSource.valueOf(item.getSource()).getName());
                } catch (Exception e) {
                    log.error("ECommentSource转换异常");
                }
            }
            if (requestUserUid.equals(item.getToUserUid())) {
                commentList.add(item);
            }
        });

        pageList.setRecords(commentList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    /**
     * 获取用户点赞信息
     *
     * @return
     */
    @ApiOperation(value = "获取用户点赞信息", notes = "增加评论")
    @PostMapping("/getPraiseListByUser")
    public String getPraiseListByUser(@ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        String userUid = RequestHolder.getUserUid();
        QueryWrapper<Comment> queryWrappe = new QueryWrapper<>();
        queryWrappe.eq(SQLConf.USER_UID, userUid);
        queryWrappe.eq(SQLConf.TYPE, ECommentType.PRAISE);
        queryWrappe.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrappe.orderByDesc(SQLConf.CREATE_TIME);
        Page<Comment> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        IPage<Comment> pageList = commentService.page(page, queryWrappe);
        List<Comment> praiseList = pageList.getRecords();
        List<String> blogUids = new ArrayList<>();
        praiseList.forEach(item -> {
            blogUids.add(item.getBlogUid());
        });
        Map<String, Blog> blogMap = new HashMap<>();
        if (blogUids.size() > 0) {
            Collection<Blog> blogList = blogService.listByIds(blogUids);
            blogList.forEach(blog -> {
                // 并不需要content内容
                blog.setContent("");
                blogMap.put(blog.getUid(), blog);
            });
        }

        praiseList.forEach(item -> {
            if (blogMap.get(item.getBlogUid()) != null) {
                item.setBlog(blogMap.get(item.getBlogUid()));
            }
        });
        pageList.setRecords(praiseList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @UserAuth(EAuthCode.ADD_COMMENT)
    @BussinessLog(value = "发表评论", behavior = EBehavior.PUBLISH_COMMENT)
    @ApiOperation(value = "增加评论", notes = "增加评论")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody CommentVO commentVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        WebConfig webConfig = webConfigService.getOne(
                new QueryWrapper<WebConfig>()
                        .eq(SysConf.STATUS, EStatus.ENABLE)
        );
        // 判断是否开启全局评论功能
        if (SysConf.CAN_NOT_COMMENT.equals(webConfig.getOpenComment())) {
            return ResultUtil.errorWithMessage(MessageConf.NO_COMMENTS_OPEN);
        }

        // 判断是否来自问答详情的评论
        if (ECommentSource.QUESTION_INFO.getCode().equals(commentVO.getSource())) {
            if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
                // 判断该问答是否开启评论功能
                Question question = questionService.getById(commentVO.getBlogUid());
                if (SysConf.CAN_NOT_COMMENT.equals(question.getOpenComment())) {
                    return ResultUtil.errorWithMessage(MessageConf.QUESTION_NO_OPEN_COMMENTS);
                }

                // 判断该评论是否是问答的一级回复
                if (StringUtils.isEmpty(commentVO.getToUid())) {
                    // 更新问答的回复数
                    Integer replyCount = question.getReplyCount() + 1;
                    question.setReplyCount(replyCount);
                    question.updateById();
                }
            }
        } else if (ECommentSource.USER_MOMENT.getCode().equals(commentVO.getSource())) {
            // TODO 判断该动态是否开启评论功能

        } else if (ECommentSource.MEDIA_INFO.getCode().equals(commentVO.getSource())) {
            // TODO 判断该课程是否开启评论功能

        } else if (ECommentSource.RESOURCE_INFO.getCode().equals(commentVO.getSource())) {
            // TODO 判断该资源是否开启评论功能

        } else if (ECommentSource.PROBLEM_INFO.getCode().equals(commentVO.getSource())) {
            // TODO 判断该问题是否开启评论功能
            if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
                // 判断该问答是否开启评论功能
                Problem problem = problemService.getById(commentVO.getBlogUid());
                if (SysConf.CAN_NOT_COMMENT.equals(problem.getOpenComment())) {
                    return ResultUtil.errorWithMessage(MessageConf.QUESTION_NO_OPEN_COMMENTS);
                }
                // 更新问题评论数
                Integer replyCount = problem.getCommentCount() + 1;
                problem.setCommentCount(replyCount);
                problem.updateById();
            }

        } else {
            // 判断该博客是否开启评论功能
            if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
                Blog blog = blogService.getById(commentVO.getBlogUid());
                if (SysConf.CAN_NOT_COMMENT.equals(blog.getOpenComment())) {
                    return ResultUtil.errorWithMessage(MessageConf.BLOG_NO_OPEN_COMMENTS);
                }
            }
//            // 判断字数是否超过限制
//            if (commentVO.getContent().length() > SysConf.ONE_ZERO_TWO_FOUR) {
//                return ResultUtil.errorWithMessage(MessageConf.COMMENT_CAN_NOT_MORE_THAN_1024);
//            }
        }

        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        commentVO.setUserUid(userUid);

        // 判断该用户是否被禁言
        if (user.getCommentStatus() == SysConf.ZERO) {
            return ResultUtil.errorWithMessage(MessageConf.YOU_DONT_HAVE_PERMISSION_TO_SPEAK);
        }
        // 判断是否发送过多无意义评论
        String jsonResult = redisUtil.get(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid);
        if (!StringUtils.isEmpty(jsonResult)) {
            Integer count = Integer.valueOf(jsonResult);
            if (count >= Constants.NUM_FIVE) {
                return ResultUtil.errorWithMessage(MessageConf.PLEASE_TRY_AGAIN_IN_AN_HOUR);
            }
        }
        // 判断是否垃圾评论
        String content = commentVO.getContent();
        Map<String, String> sensitiveMap = sensitiveUtils.filter(content,false, SysConf.SYS_SENSITIVE_WORD);
        if (Integer.parseInt(sensitiveMap.get(SysConf.COUNT)) > Constants.NUM_ZERO) {
            if (StringUtils.isEmpty(jsonResult)) {
                Integer count = 0;
                redisUtil.setEx(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, count.toString(), 1, TimeUnit.HOURS);
            } else {
                redisUtil.incrBy(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, 1);
            }
            commentVO.setContent(sensitiveMap.get(SysConf.CONTENT));
        }

        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        String blogContent = "";
        if (SensitiveUtils.REPLACEMENT.equals(commentVO.getContent())) {
            blogContent = commentVO.getContent();
        } else {
            // 将Markdown转换成html
            blogContent = FileUtils.markdownToHtml(commentVO.getContent());
        }
        comment.setContent(blogContent);


        // 当该评论不是一级评论时，需要设置一级评论UID字段
        if (StringUtils.isNotEmpty(commentVO.getToUid())) {
            Comment toComment = commentService.getById(commentVO.getToUid());
            assert toComment != null;
            // 表示 toComment 是非一级评论
            if (StringUtils.isNotEmpty(toComment.getFirstCommentUid())) {
                comment.setFirstCommentUid(toComment.getFirstCommentUid());
            } else {
                // 表示父评论是一级评论，直接获取UID
                comment.setFirstCommentUid(toComment.getUid());
            }

            comment.setToUserUid(commentVO.getToUserUid());
        }

        // 判断评论是否需要审核
        String auditType = commonService.checkIsAudit(RequestHolder.getRequest(), comment.getContent());
        if (auditType.equals(EAuditStatus.AGREE)){
            comment.setAuditName("系统");
            comment.setAuditTime(new Date());
            comment.setAuditStatus(EAuditStatus.AGREE);
        }else {
            comment.setAuditStatus(EAuditStatus.WAIT);
        }

        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setStatus(EStatus.ENABLE);
        comment.insert();

        //获取用户头像
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
            if (webUtil.getPicture(pictureList).size() > 0) {
                user.setPhotoUrl(webUtil.getPicture(pictureList).get(0));
            }
        }
        comment.setUser(user);

        // 发送评论新增事件
        domainEventUtil.publishEvent(EventAction.COMMENT_ADD, comment);
        return ResultUtil.result(SysConf.SUCCESS, comment);
    }

    @BussinessLog(value = "举报评论", behavior = EBehavior.REPORT_COMMENT)
    @ApiOperation(value = "举报评论", notes = "举报评论")
    @PostMapping("/report")
    public String reportComment(@Validated({GetOne.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        Comment comment = commentService.getById(commentVO.getUid());
        // 判断评论是否被删除
        if (comment == null || comment.getStatus() == EStatus.DISABLED) {
            return ResultUtil.errorWithMessage(MessageConf.COMMENT_IS_NOT_EXIST);
        }

        // 判断举报的评论是否是自己的
        if (comment.getUserUid().equals(commentVO.getUserUid())) {
            return ResultUtil.errorWithMessage(MessageConf.CAN_NOT_REPORT_YOURSELF_COMMENTS);
        }
        // 查看该用户是否重复举报该评论
        QueryWrapper<CommentReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, commentVO.getUserUid());
        queryWrapper.eq(SQLConf.REPORT_COMMENT_UID, comment.getUid());
        List<CommentReport> commentReportList = commentReportService.list(queryWrapper);
        if (commentReportList.size() > 0) {
            return ResultUtil.errorWithMessage(MessageConf.CAN_NOT_REPEAT_REPORT_COMMENT);
        }
        CommentReport commentReport = new CommentReport();
        commentReport.setContent(commentVO.getContent());
        commentReport.setProgress(0);
        // 从VO中获取举报的用户uid
        commentReport.setUserUid(commentVO.getUserUid());
        commentReport.setReportCommentUid(comment.getUid());
        // 从entity中获取被举报的用户uid
        commentReport.setReportUserUid(comment.getUserUid());
        commentReport.setStatus(EStatus.ENABLE);
        commentReport.insert();

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }

    /**
     * 通过UID删除评论
     *
     * @param commentVO
     * @param result
     * @return
     */
    @UserAuth(EAuthCode.ADD_COMMENT)
    @BussinessLog(value = "删除评论", behavior = EBehavior.DELETE_COMMENT)
    @ApiOperation(value = "删除评论", notes = "删除评论")
    @PostMapping("/delete")
    public String deleteBatch(@Validated({Delete.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        String userUid = RequestHolder.getUserUid();
        Comment comment = commentService.getById(commentVO.getUid());
        // 判断该评论是否能够删除
        if (!comment.getUserUid().equals(userUid)) {
            return ResultUtil.errorWithMessage(MessageConf.DATA_NO_PRIVILEGE);
        }
        comment.setStatus(EStatus.DISABLED);
        comment.updateById();

        // 获取该评论下的子评论进行删除
        // 传入需要被删除的评论 【因为这里是一条，我们需要用List包装一下，以后可以用于多评论的子评论删除】
        List<Comment> commentList = new ArrayList<>(Constants.NUM_ONE);
        commentList.add(comment);

        // 判断删除的是一级评论还是子评论
        String firstCommentUid = "";
        if (StringUtils.isNotEmpty(comment.getFirstCommentUid())) {
            // 删除的是子评论
            firstCommentUid = comment.getFirstCommentUid();
        } else {
            // 删除的是一级评论
            firstCommentUid = comment.getUid();

            // 判断要删除的一级评论是否是问答回复【需要将该问答下的回复-1】
            if (ECommentSource.QUESTION_INFO.getCode().equals(comment.getSource())) {
                // 通过ID获取对应的问答
                if (StringUtils.isNotEmpty(comment.getBlogUid())) {
                    Question question = questionService.getById(comment.getBlogUid());
                    Integer replyCount = question.getReplyCount() - 1;
                    if (replyCount < 0) {
                        question.setReplyCount(0);
                    } else {
                        question.setReplyCount(replyCount);
                    }
                    question.updateById();
                    log.info("问答回复数减1");
                }
            }
        }

        // 获取该评论一级评论下所有的子评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.FIRST_COMMENT_UID, firstCommentUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Comment> toCommentList = commentService.list(queryWrapper);
        List<Comment> resultList = new ArrayList<>();
        this.getToCommentList(comment, toCommentList, resultList);
        // 将所有的子评论也删除
        if (resultList.size() > 0) {
            resultList.forEach(item -> {
                item.setStatus(EStatus.DISABLED);
                item.setUpdateTime(new Date());
            });
            commentService.updateBatchById(resultList);
        }

        // 发送评论新增事件
        domainEventUtil.publishEvent(EventAction.COMMENT_DELETE, comment);

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @ApiOperation(value = "关闭评论邮件通知", notes = "关闭评论邮件通知")
    @GetMapping("/closeEmailNotification/{userUid}")
    public String bindUserEmail(@PathVariable("userUid") String userUid) {

        User user = userService.getById(userUid);
        if (user == null) {
            ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
        user.setStartEmailNotification(0);
        user.updateById();

        // 通过user中获取的token，去修改redis中的信息
        if (StringUtils.isNotEmpty(user.getValidCode())) {
            String accessToken = user.getValidCode();
            String userInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken);
            if (StringUtils.isNotEmpty(userInfo)) {
                Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
                // 关闭邮件通知
                map.put(SysConf.START_EMAIL_NOTIFICATION, 0);
                redisUtil.setEx(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + accessToken, JsonUtils.objectToJson(map), userTokenSurvivalTime, TimeUnit.HOURS);
            }
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }


    /**
     * 获取评论所有回复
     *
     * @param list
     * @param toCommentListMap
     * @return
     */
    private List<Comment> getCommentReplys(List<Comment> list, Map<String, List<Comment>> toCommentListMap) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        } else {
            list.forEach(item -> {
                String commentUid = item.getUid();
                List<Comment> replyCommentList = toCommentListMap.get(commentUid);
                List<Comment> replyComments = getCommentReplys(replyCommentList, toCommentListMap);
                item.setReplyList(replyComments);
            });
            return list;
        }
    }

    /**
     * 获取二级评论列表
     * @param list
     * @param toCommentListMap
     * @return
     */
    private List<Comment> getCommentReplysLv2(List<Comment> list, Map<String, List<Comment>> toCommentListMap) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }

        return null;
    }

    /**
     * 获取某条评论下的所有子评论
     *
     * @return
     */
    private void getToCommentList(Comment comment, List<Comment> commentList, List<Comment> resultList) {
        if (comment == null) {
            return;
        }
        String commentUid = comment.getUid();
        for (Comment item : commentList) {
            if (commentUid.equals(item.getToUid())) {
                resultList.add(item);
                // 寻找子评论的子评论
                getToCommentList(item, commentList, resultList);
            }
        }
    }
}

