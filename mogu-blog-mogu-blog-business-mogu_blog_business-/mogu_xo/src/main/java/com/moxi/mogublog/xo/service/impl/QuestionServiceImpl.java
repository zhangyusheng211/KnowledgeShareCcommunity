package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.commons.entity.QuestionTag;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mogublog.commons.vo.QuestionVO;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.CollectManager;
import com.moxi.mogublog.xo.mapper.QuestionMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.global.ErrorCode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 问答表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class QuestionServiceImpl extends SuperServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionTagService questionTagService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SysParamsService sysParamsService;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectManager collectManager;
    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    private CommonService commonService;

    @Override
    public IPage<Question> getPageList(QuestionVO questionVO) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        // 构建搜索条件
        if (StringUtils.isNotEmpty(questionVO.getKeyword()) && !StringUtils.isEmpty(questionVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, questionVO.getKeyword().trim());
        }
        if (!StringUtils.isEmpty(questionVO.getQuestionTagUid())) {
            queryWrapper.like(SQLConf.TAG_UID, questionVO.getQuestionTagUid());
        }
        if (!StringUtils.isEmpty(questionVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, questionVO.getUserUid());
        }
        if (!StringUtils.isEmpty(questionVO.getAdminUid())) {
            queryWrapper.eq(SQLConf.ADMINUID, questionVO.getAdminUid());
        }
        if (!StringUtils.isEmpty(questionVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, questionVO.getIsPublish());
        }
        if (questionVO.getIsBlack() != null && questionVO.getIsBlack() == 0) {
            queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        }

        //分页
        Page<Question> page = new Page<>();
        page.setCurrent(questionVO.getCurrentPage());
        page.setSize(questionVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);


        // 判断问答方法
        if (StringUtils.isNotEmpty(questionVO.getMethodType())) {
            String methodType = questionVO.getMethodType();
            switch (methodType) {
                case "newQuestion": {
                    queryWrapper.orderByDesc(SQLConf.IS_TOP);
                    queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
                }
                break;
                case "hotQuestion": {
                    queryWrapper.orderByDesc(SQLConf.REPLY_COUNT);
                }
                break;
                case "waitQuestion": {
                    queryWrapper.eq(SQLConf.REPLY_COUNT, 0);
                    queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
                }
                break;
                default: {
                    throw new QueryException("输入错误的参数");
                }
            }
        } else {
            if (StringUtils.isNotEmpty(questionVO.getOrderByAscColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByAscColumn())).toString();
                queryWrapper.orderByAsc(column);
            } else if (StringUtils.isNotEmpty(questionVO.getOrderByDescColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByDescColumn())).toString();
                queryWrapper.orderByDesc(column);
            } else {
                // 使用默认按sort值大小倒序
                queryWrapper.orderByDesc(SQLConf.SORT);
            }
        }

        IPage<Question> pageList = questionService.page(page, queryWrapper);
        List<Question> questionList = pageList.getRecords();

        for (Question item : questionList) {
            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.Question.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.Question.getType());
            Map<String, Object> collectInfo = collectManager.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);
        }

        this.setQuestionTagAndUser(questionList);
        pageList.setRecords(questionList);
        return pageList;
    }

    @Override
    public Integer getQuestionCount(QuestionVO questionVO) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(questionVO.getAdminUid())) {
            queryWrapper.eq(SQLConf.ADMINUID, questionVO.getAdminUid());
        }
        if (StringUtils.isNotEmpty(questionVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, questionVO.getUserUid());
        }
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer questionCount = questionService.count(queryWrapper);
        return questionCount;
    }

    @Override
    public Map<String, Object> getQuestionByKeyword(String keywords, Long currentPage, Long pageSize) {
        final String keyword = keywords.trim();
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.like(SQLConf.TITLE, keyword).or().like(SQLConf.SUMMARY, keyword));
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.select(Question.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        Page<Question> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        IPage<Question> iPage = questionService.page(page, queryWrapper);
        List<Question> questionList = iPage.getRecords();
        questionList.forEach(item -> {
            // 给标题和简介设置高亮
            item.setTitle(StringUtils.getHitCode(item.getTitle(), keyword));
            item.setSummary(StringUtils.getHitCode(item.getSummary(), keyword));

        });
        this.setQuestionTagAndUser(questionList);

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, iPage.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, iPage.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, pageSize);
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, iPage.getCurrent());
        // 返回数据
        map.put(SysConf.QUESTION_LIST, questionList);
        return map;
    }

    @Override
    public IPage<Question> getQuestionListByAuthor(String author, Long currentPage, Long pageSize) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(SQLConf.NICK_NAME, author);
        List<User> userList = userService.list(userQueryWrapper);
        List<String> userUidList = new ArrayList<>();
        userList.forEach(item -> {
            userUidList.add(item.getUid());
        });
        IPage<Question> pageList = new Page<>();
        if (userUidList.size() == 0) {
            return pageList;
        }

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        Page<Question> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.in(SQLConf.USER_UID, userUidList);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Question.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        pageList = questionService.page(page, queryWrapper);
        List<Question> list = pageList.getRecords();
        this.setQuestionTagAndUser(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String getQuestion(QuestionVO questionVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        if (questionVO.getOid() == 0) {
            throw new QueryException(ErrorCode.PARAM_INCORRECT, MessageConf.PARAM_INCORRECT);
        }
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.OID, questionVO.getOid());
        queryWrapper.last(SysConf.LIMIT_ONE);
        Question question = questionService.getOne(queryWrapper);
        if (question == null || question.getStatus() == EStatus.DISABLED || EPublish.NO_PUBLISH.equals(question.getIsPublish())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.BLOG_IS_DELETE);
        }
        List<Question> questionList = new ArrayList<>();
        questionList.add(question);
        this.setQuestionTagAndUser(questionList);
        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = redisUtil.get(RedisConf.QUESTION_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + question.getOid());
        if (StringUtils.isEmpty(jsonResult)) {
            //给问答点击数增加
            Integer clickCount = question.getClickCount() + 1;
            question.setClickCount(clickCount);
            question.updateById();
            //将该用户点击记录存储到redis中
            redisUtil.setEx(RedisConf.QUESTION_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + question.getOid(), question.getClickCount().toString(),
                    DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
        }

        // 判断是否开启图片懒加载
        if (Constants.STR_ONE.equals(questionVO.getIsLazy())) {
            String blogContent = question.getContent();
            if (StringUtils.isNotEmpty(blogContent)) {
                question.setContent(blogContent.replaceAll(" src=", " data-src="));
            }
        }

        return ResultUtil.successWithData(questionList.get(0));
    }

    @Override
    public String addQuestion(QuestionVO questionVO) {
        Question question = new Question();
        HttpServletRequest request = RequestHolder.getRequest();
        // 像关注的用户，推送系统通知
        NoticeVO noticeVO = new NoticeVO();
        // 判断是否是用户投稿
        Boolean isAdmin = false;
        if (EContributeSource.USER_PUBLISH.equals(questionVO.getQuestionSource())) {
            isAdmin = false;
            if (request.getAttribute(SysConf.USER_UID) == null) {
                throw new InsertException("用户未登录");
            }
            String userUid = request.getAttribute(SysConf.USER_UID).toString();

            // 判断该用户是否超过今日投稿次数
            Integer userMaxPublishCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.USER_PUBLISH_QUESTION_COUNT));
            String countJson = redisUtil.get(RedisConf.USER_PUBLISH_QUESTION_COUNT + Constants.SYMBOL_COLON + userUid);
            Integer userPublishCount = 0;
            if (StringUtils.isNotEmpty(countJson)) {
                userPublishCount = Integer.valueOf(countJson);
                if (userPublishCount >= userMaxPublishCount) {
                    return ResultUtil.errorWithMessage("您发布问答次数已达今日上限");
                }
            }
            userPublishCount++;
            // 设置一天过期
            redisUtil.setEx(RedisConf.USER_PUBLISH_QUESTION_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(userPublishCount), 1, TimeUnit.DAYS);
            questionVO.setUserUid(userUid);
            noticeVO.setUserUid(userUid);

            String content = questionVO.getContent();
            String auditType = commonService.checkIsAudit(request, content);
            if (auditType.equals(EAuditStatus.AGREE)) {
                questionVO.setAuditName("系统");
                questionVO.setAuditTime(new Date());
                questionVO.setAuditStatus(EAuditStatus.AGREE);
            } else {
                questionVO.setAuditStatus(EAuditStatus.WAIT);
            }
        } else {
            isAdmin = true;
            // 当为后台管理员添加的时候
            Admin admin = adminService.getById(RequestHolder.getAdminUid());
            // 判断该管理员是否进行了权限附身
            if (StringUtils.isNotEmpty(admin.getUserUid())) {
                User user = userService.getById(admin.getUserUid());
                if (user != null) {
                    noticeVO.setAdminUid(RequestHolder.getAdminUid());
                    questionVO.setUserUid(user.getUid());
                    // 被权限附身的管理员，改成以用户投稿的方式上传文章
                    questionVO.setQuestionSource(EContributeSource.USER_PUBLISH);
                } else {
                    noticeVO.setAdminUid(RequestHolder.getAdminUid());
                    questionVO.setQuestionSource(EContributeSource.ADMIN_PUBLISH);
                }
            } else {
                noticeVO.setAdminUid(RequestHolder.getAdminUid());
            }
            // 当后端添加时候，修改为管理员uid
            questionVO.setAdminUid(RequestHolder.getAdminUid());
            questionVO.setAuditName("管理员新增");
            questionVO.setAuditTime(new Date());
            questionVO.setAuditStatus(EAuditStatus.AGREE);
        }
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS);

        if (StringUtils.isEmpty(question.getSummary())) {
            String summary = StringUtils.dealContent(question.getContent());
            if (summary.length() < 190) {
                question.setSummary(summary);
            } else {
                question.setSummary(summary.substring(0, 190) + "...");
            }
        }
        boolean isSave = question.insert();
        if (isSave) {
            /**
             * 发布新增事件
             */
            domainEventUtil.publishEvent(EventAction.QUESTION_ADD, question);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editQuestion(QuestionVO questionVO) {
        Question question = questionService.getById(questionVO.getUid());
        if (question == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        // 判断用户是否有权操作
        this.checkAuth(questionVO, question);
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS, SysConf.UID, SysConf.QUESTION_SOURCE);
        Boolean isSave = question.updateById();
        if (isSave) {
            // 发送事件变更
            domainEventUtil.publishEvent(EventAction.QUESTION_EDIT, question);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteQuestion(QuestionVO questionVO) {
        Question question = questionService.getById(questionVO.getUid());
        if (question == null) {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
        // 判断用户是否有权操作
        this.checkAuth(questionVO, question);
        question.setStatus(EStatus.DISABLED);
        Boolean isSave = question.updateById();
        if (isSave) {
            // 发送领域事件，更新索引，扣除积分
            domainEventUtil.publishEvent(EventAction.QUESTION_DELETE, question);
        }

        return ResultUtil.successWithMessage("删除成功");
    }

    @Override
    public String deleteBatchQuestion(List<QuestionVO> questionVOList) {
        if (questionVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = new ArrayList<>();
        questionVOList.forEach(item -> {
            uidList.add(item.getUid());
        });
        Collection<Question> questionList = questionService.listByIds(uidList);
        questionList.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
        });
        boolean isSuccess = questionService.updateBatchById(questionList);
        if (isSuccess) {
            questionList.forEach(item -> {
                // 发送事件变更
                domainEventUtil.publishEvent(EventAction.QUESTION_DELETE, item);
            });
        }

        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    /**
     * 设置问答标签和用户
     *
     * @param questionList
     */
    private void setQuestionTagAndUser(List<Question> questionList) {
        Set<String> questionTagUidSet = new HashSet<>();
        Set<String> userUidList = new HashSet<>();
        Set<String> adminUidList = new HashSet<>();
        questionList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getQuestionTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getQuestionTagUid(), SysConf.FILE_SEGMENTATION);
                for (String itemTagUid : tagUidsTemp) {
                    questionTagUidSet.add(itemTagUid);
                }
            }
            // 判断用户投稿，还是后台管理员添加
            if (EContributeSource.USER_PUBLISH.equals(item.getQuestionSource())) {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
            } else {
                if (StringUtils.isNotEmpty(item.getAdminUid())) {
                    adminUidList.add(item.getAdminUid());
                }
            }
        });

        // 获取问答标签
        Collection<QuestionTag> questionTagList = new ArrayList<>();
        if (questionTagUidSet.size() > 0) {
            questionTagList = questionTagService.listByIds(questionTagUidSet);
        }
        Map<String, QuestionTag> tagMap = new HashMap<>();
        questionTagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        // 获取提问者的信息
        List<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.getUserListAndAvatarByIds(userUidList);
        }
        List<Admin> adminList = new ArrayList<>();
        if (adminUidList.size() > 0) {
            adminList = adminService.getAdminListByUid(adminUidList);
        }
        Map<String, User> userMap = new HashMap<>();
        Map<String, Admin> adminMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        adminList.forEach(item -> {
            adminMap.put(item.getUid(), item);
        });

        for (Question item : questionList) {
            //获取标签
            if (StringUtils.isNotEmpty(item.getQuestionTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getQuestionTagUid(), SysConf.FILE_SEGMENTATION);
                List<QuestionTag> tagListTemp = new ArrayList<>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setQuestionTagList(tagListTemp);
            }

            //获取用户【判断是用户投稿，还是后台添加】
            if (EContributeSource.USER_PUBLISH.equals(item.getQuestionSource())) {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    item.setUser(userMap.get(item.getUserUid()));
                }
            } else {
                if (StringUtils.isNotEmpty(item.getAdminUid())) {
                    User user = new User();
                    Admin admin = adminMap.get(item.getAdminUid());
                    if (admin != null) {
                        user.setAvatar(admin.getAvatar());
                        user.setUid(admin.getUid());
                        user.setOccupation(admin.getOccupation());
                        user.setGender(admin.getGender());
                        user.setSummary(admin.getSummary());
                        user.setNickName(admin.getNickName());
                        user.setPhotoUrl(admin.getPhotoUrl());
                        item.setUser(user);
                    }
                }
            }
        }
    }

    /**
     * 检查权限校验
     *
     * @param questionVO
     * @param question
     */
    private void checkAuth(QuestionVO questionVO, Question question) {
        // 判断是否是Web端的请求，还是后台的请求
        if (EContributeSource.USER_PUBLISH.equals(questionVO.getQuestionSource())) {
            // 判断用户是否能删除
            HttpServletRequest request = RequestHolder.getRequest();
            String userUid = request.getAttribute(SysConf.USER_UID).toString();
            if (!userUid.equals(question.getUserUid())) {
                throw new UpdateException("您无权操作其它用户的问答");
            }
        }
    }


    /**
     * 审批问答
     *
     * @param questionVO
     * @return
     */
    @Override
    public String auditQuestion(QuestionVO questionVO) {
        Question question = getById(questionVO.getUid());
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        question.setAuditStatus(questionVO.getAuditStatus());
        question.setRejectReason(questionVO.getRejectReason());
        question.setAuditName(admin.getNickName());
        question.setAuditTime(new Date());
        if (EAuditStatus.WAIT.equals(questionVO.getAuditStatus()) || EAuditStatus.REJECT.equals(questionVO.getAuditStatus())) {
            question.setIsPublish(EPublish.NO_PUBLISH);
        } else {
            question.setIsPublish(EPublish.PUBLISH);
        }
        boolean isSave = questionService.updateById(question);
        if (isSave) {
            // 发送事件变更
            domainEventUtil.publishEvent(EventAction.QUESTION_AUDIT, question);
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }
}