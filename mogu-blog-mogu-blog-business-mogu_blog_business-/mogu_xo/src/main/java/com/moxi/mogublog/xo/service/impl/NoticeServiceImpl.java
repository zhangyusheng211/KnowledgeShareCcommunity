package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.NoticeMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 通知表 服务实现类
 *
 * @author 陌溪
 * @date 2021年8月6日00:02:10
 */
@Service
public class NoticeServiceImpl extends SuperServiceImpl<NoticeMapper, Notice> implements NoticeService {
    @Resource
    private WebFeignClient webFeignClient;
    @Resource
    private NoticeService noticeService;

    @Resource
    private CommentService commentService;

    @Resource
    private UserWatchService userWatchService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private BlogService blogService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserMomentService userMomentService;

    @Resource
    private UserPraiseRecordService userPraiseRecordService;

    @Resource
    private UserService userService;

    @Resource
    private ProblemService problemService;

    @Resource
    private CollectService collectService;
    @Resource
    private DomainEventUtil domainEventUtil;
    @Value(value = "${data.website.url}")
    private String dataWebsiteUrl;
    @Resource
    private RabbitMqUtil rabbitMqUtil;
    @Resource
    private UserMomentService momentService;
    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public IPage<Notice> getPageList(NoticeVO noticeVO) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(noticeVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, noticeVO.getUserUid());
        }
        if (noticeVO.getIsBlack() != null) {
            queryWrapper.eq(SQLConf.IS_BLACK, noticeVO.getIsBlack());
        }
        if ( noticeVO.getNoticeType() != null && noticeVO.getNoticeType() > 0) {
            queryWrapper.eq(SQLConf.NOTICE_TYPE, noticeVO.getNoticeType());
        }
        Page<Notice> page = new Page<>();
        page.setSize(noticeVO.getPageSize());
        page.setCurrent(noticeVO.getCurrentPage());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Notice> noticeIPage = noticeService.page(page, queryWrapper);
        noticeIPage.setRecords(convertNoticeList(noticeIPage.getRecords()));
        return noticeIPage;
    }

    @Override
    public String addNotice(NoticeVO noticeVO) {
        Notice notice = new Notice();
        // 使用Spring工具类提供的深拷贝
        BeanUtils.copyProperties(noticeVO, notice, SysConf.STATUS);
        boolean isSave = notice.insert();
        //如果是后台站内信  不需要更新收件箱
        if (Objects.equals(notice.getIsBlack(), 0)) {
            // 同时更新他的收件箱
            this.sendNoticeByRedis(notice);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String batchAddNoticeByWatch(NoticeVO noticeVO) {
        // 判断是否有人关注过该作者, 需要想关注作者的人，推送系统通知，告知博客更新
        QueryWrapper<UserWatch> queryWrapper = new QueryWrapper();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(noticeVO.getAdminUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, noticeVO.getAdminUid());
        } else if (StringUtils.isNotEmpty(noticeVO.getUserUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, noticeVO.getUserUid());
        } else {
            throw new InsertException("插入失败，消息通知发送失败");
        }

        List<UserWatch> userWatchList = userWatchService.list(queryWrapper);
        List<Notice> noticeList = new ArrayList<>();
        userWatchList.forEach(item -> {
            String userUid = item.getUserUid();
            Notice notice = new Notice();
            notice.setUserUid(userUid);
            notice.setNoticeType(ENoticeType.SYSTEM.getCode());
            notice.setBusinessType(noticeVO.getBusinessType());
            notice.setBusinessUid(noticeVO.getBusinessUid());
            notice.setNoticeType(noticeVO.getNoticeType());
            notice.setStatus(EStatus.ENABLE);
            noticeList.add(notice);
            // 同时更新他的收件箱
            this.sendNoticeByRedis(notice);
        });

        if (noticeList.size() > 0) {
            noticeService.saveBatch(noticeList);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editNotice(NoticeVO noticeVO) {

        Notice notice = noticeService.getById(noticeVO.getUid());
        if (notice == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        BeanUtils.copyProperties(noticeVO, notice, SysConf.STATUS, SysConf.UID);
        notice.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteNotice(NoticeVO noticeVO) {
        List<NoticeVO> noticeVOList = new ArrayList<>();
        noticeVOList.add(noticeVO);
        return this.deleteBatchNotice(noticeVOList);
    }

    @Override
    public String deleteBatchNotice(List<NoticeVO> noticeVOList) {
        Set<String> noticeUidList = new HashSet<>();
        noticeVOList.forEach(item -> {
            noticeUidList.add(item.getUid());
        });

        if (noticeUidList.size() < 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }

        Collection<Notice> noticeList = noticeService.listByIds(noticeUidList);

        String userUid = RequestHolder.getUserUid();

        noticeList.forEach(item -> {
            if (userUid != null && !userUid.equals(item.getUserUid())) {
                throw new QueryException("无法删除别人的通知");
            }
            item.setStatus(EStatus.DISABLED);
            // 删除通知事件
            domainEventUtil.publishEvent(EventAction.NOTICE_DELETE, item);
        });
        noticeService.updateBatchById(noticeList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String getUserReceiveNoticeCount() {
        HttpServletRequest request = RequestHolder.getRequest();
        int receiveNoticeCommentCount = 0;
        int receiveNoticeWatchCount = 0;
        int receiveNoticePraiseCount = 0;
        int receiveNoticeSystemCount = 0;
        int receiveNoticeCollectCount = 0;
        int receiveNoticeMessageCount = 0;
        int receiveNoticeMessageGroupCount = 0;

        if (request.getAttribute(SysConf.USER_UID) != null) {
            String userUid = request.getAttribute(SysConf.USER_UID).toString();

            String commentCount = redisUtil.get(RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid);
            String watchCount = redisUtil.get(RedisConf.USER_RECEIVE_WATCH_COUNT + Constants.SYMBOL_COLON + userUid);
            String praiseCount = redisUtil.get(RedisConf.USER_RECEIVE_PRAISE_COUNT + Constants.SYMBOL_COLON + userUid);
            String systemCount = redisUtil.get(RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + userUid);
            String messageCount = redisUtil.get(RedisConf.USER_RECEIVE_MESSAGE_COUNT + Constants.SYMBOL_COLON + userUid);
            String collectCount = redisUtil.get(RedisConf.USER_RECEIVE_COLLECT_COUNT + Constants.SYMBOL_COLON + userUid);

            String userMessageGroupCount = redisUtil.get(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT + Constants.SYMBOL_COLON + userUid);
//            String messageGroupCount = redisUtil.get(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT);

            if (StringUtils.isNotEmpty(commentCount)) {
                receiveNoticeCommentCount = Integer.parseInt(commentCount);
            }
            if (StringUtils.isNotEmpty(watchCount)) {
                receiveNoticeWatchCount = Integer.parseInt(watchCount);
            }
            if (StringUtils.isNotEmpty(praiseCount)) {
                receiveNoticePraiseCount = Integer.parseInt(praiseCount);
            }
            if (StringUtils.isNotEmpty(systemCount)) {
                receiveNoticeSystemCount = Integer.parseInt(systemCount);
            }
            if (StringUtils.isNotEmpty(collectCount)) {
                receiveNoticeCollectCount = Integer.parseInt(collectCount);
            }
            if (StringUtils.isNotEmpty(messageCount)) {
                receiveNoticeMessageCount = Integer.parseInt(messageCount);
            }
            if (StringUtils.isNotEmpty(messageCount)) {
                receiveNoticeMessageCount = Integer.parseInt(messageCount);
            }
            // 判断群组有没有接收过通知
            if (StringUtils.isNotEmpty(userMessageGroupCount)) {
                receiveNoticeMessageGroupCount = Integer.parseInt(userMessageGroupCount);
            }
        }

        Map<String, Integer> map = new HashMap<>(Constants.NUM_FOUR);
        map.put(SysConf.USER_RECEIVE_COMMENT_COUNT, receiveNoticeCommentCount);
        map.put(SysConf.USER_RECEIVE_WATCH_COUNT, receiveNoticeWatchCount);
        map.put(SysConf.USER_RECEIVE_PRAISE_COUNT, receiveNoticePraiseCount);
        map.put(SysConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT, receiveNoticeSystemCount);
        map.put(SysConf.USER_RECEIVE_COLLECT_COUNT, receiveNoticeCollectCount);
        map.put(SysConf.USER_RECEIVE_MESSAGE_COUNT, receiveNoticeMessageCount + receiveNoticeMessageGroupCount);

        return ResultUtil.successWithData(map);
    }

    @Override
    public String readUserReceiveNoticeCount() {
        HttpServletRequest request = RequestHolder.getRequest();
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        redisUtil.delete(RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid);
        redisUtil.delete(RedisConf.USER_RECEIVE_WATCH_COUNT + Constants.SYMBOL_COLON + userUid);
        redisUtil.delete(RedisConf.USER_RECEIVE_PRAISE_COUNT + Constants.SYMBOL_COLON + userUid);
        redisUtil.delete(RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + userUid);
        redisUtil.delete(RedisConf.USER_RECEIVE_MESSAGE_COUNT + Constants.SYMBOL_COLON + userUid);
        redisUtil.delete(RedisConf.USER_RECEIVE_COLLECT_COUNT + Constants.SYMBOL_COLON + userUid);
        // 阅读过群组消息，往redis插入记录
        // redisUtil.setEx(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT + Constants.SYMBOL_COLON + userUid, Constants.STR_ONE, 7, TimeUnit.DAYS);
        redisUtil.delete(RedisConf.USER_RECEIVE_GROUP_MESSAGE_COUNT + Constants.SYMBOL_COLON + userUid);

        // 发送阅读通知事件
        domainEventUtil.publishEvent(EventAction.NOTICE_VIEW, new Notice());
        return ResultUtil.successWithMessage("阅读成功");
    }


    /**
     * 转换通知列表
     * TODO 这一堆代码属实有些难看，后面再重构把
     *
     * @param noticeList
     * @return
     */
    public List<Notice> convertNoticeList(List<Notice> noticeList) {
        if (noticeList.size() == 0) {
            return noticeList;
        }
        Set<String> commentUidSet = new HashSet<>();
        Set<String> watchUidSet = new HashSet<>();
        Set<String> blogUidSet = new HashSet<>();
        Set<String> questionUidSet = new HashSet<>();
        Set<String> momentUidSet = new HashSet<>();
        Set<String> praiseUidSet = new HashSet<>();
        Set<String> collectUidSet = new HashSet<>();
        Set<String> userUidSet = new HashSet<>();
        Set<String> problemUidSet = new HashSet<>();

        for (Notice item : noticeList) {
            if (EBusinessType.BLOG.getCode().equals(item.getBusinessType())) {
                blogUidSet.add(item.getBusinessUid());
            } else if (EBusinessType.QUESTION.getCode().equals(item.getBusinessType())) {
                questionUidSet.add(item.getBusinessUid());
            } else if (EBusinessType.PROBLEM.getCode().equals(item.getBusinessType())) {
                problemUidSet.add(item.getBusinessUid());
            } else if (EBusinessType.MOMENT.getCode().equals(item.getBusinessType())) {
                momentUidSet.add(item.getBusinessUid());
            } else if (ENoticeType.WATCH.getCode().equals(item.getNoticeType())) {
                watchUidSet.add(item.getBusinessUid());
            } else if (ENoticeType.COMMENT.getCode().equals(item.getNoticeType())) {
                commentUidSet.add(item.getBusinessUid());
            } else if (ENoticeType.PRAISE.getCode().equals(item.getNoticeType())) {
                praiseUidSet.add(item.getBusinessUid());
            } else if (ENoticeType.COLLECT.getCode().equals(item.getNoticeType())) {
                collectUidSet.add(item.getBusinessUid());
            }
        }

        // 从点赞表中，获取到点赞的资源
        Map<String, UserPraiseRecord> userPraiseRecordMap = new HashMap<>();
        if (praiseUidSet.size() > 0) {
            Collection<UserPraiseRecord> userPraiseRecordCollection = userPraiseRecordService.listByIds(praiseUidSet);
            userPraiseRecordCollection.forEach(item -> {
                // 设置点赞人的信息
                userUidSet.add(item.getUserUid());
                // 将点赞信息存储
                userPraiseRecordMap.put(item.getUid(), item);
                if (EResourceType.BLOG.getType().equals(item.getResourceType())) {
                    blogUidSet.add(item.getResourceUid());
                } else if (EResourceType.Question.getType().equals(item.getResourceType())) {
                    questionUidSet.add(item.getResourceUid());
                } else if (EResourceType.MOMENT.getType().equals(item.getResourceType())) {
                    momentUidSet.add(item.getResourceUid());
                } else if (EResourceType.COMMENT.getType().equals(item.getResourceType())) {
                    commentUidSet.add(item.getResourceUid());
                } else if (EResourceType.PROBLEM.getType().equals(item.getResourceType())) {
                    problemUidSet.add(item.getResourceUid());
                }
            });
        }

        // 从收藏表中，获取到收藏的资源
        Map<String, Collect> collectMap = new HashMap<>();
        if (collectUidSet.size() > 0) {
            Collection<Collect> collectCollection = collectService.listByIds(collectUidSet);
            for (Collect item : collectCollection) {
                // 设置点赞人的信息
                userUidSet.add(item.getUserUid());
                // 将点赞信息存储
                collectMap.put(item.getUid(), item);
                if (EResourceType.BLOG.getType().equals(item.getCollectType())) {
                    blogUidSet.add(item.getBizUid());
                } else if (EResourceType.Question.getType().equals(item.getCollectType())) {
                    questionUidSet.add(item.getBizUid());
                } else if (EResourceType.MOMENT.getType().equals(item.getCollectType())) {
                    momentUidSet.add(item.getBizUid());
                } else if (EResourceType.COMMENT.getType().equals(item.getCollectType())) {
                    commentUidSet.add(item.getBizUid());
                } else if (EResourceType.PROBLEM.getType().equals(item.getCollectType())) {
                    problemUidSet.add(item.getBizUid());
                }
            }
        }

        List<Comment> commentList = new ArrayList<>();
        Map<String, Comment> commentMap = new HashMap<>();
        if (commentUidSet.size() > 0) {
            Collection<Comment> commentCollection = commentService.listByIds(commentUidSet);
            commentList = commentService.convertCommentList(commentCollection);
            commentList.forEach(item -> {
                commentMap.put(item.getUid(), item);
            });
        }

        Map<String, Blog> blogMap = new HashMap<>();
        if (blogUidSet.size() > 0) {
            Collection<Blog> blogCollection = blogService.listByIds(blogUidSet);
            blogCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidSet.add(item.getUserUid());
                }
                blogMap.put(item.getUid(), item);
            });
        }

        Map<String, Question> questionMap = new HashMap<>();
        if (questionUidSet.size() > 0) {
            Collection<Question> questionCollection = questionService.listByIds(questionUidSet);
            questionCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidSet.add(item.getUserUid());
                }
                questionMap.put(item.getUid(), item);
            });
        }

        List<UserWatch> userWatchList = new ArrayList<>();
        Map<String, UserWatch> userWatchMap = new HashMap<>();

        if (watchUidSet.size() > 0) {
            Collection<UserWatch> userWatchCollection = userWatchService.listByIds(watchUidSet);
            userWatchList = userWatchService.convertUserWatchList(userWatchCollection);
            userWatchList.forEach(item -> {
                userWatchMap.put(item.getUid(), item);
            });
        }

        Map<String, UserMoment> momentMap = new HashMap<>();
        if (momentUidSet.size() > 0) {
            Collection<UserMoment> momentCollection = userMomentService.listByIds(momentUidSet);
            momentCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidSet.add(item.getUserUid());
                }
                momentMap.put(item.getUid(), item);
            });
        }

        Map<String, Problem> problemMap = new HashMap<>();
        if (problemUidSet.size() > 0) {
            Collection<Problem> problemCollection = problemService.listByIds(problemUidSet);
            problemCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidSet.add(item.getUserUid());
                }
                problemMap.put(item.getUid(), item);
            });
        }

        // 设置用户
        Map<String, User> userMap = new HashMap<>();
        if (userUidSet.size() > 0) {
            Collection<User> userCollection = userService.listByIds(userUidSet);
            List<User> userList = userService.convertUserList(userCollection);
            userService.setUserAvatar(userList);
            userList.forEach(item -> {
                userMap.put(item.getUid(), item);
            });
        }

        noticeList.forEach(item -> {
            // 评论动作
            if (ENoticeType.COMMENT.getCode().equals(item.getNoticeType())) {
                item.setComment(commentMap.get(item.getBusinessUid()));

                // 点赞动作
            } else if (ENoticeType.PRAISE.getCode().equals(item.getNoticeType())) {
                UserPraiseRecord userPraiseRecord = userPraiseRecordMap.get(item.getBusinessUid());
                if (userPraiseRecord != null) {
                    // 设置点赞人信息
                    if (StringUtils.isNotEmpty(userPraiseRecord.getUserUid())) {
                        item.setFromUser(userMap.get(userPraiseRecord.getUserUid()));
                    }
                    // 判断类型给设置对应的资源
                    if (EResourceType.BLOG.getType().equals(userPraiseRecord.getResourceType())) {
                        Blog blog = blogMap.get(userPraiseRecord.getResourceUid());
                        if (blog != null) {
                            if (blog.getUserUid() != null) {
                                User user = userMap.get(blog.getUserUid());
                                blog.setUser(user);
                            }
                            // 移除文章内容
                            blog.setContent("");
                            item.setBlog(blog);
                        }
                    } else if (EResourceType.Question.getType().equals(userPraiseRecord.getResourceType())) {
                        Question question = questionMap.get(userPraiseRecord.getResourceUid());
                        if (question != null) {
                            // 移除问答内容
                            question.setContent("");
                            item.setQuestion(question);
                        }
                    } else if (EResourceType.PROBLEM.getType().equals(userPraiseRecord.getResourceType())) {
                        Problem problem = problemMap.get(userPraiseRecord.getResourceUid());
                        if (problem != null) {
                            if (problem.getUserUid() != null) {
                                User user = userMap.get(problem.getUserUid());
                                problem.setUser(user);
                            }
                            problem.setAnswer("");
                            problem.setContent("");
                            item.setProblem(problem);
                        }
                    } else if (EResourceType.COMMENT.getType().equals(userPraiseRecord.getResourceType())) {
                        item.setComment(commentMap.get(userPraiseRecord.getResourceUid()));
                    } else if (EResourceType.MOMENT.getType().equals(userPraiseRecord.getResourceType())) {
                        item.setUserMoment(momentMap.get(userPraiseRecord.getResourceUid()));
                    }
                }
            } else if (ENoticeType.WATCH.getCode().equals(item.getNoticeType())) {
                item.setUserWatch(userWatchMap.get(item.getBusinessUid()));
            } else if (ENoticeType.SYSTEM.getCode().equals(item.getNoticeType())) {
                if (EBusinessType.BLOG.getCode().equals(item.getBusinessType())) {
                    Blog blog = blogMap.get(item.getBusinessUid());
                    if (blog != null) {
                        if (blog.getUserUid() != null) {
                            User user = userMap.get(blog.getUserUid());
                            blog.setUser(user);
                        }
                        // 移除文章内容
                        blog.setContent("");
                        item.setBlog(blog);
                    }
                } else if (EBusinessType.QUESTION.getCode().equals(item.getBusinessType())) {
                    Question question = questionMap.get(item.getBusinessUid());
                    if (question != null) {
                        if (question.getUserUid() != null) {
                            User user = userMap.get(question.getUserUid());
                            question.setUser(user);
                        }
                        // 移除问答内容
                        question.setContent("");
                        item.setQuestion(question);
                    }
                } else if (EBusinessType.MOMENT.getCode().equals(item.getBusinessType())) {
                    UserMoment userMoment = momentMap.get(item.getBusinessUid());
                    if (userMoment != null) {
                        if (userMoment.getUserUid() != null) {
                            User user = userMap.get(userMoment.getUserUid());
                            userMoment.setUser(user);
                        }
                        item.setUserMoment(userMoment);
                    }
                } else if (EBusinessType.PROBLEM.getCode().equals(item.getBusinessType())) {
                    Problem problem = problemMap.get(item.getBusinessUid());
                    if (problem != null) {
                        if (problem.getUserUid() != null) {
                            User user = userMap.get(problem.getUserUid());
                            problem.setUser(user);
                        }
                        problem.setAnswer("");
                        problem.setContent("");
                        item.setProblem(problem);
                    }
                }
            } else if (ENoticeType.COLLECT.getCode().equals(item.getNoticeType())) {
                Collect collect = collectMap.get(item.getBusinessUid());
                if (collect != null) {
                    // 设置点赞人信息
                    if (StringUtils.isNotEmpty(collect.getUserUid())) {
                        item.setFromUser(userMap.get(collect.getUserUid()));
                    }

                    // 判断类型给设置对应的资源
                    if (EResourceType.BLOG.getType().equals(collect.getCollectType())) {
                        Blog blog = blogMap.get(collect.getBizUid());
                        if (blog != null) {
                            if (blog.getUserUid() != null) {
                                User user = userMap.get(blog.getUserUid());
                                blog.setUser(user);
                            }
                            // 移除文章内容
                            blog.setContent("");
                            item.setBlog(blog);
                        }
                    } else if (EResourceType.Question.getType().equals(collect.getCollectType())) {
                        Question question = questionMap.get(collect.getBizUid());
                        if (question != null) {
                            // 移除问答内容
                            question.setContent("");
                            item.setQuestion(question);
                        }
                    } else if (EResourceType.PROBLEM.getType().equals(collect.getCollectType())) {
                        Problem problem = problemMap.get(collect.getBizUid());
                        if (problem != null) {
                            if (problem.getUserUid() != null) {
                                User user = userMap.get(problem.getUserUid());
                                problem.setUser(user);
                            }
                            problem.setAnswer("");
                            problem.setContent("");
                            item.setProblem(problem);
                        }
                    } else if (EResourceType.COMMENT.getType().equals(collect.getCollectType())) {
                        item.setComment(commentMap.get(collect.getBizUid()));
                    } else if (EResourceType.MOMENT.getType().equals(collect.getCollectType())) {
                        item.setUserMoment(momentMap.get(collect.getBizUid()));
                    }
                }
            }
        });
        return noticeList;
    }

    /**
     * 通过redis发送通知（小红点）
     *
     * @param notice
     */
    private void sendNoticeByRedis(Notice notice) {
        // 同时更新他的收件箱
        // 如果用户发送的内容，需要向该用户Redis收件箱中中写入一条记录
        String userUid = notice.getUserUid();
        String redisKey = "";
        switch (ENoticeType.getNoticeType(notice.getNoticeType())) {
            case COMMENT: {
                redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid;
            }
            break;
            case WATCH: {
                redisKey = RedisConf.USER_RECEIVE_WATCH_COUNT + Constants.SYMBOL_COLON + userUid;
            }
            break;
            case PRAISE: {
                redisKey = RedisConf.USER_RECEIVE_PRAISE_COUNT + Constants.SYMBOL_COLON + userUid;
            }
            break;
            case COLLECT: {
                redisKey = RedisConf.USER_RECEIVE_COLLECT_COUNT + Constants.SYMBOL_COLON + userUid;
            }
            break;
            case SYSTEM: {
                redisKey = RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + userUid;
            }
            break;
            default:
                throw new InsertException("通知类型转换异常");
        }
        // 消息红点保留7天
        String count = redisUtil.get(redisKey);
        if (StringUtils.isNotEmpty(count)) {
            redisUtil.incrBy(redisKey, Constants.NUM_ONE);
        } else {
            redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
        }
    }

    /**
     * 获取后台站内信列表
     *
     * @return
     */
    public IPage<Notice> getBlackNoticeList(NoticeVO noticeVO) {
        String adminUid = RequestHolder.getAdminUid();
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.eq(SQLConf.IS_BLACK, EStatus.ENABLE);
        wrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        wrapper.eq(SQLConf.ADMINUID, adminUid);
        wrapper.orderByDesc(SQLConf.CREATE_TIME);
        Page<Notice> page = new Page<>();
        page.setCurrent(noticeVO.getCurrentPage());
        page.setSize(noticeVO.getPageSize());
        IPage<Notice> pageList = noticeService.page(page, wrapper);
        List<String> userUidList = new ArrayList<>();
        pageList.getRecords().forEach((notice) -> {
            userUidList.add(notice.getUserUid());
        });
        // 获取带用户头像的
        Map<String, User> userMap = userService.getUserAvatarMapByIds(userUidList);

        //将 businessTypeName 设置进去
        pageList.getRecords().forEach((notice) -> {
            EBusinessType businessType = EBusinessType.getType(String.valueOf(notice.getBusinessType()));
            if (businessType != null) {
                notice.setBusinessTypeName(businessType.getName());
            }
            if (StringUtils.isNotEmpty(notice.getUserUid())) {
                notice.setUser(userMap.get(notice.getUserUid()));
            }

        });
        return pageList;
    }

    /**
     * 删除当前管理员小红点通知
     *
     * @return
     */
    public void deleteRedisBatchNotice(String adminUid) {
        String redisKey = RedisConf.ADMIN_NOTICE_POINT + Constants.SYMBOL_COLON + adminUid;
        //如果key存在， 则删除该key
        Boolean hasKey = redisUtil.hasKey(redisKey);
        if (hasKey) {
            redisUtil.delete(redisKey);
        }
    }

    /**
     * @param adminUid
     */
    public Integer searchAllNoticeCount(String adminUid) {
        String redisKey = RedisConf.ADMIN_NOTICE_POINT + Constants.SYMBOL_COLON + adminUid;
        //如果key存在， 则删除该key
        Boolean hasKey = redisUtil.hasKey(redisKey);
        if (hasKey) {
            String s = redisUtil.get(redisKey);
            return Integer.parseInt(s);
        } else {
            return 0;
        }
    }

    @Override
    public boolean sendNoticeByComment(Comment comment) {

        if (StringUtils.isEmpty(comment.getToUid())) {
            // 当该评论是一级评论的时候，说明是对 博客详情、留言板、关于我
            // 判断是否开启邮件通知【管理员邮箱通知】
            SystemConfig systemConfig = systemConfigService.getConfig();
            if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
                if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                    String sourceName = ECommentSource.valueOf(comment.getSource()).getName();
                    String linkText = "<a href=\" " + getUrlByCommentSource(comment) + "\">" + sourceName + "</a>\n";
                    String commentContent = linkText + "收到新的评论: " + comment.getContent();
                    rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), commentContent);
                } else {
                    log.error("网站没有配置通知接收的邮箱地址！");
                }
            }
        }

        // 判断被评论的用户，是否开启了评论邮件提醒
        if (StringUtils.isNotEmpty(comment.getToUserUid())) {
            User toUser = userService.getById(comment.getToUserUid());
            if (toUser.getStartEmailNotification() == SysConf.ONE) {
                Comment toComment = commentService.getById(comment.getToUid());
                sendEmailByComment(comment, toUser, toComment);
            }
            // 向该用户推送消息通知
            Notice notice = new Notice();
            notice.setUserUid(comment.getToUserUid());
            notice.setNoticeType(ENoticeType.COMMENT.getCode());
            // 设置通知类型
            if (ECommentSource.QUESTION_INFO.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.QUESTION_REPLY.getCode());
            } else if (ECommentSource.BLOG_INFO.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.BLOG_REPLY.getCode());
            } else if (ECommentSource.ABOUT.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.ABOUT_ME_REPLY.getCode());
            } else if (ECommentSource.MESSAGE_BOARD.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.MESSAGE_BOARD_REPLY.getCode());
            } else if (ECommentSource.USER_MOMENT.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.USER_MOMENT_REPLY.getCode());
            } else if (ECommentSource.MEDIA_INFO.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.MEDIA_REPLY.getCode());
            } else if (ECommentSource.PROBLEM_INFO.getCode().equals(comment.getSource())) {
                notice.setBusinessType(EBusinessType.PROBLEM_REPLY.getCode());
            } else {
                throw new InsertException("消息发送失败，输入错误的评论类型");
            }
            notice.setBusinessUid(comment.getUid());
            notice.insert();

            // 被评论的人，收到小红点通知
            String redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + comment.getToUserUid();
            String count = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(count)) {
                redisUtil.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }

        } else {
            String tempUserUid = "";
            // 如果是一级评论，需要判断该文章或者问答是否属于用户投稿的
            if (ECommentSource.QUESTION_INFO.getCode().equals(comment.getSource())) {
                Question question = questionService.getById(comment.getBlogUid());
                if (question != null && EContributeSource.USER_PUBLISH.equals(question.getQuestionSource())) {
                    tempUserUid = question.getUserUid();
                    // 当是门户投稿添加的，需要向号主发送评论通知
                    Notice notice = new Notice();
                    notice.setUserUid(tempUserUid);
                    notice.setNoticeType(ENoticeType.COMMENT.getCode());
                    notice.setBusinessType(EBusinessType.QUESTION_COMMENT.getCode());
                    notice.setBusinessUid(comment.getUid());
                    notice.insert();
                }

            } else if (ECommentSource.BLOG_INFO.getCode().equals(comment.getSource())) {
                Blog blog = blogService.getById(comment.getBlogUid());
                if (blog != null && EContributeSource.USER_PUBLISH.equals(blog.getArticleSource())) {
                    tempUserUid = blog.getUserUid();
                    // 当是门户投稿添加的，需要向号主发送评论通知
                    Notice notice = new Notice();
                    notice.setUserUid(tempUserUid);
                    notice.setNoticeType(ENoticeType.COMMENT.getCode());
                    notice.setBusinessType(EBusinessType.BLOG_COMMENT.getCode());
                    notice.setBusinessUid(comment.getUid());
                    notice.insert();
                }
            } else if (ECommentSource.USER_MOMENT.getCode().equals(comment.getSource())) {
                UserMoment userMoment = momentService.getById(comment.getBlogUid());
                if (userMoment != null) {
                    tempUserUid = userMoment.getUserUid();
                    // 当是门户投稿添加的，需要向号主发送评论通知
                    Notice notice = new Notice();
                    notice.setUserUid(tempUserUid);
                    notice.setNoticeType(ENoticeType.COMMENT.getCode());
                    notice.setBusinessType(EBusinessType.USER_MOMENT_COMMENT.getCode());
                    notice.setBusinessUid(comment.getUid());
                    notice.insert();
                }
            } else if (ECommentSource.PROBLEM_INFO.getCode().equals(comment.getSource())) {
                Problem problem = problemService.getById(comment.getBlogUid());
                if (problem != null) {
                    tempUserUid = problem.getUserUid();
                    // 当是门户投稿添加的，需要向号主发送评论通知
                    Notice notice = new Notice();
                    notice.setUserUid(tempUserUid);
                    notice.setNoticeType(ENoticeType.COMMENT.getCode());
                    notice.setBusinessType(EBusinessType.PROBLEM_COMMENT.getCode());
                    notice.setBusinessUid(comment.getUid());
                    notice.insert();
                }
            }

            // 如果用户投稿的文章收到评论，需要向该用户Redis收件箱中中写入一条记录
            if (StringUtils.isNotEmpty(tempUserUid)) {
                String redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + tempUserUid;
                String count = redisUtil.get(redisKey);
                if (StringUtils.isNotEmpty(count)) {
                    redisUtil.incrBy(redisKey, Constants.NUM_ONE);
                } else {
                    redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
                }
                // 刷新通知
                webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, tempUserUid);
            }


        }
        return true;
    }

    /**
     * 发送评论邮件
     *
     * @param comment
     * @param toUser
     * @param toComment
     */
    private void sendEmailByComment(Comment comment, User toUser, Comment toComment) {
        if (toComment != null && StringUtils.isNotEmpty(toComment.getContent())) {
            Map<String, String> map = new HashMap<>();
            map.put(SysConf.EMAIL, toUser.getEmail());
            map.put(SysConf.TEXT, comment.getContent());
            map.put(SysConf.TO_TEXT, toComment.getContent());

            if (comment.getUser() != null) {
                map.put(SysConf.NICKNAME, comment.getUser().getNickName());
            }
            map.put(SysConf.TO_NICKNAME, toUser.getNickName());
            map.put(SysConf.USER_UID, toUser.getUid());
            // 获取评论跳转的链接
            String commentSource = toComment.getSource();
            String url = new String();
            switch (commentSource) {
                case "ABOUT": {
                    url = dataWebsiteUrl + "about";
                }
                break;
                case "BLOG_INFO": {
                    url = dataWebsiteUrl + "info?blogUid=" + toComment.getBlogUid();
                }
                break;
                case "MESSAGE_BOARD": {
                    url = dataWebsiteUrl + "messageBoard";
                }
                break;
                default: {
                    log.error("跳转到其它链接");
                }
            }
            map.put(SysConf.URL, url);
            // 发送评论邮件
            rabbitMqUtil.sendCommentEmail(map);
        }
    }

    /**
     * 通过评论类型跳转到对应的页面
     *
     * @param comment
     * @return
     */
    private String getUrlByCommentSource(Comment comment) {
        String linkUrl = new String();
        String commentSource = comment.getSource();
        switch (commentSource) {
            case "ABOUT": {
                linkUrl = dataWebsiteUrl + "about";
            }
            break;
            case "BLOG_INFO": {
                linkUrl = dataWebsiteUrl + "info?blogUid=" + comment.getBlogUid();
            }
            break;
            case "MESSAGE_BOARD": {
                linkUrl = dataWebsiteUrl + "messageBoard";
            }
            break;
            default: {
                linkUrl = dataWebsiteUrl;
                log.error("跳转到其它链接");
            }
        }
        return linkUrl;
    }
}
