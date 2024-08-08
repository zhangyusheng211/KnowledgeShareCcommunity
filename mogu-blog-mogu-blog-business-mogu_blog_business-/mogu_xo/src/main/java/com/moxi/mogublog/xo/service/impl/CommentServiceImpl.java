package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.CommentVO;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.CommentMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.DeleteException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 评论表 服务实现类
 *
 * @author 陌溪
 * @date 2018-09-08
 */
@Service
public class CommentServiceImpl extends SuperServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;
    @Resource
    private QuestionService questionService;
    @Resource
    private FileFeignUtil fileFeignUtil;
    @Resource
    private ProblemService problemService;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectService collectService;
    @Autowired
    private AdminService adminService;
    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public Integer getCommentCount(CommentVO commentVO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, commentVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            queryWrapper.eq(SQLConf.BLOG_UID, commentVO.getBlogUid());
        }
        if (StringUtils.isNotEmpty(commentVO.getToUserUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, commentVO.getToUserUid());
        }
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<Comment> getPageList(CommentVO commentVO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getKeyword()) && !StringUtils.isEmpty(commentVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.CONTENT, commentVO.getKeyword().trim());
        }

        if (commentVO.getType() != null) {
            queryWrapper.eq(SQLConf.TYPE, commentVO.getType());
        }

        if (StringUtils.isNotEmpty(commentVO.getSource()) && !SysConf.ALL.equals(commentVO.getSource())) {
            queryWrapper.eq(SQLConf.SOURCE, commentVO.getSource());
        }
        if (StringUtils.isNotEmpty(commentVO.getAuditStatus())) {
            queryWrapper.eq(SQLConf.AUDIT_STATUS, commentVO.getAuditStatus());
        }

        if (StringUtils.isNotEmpty(commentVO.getUserName())) {
            String userName = commentVO.getUserName();
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.like(SQLConf.NICK_NAME, userName);
            userQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            List<User> list = userService.list(userQueryWrapper);
            if (list.size() > 0) {
                List<String> userUid = new ArrayList<>();
                list.forEach(item -> {
                    userUid.add(item.getUid());
                });
                queryWrapper.in(SQLConf.USER_UID, userUid);
            } else {
                // 当没有查询到用户时，默认UID
                queryWrapper.in(SQLConf.USER_UID, SysConf.DEFAULT_UID);
            }
        }

        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> commentList = pageList.getRecords();
        pageList.setRecords(this.convertCommentList(commentList));
        return pageList;
    }

    @Override
    public Map<String, List<Comment>> getCommentMapByBusinessId(CommentVO commentVO) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        Map<String, List<Comment>> businessCommentMap = new HashMap<>();
        List<String> businessUidList = commentVO.getBusinessUidList();
        if (businessUidList != null && businessUidList.size() > 0) {
            queryWrapper.in(SQLConf.BLOG_UID, businessUidList);
        } else {
            return businessCommentMap;
        }
        queryWrapper.eq(SQLConf.SOURCE, commentVO.getSource());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        // 查询出该文章下所有的评论
        List<Comment> list = commentService.list(queryWrapper);
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
        List<User> filterUserList = userService.convertUserList(userList);

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

            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.COMMENT.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.COMMENT.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);

            commentMap.put(item.getUid(), item);
        });

        // 给查询出来的评论添加基本信息
        list.forEach(item -> {

            String commentUid = item.getUid();
            String toCommentUid = item.getToUid();
            Comment comment = commentMap.get(commentUid);
            if (StringUtils.isNotEmpty(toCommentUid)) {
                comment.setToComment(commentMap.get(toCommentUid));
            }

            String blogUid = item.getBlogUid();
            List<Comment> commentList = businessCommentMap.get(blogUid);
            if (commentList != null) {
                commentList.add(comment);
                businessCommentMap.put(blogUid, commentList);
            } else {
                List<Comment> tempCommentList = new ArrayList<>();
                tempCommentList.add(comment);
                businessCommentMap.put(blogUid, tempCommentList);
            }

        });
        return businessCommentMap;
    }

    @Override
    public String addComment(CommentVO commentVO) {
        // 附身用户才能添加评论
        String adminUid = RequestHolder.getAdminUid();
        if (StringUtils.isEmpty(adminUid)) {
            throw new QueryException("只有管理员可以回复");
        }
        Admin admin = adminService.getById(adminUid);
        if (admin == null || StringUtils.isEmpty(admin.getUserUid())) {
            throw new QueryException("当前账号暂未附身门户用户，无法进行评论回复，请到 管理员管理-> 编辑, 选择要附身用户 再重新提交");
        }

        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        comment.setContent(commentVO.getContent());
        comment.setUserUid(admin.getUserUid());
        comment.setToUserUid(commentVO.getToUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setStatus(EStatus.ENABLE);
        comment.setUpdateTime(new Date());
        comment.setIsTop(comment.getIsTop());
        comment.setAuditStatus(EAuditStatus.AGREE);
        comment.setFirstCommentUid(commentVO.getFirstCommentUid());


        boolean isSave = comment.insert();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.COMMENT_ADD, comment);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editComment(CommentVO commentVO) {
        Comment comment = commentService.getById(commentVO.getUid());
        if (comment == null) {
            throw new QueryException("未找到评论信息");
        }
        // 进行对象拷贝
        BeanUtil.copyProperties(commentVO, comment, SysConf.STATUS);
        boolean isSave = comment.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.COMMENT_EDIT, comment);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteComment(CommentVO commentVO) {
        Comment comment = commentService.getById(commentVO.getUid());
        comment.setStatus(EStatus.DISABLED);
        comment.setUpdateTime(new Date());
        boolean isSave = comment.updateById();
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.COMMENT_DELETE, comment);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String deleteBatchComment(List<CommentVO> commentVOList) {
        if (commentVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        commentVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<Comment> commentList = commentService.listByIds(uids);

        commentList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            domainEventUtil.publishEvent(EventAction.COMMENT_DELETE, item);
        });
        commentService.updateBatchById(commentList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String batchDeleteCommentByBlogUid(List<String> blogUidList) {
        if (blogUidList.size() == 0) {
            throw new DeleteException();
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConf.BLOG_UID, blogUidList);
        List<Comment> commentList = commentService.list(queryWrapper);
        if (commentList.size() > 0) {
            commentList.forEach(item -> {
                item.setStatus(EStatus.DISABLED);
                domainEventUtil.publishEvent(EventAction.COMMENT_DELETE, item);
            });
            commentService.updateBatchById(commentList);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public List<Comment> convertCommentList(Collection<Comment> commentList) {
        List<Comment> resultList = new ArrayList<>();
        if (commentList.size() == 0) {
            return resultList;
        }
        Set<String> userUidSet = new HashSet<>();
        Set<String> blogUidSet = new HashSet<>();
        Set<String> questionUidSet = new HashSet<>();
        Set<String> problemUidSet = new HashSet<>();
        commentList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidSet.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                userUidSet.add(item.getToUserUid());
            }

            if (StringUtils.isNotEmpty(item.getBlogUid())) {
                if (ECommentSource.BLOG_INFO.getCode().equals(item.getSource())) {
                    blogUidSet.add(item.getBlogUid());
                } else if (ECommentSource.QUESTION_INFO.getCode().equals(item.getSource())) {
                    questionUidSet.add(item.getBlogUid());
                } else if (ECommentSource.PROBLEM_INFO.getCode().equals(item.getSource())) {
                    problemUidSet.add(item.getBlogUid());
                }
            }
        });

        // 获取博客
        Collection<Blog> blogList = new ArrayList<>();
        if (blogUidSet.size() > 0) {
            blogList = blogService.listByIds(blogUidSet);
        }
        Collection<Question> questionList = new ArrayList<>();
        if (questionUidSet.size() > 0) {
            questionList = questionService.listByIds(questionUidSet);
        }
        Collection<Problem> problemList = new ArrayList<>();
        if (problemUidSet.size() > 0) {
            problemList = problemService.listByIds(problemUidSet);
        }

        Map<String, Blog> blogMap = new HashMap<>();
        Map<String, Question> questionMap = new HashMap<>();
        Map<String, Problem> problemMap = new HashMap<>();

        blogList.forEach(item -> {
            // 评论管理并不需要查看博客内容，因此将其排除
            item.setContent("");
            blogMap.put(item.getUid(), item);
        });
        questionList.forEach(item -> {
            item.setContent("");
            questionMap.put(item.getUid(), item);
        });
        problemList.forEach(item -> {
            item.setContent("");
            item.setAnswer("");
            problemMap.put(item.getUid(), item);
        });

        // 获取头像
        Collection<User> userCollection = new ArrayList<>();
        if (userUidSet.size() > 0) {
            userCollection = userService.listByIds(userUidSet);
        }

        List<String> fileUidList = new ArrayList<>();
        userCollection.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUidList.add(item.getAvatar());
            }
        });

        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);

        // 用户信息脱敏
        userCollection = userService.convertUserList(userCollection);

        Map<String, User> userMap = new HashMap<>();
        userCollection.forEach(item -> {
            // 判断头像是否为空
            if (pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        for (Comment item : commentList) {
            try {
                ECommentSource commentSource = ECommentSource.valueOf(item.getSource());
                item.setSourceName(commentSource.getName());
            } catch (Exception e) {
                log.error("ECommentSource 转换异常");
            }

            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }

            if (StringUtils.isNotEmpty(item.getBlogUid())) {
                if (ECommentSource.BLOG_INFO.getCode().equals(item.getSource())) {
                    item.setBlog(blogMap.get(item.getBlogUid()));
                } else if (ECommentSource.QUESTION_INFO.getCode().equals(item.getSource())) {
                    item.setQuestion(questionMap.get(item.getBlogUid()));
                } else if (ECommentSource.PROBLEM_INFO.getCode().equals(item.getSource())) {
                    item.setProblem(problemMap.get(item.getBlogUid()));
                }
            }
            resultList.add(item);
        }
        return resultList;
    }


    /**
     * 审批评论
     *
     * @param commentVO
     * @return
     */
    @Override
    public String auditComment(CommentVO commentVO) {
        Comment comment = getById(commentVO.getUid());
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        comment.setAuditStatus(commentVO.getAuditStatus());
        comment.setRejectReason(commentVO.getRejectReason());
        comment.setAuditName(admin.getNickName());
        comment.setAuditTime(new Date());
        boolean isSave = commentService.updateById(comment);
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.COMMENT_AUDIT, comment);
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public IPage<Comment> getUserCommentList(CommentVO commentVO) {
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
        queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.isNull(SQLConf.TO_UID);
        // 按是否置顶进行倒排
        queryWrapper.orderByDesc(SQLConf.IS_TOP);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        // 查询出所有的一级评论，进行分页显示
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> firstUidList = new ArrayList<>();
        list.forEach(item -> {
            firstUidList.add(item.getUid());
        });
        if (firstUidList.size() > 0) {
            // 查询一级评论下的子评论
            QueryWrapper<Comment> notFirstQueryWrapper = new QueryWrapper<>();
            notFirstQueryWrapper.in(SQLConf.FIRST_COMMENT_UID, firstUidList);
            notFirstQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            notFirstQueryWrapper.orderByDesc(SQLConf.IS_TOP);

            notFirstQueryWrapper.orderByDesc(SQLConf.CREATE_TIME);
            List<Comment> notFirstList = commentService.list(notFirstQueryWrapper);
            // 将子评论加入总的评论中
            if (notFirstList.size() > 0) {
                list.addAll(notFirstList);
            }
        }
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
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
        List<User> filterUserList = userService.convertUserList(userList);
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
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }

            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.COMMENT.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.COMMENT.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);
        });
        return pageList;
    }
}
