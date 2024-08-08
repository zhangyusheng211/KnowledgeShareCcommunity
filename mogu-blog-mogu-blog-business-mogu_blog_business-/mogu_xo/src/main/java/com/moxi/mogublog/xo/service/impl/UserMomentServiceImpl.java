package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.CommentVO;
import com.moxi.mogublog.commons.vo.UserMomentVO;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.dto.UserMomentDto;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.UserPraiseRecordManager;
import com.moxi.mogublog.xo.mapper.UserMomentMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mogublog.xo.utils.SensitiveUtils;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.DeleteException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户动态日志 服务实现类
 *
 * @author 陌溪
 * @date 2021年12月25日23:57:11
 */
@Service
public class UserMomentServiceImpl extends SuperServiceImpl<UserMomentMapper, UserMoment> implements UserMomentService {

    @Resource
    UserMomentService userMomentService;
    @Resource
    UserMomentTopicService userMomentTopicService;
    @Resource
    FileFeignUtil fileFeignUtil;
    @Resource
    UserService userService;
    @Resource
    CommentService commentService;
    @Resource
    RedisUtil redisUtil;
    @Autowired
    private SensitiveUtils sensitiveUtils;
    @Resource
    private UserWatchService userWatchService;
    @Resource
    UserMomentMapper userMomentMapper;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectService collectService;

    @Resource
    AdminService adminService;
    @Resource
    ApplicationContext context;
    @Resource
    private CommonService commonService;
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    private UserPraiseRecordManager userPraiseRecordManager;

    @Override
    public Page<UserMomentDto> list(UserMomentVO userMomentVO) {
        List<String> toUserUidList = new ArrayList<>();
        // 判断是否只查看关注用户的文章
        if (StringUtils.isNotEmpty(userMomentVO.getOrderBy()) && SysConf.USER_WATCH.equals(userMomentVO.getOrderBy())) {
            // 获取用户是否登录
            String userUid = RequestHolder.getUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                // 获取用户关注的用户
                List<UserWatch> userWatchList = userWatchService.list(new LambdaQueryWrapper<UserWatch>()
                        .eq(UserWatch::getUserUid, userUid)
                        .eq(UserWatch::getStatus, EStatus.ENABLE));
                toUserUidList = userWatchList.stream().map(UserWatch::getToUserUid).collect(Collectors.toList());
            }
        }

        Page<UserMomentDto> page = new Page<>(userMomentVO.getCurrentPage(), userMomentVO.getPageSize());
        Page<UserMomentDto> momentDtoPage = userMomentMapper.list(page, userMomentVO, toUserUidList);
        List<UserMomentDto> list = momentDtoPage.getRecords();
        /**
         * 文件集合
         */
        List<String> fileUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        List<String> momentTopicUidList = new ArrayList<>();
        List<String> businessUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUids())) {
                String[] array = StringUtils.split(item.getFileUids(), Constants.SYMBOL_COMMA);
                for (int i = 0; i < array.length; i++) {
                    fileUidList.add(array[i]);
                }
            }
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(item.getTopicUids())) {
                List<String> topics = StringUtils.split(item.getTopicUids());
                momentTopicUidList.addAll(topics);
            }
            businessUidList.add(item.getUid());
        });

        // 获取图片
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);

        // 获取用户列表
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> collection = userService.listByIds(userUidList);
            // 设置头像
            userService.setUserAvatar(collection);
            // 过滤敏感信息
            List<User> userList = userService.convertUserList(collection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }
        // 获取topic
        Map<String, UserMomentTopic> topicMap = new HashMap<>();
        if (momentTopicUidList.size() > 0) {
            Collection<UserMomentTopic> collection = userMomentTopicService.listByIds(momentTopicUidList);
            for (UserMomentTopic userMomentTopic : collection) {
                topicMap.put(userMomentTopic.getUid(), userMomentTopic);
            }
        }
        for (UserMoment item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUids())) {
                String[] array = StringUtils.split(item.getFileUids(), Constants.SYMBOL_COMMA);
                List<String> photoList = new ArrayList<>();
                for (int i = 0; i < array.length; i++) {
                    photoList.add(pictureMap.get(array[i]));
                }
                item.setPhotoList(photoList);
            }
            // 设置用户
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            // 设置话题
            if (StringUtils.isNotEmpty(item.getTopicUids())) {
                List<String> topics = StringUtils.split(item.getTopicUids());
                List<UserMomentTopic> userMomentTopicList = new ArrayList<>();
                for (String topicUid : topics) {
                    if (topicMap.get(topicUid) != null) {
                        userMomentTopicList.add(topicMap.get(topicUid));
                    }
                }
                item.setUserMomentTopicList(userMomentTopicList);
            }

            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.MOMENT.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.MOMENT.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);
        }
        momentDtoPage.setRecords(list);
        return momentDtoPage;
    }

    @Override
    public IPage<UserMoment> getPageList(UserMomentVO userMomentVO) {
        QueryWrapper<UserMoment> queryWrapper = new QueryWrapper<>();
        /**
         * 关键词
         */
        if (StringUtils.isNotEmpty(userMomentVO.getKeyword()) && StringUtils.isNotEmpty(userMomentVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.CONTENT, userMomentVO.getKeyword().trim());
        }

        /**
         * 是否发布
         */
//        if (userMomentVO.getIsPublish() != null) {
//            queryWrapper.eq(SQLConf.IS_PUBLISH, userMomentVO.getIsPublish());
//        }
        /**
         * 是否后台传入
         */
        if (userMomentVO.getIsBlack() == null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
            queryWrapper.eq(SQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        }

        /**
         * 指定动态uid
         */
        if (StringUtils.isNotEmpty(userMomentVO.getUid())) {
            queryWrapper.eq(SQLConf.UID, userMomentVO.getUid());
        }

        /**
         * 指定用户id
         */
        if (StringUtils.isNotEmpty(userMomentVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, userMomentVO.getUserUid());
        }

        /**
         * 话题uid
         */
        if (StringUtils.isNotEmpty(userMomentVO.getTopicUids())) {
            queryWrapper.like(SQLConf.TOPIC_UIDS, userMomentVO.getTopicUids());
        }

        // 判断是否只查看关注用户的文章
        if (StringUtils.isNotEmpty(userMomentVO.getOrderBy()) && SysConf.USER_WATCH.equals(userMomentVO.getOrderBy())) {
            // 获取用户是否登录
            String userUid = RequestHolder.getUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                // 获取用户关注的用户
                List<UserWatch> userWatchList = userWatchService.list(new LambdaQueryWrapper<UserWatch>()
                        .eq(UserWatch::getUserUid, userUid)
                        .eq(UserWatch::getStatus, EStatus.ENABLE));
                List<String> toUserUidList = userWatchList.stream().map(UserWatch::getToUserUid).collect(Collectors.toList());
                if (toUserUidList.size() > 0) {
                    queryWrapper.in(SQLConf.USER_UID, toUserUidList);
                } else {
                    IPage<UserMoment> emptyPage = new Page<>();
                    return emptyPage;
                }
            }
        }

        // 判断是否需要排序
        if (StringUtils.isNotEmpty(userMomentVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userMomentVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(userMomentVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userMomentVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.IS_TOP);
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        Page<UserMoment> page = new Page<>();
        page.setCurrent(userMomentVO.getCurrentPage());
        page.setSize(userMomentVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);

        IPage<UserMoment> pageList = userMomentService.page(page, queryWrapper);
        List<UserMoment> list = pageList.getRecords();

        List<String> fileUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        List<String> momentTopicUidList = new ArrayList<>();
        List<String> businessUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUids())) {
                String[] array = StringUtils.split(item.getFileUids(), Constants.SYMBOL_COMMA);
                for (int i = 0; i < array.length; i++) {
                    fileUidList.add(array[i]);
                }
            }
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(item.getTopicUids())) {
                List<String> topics = StringUtils.split(item.getTopicUids());
                momentTopicUidList.addAll(topics);
            }
            businessUidList.add(item.getUid());
        });

        // 获取图片
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);

        // 获取用户列表
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> collection = userService.listByIds(userUidList);
            // 设置头像
            userService.setUserAvatar(collection);
            // 过滤敏感信息
            List<User> userList = userService.convertUserList(collection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        // 获取topic
        Map<String, UserMomentTopic> topicMap = new HashMap<>();
        if (momentTopicUidList.size() > 0) {
            Collection<UserMomentTopic> collection = userMomentTopicService.listByIds(momentTopicUidList);
            for (UserMomentTopic userMomentTopic : collection) {
                topicMap.put(userMomentTopic.getUid(), userMomentTopic);
            }
        }

        // 获取评论
        CommentVO commentVO = new CommentVO();
        commentVO.setSource(ECommentSource.USER_MOMENT.getCode());
        commentVO.setBusinessUidList(businessUidList);
        Map<String, List<Comment>> commentListMap = commentService.getCommentMapByBusinessId(commentVO);

        for (UserMoment item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUids())) {
                String[] array = StringUtils.split(item.getFileUids(), Constants.SYMBOL_COMMA);
                List<String> photoList = new ArrayList<>();
                for (int i = 0; i < array.length; i++) {
                    photoList.add(pictureMap.get(array[i]));
                }
                item.setPhotoList(photoList);
            }
            // 设置用户
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            // 设置话题
            if (StringUtils.isNotEmpty(item.getTopicUids())) {
                List<String> topics = StringUtils.split(item.getTopicUids());
                List<UserMomentTopic> userMomentTopicList = new ArrayList<>();
                for (String topicUid : topics) {
                    if (topicMap.get(topicUid) != null) {
                        userMomentTopicList.add(topicMap.get(topicUid));
                    }
                }
                item.setUserMomentTopicList(userMomentTopicList);
            }
            // 设置动态下的回复
            if (commentListMap.get(item.getUid()) != null) {
                item.setCommentList(commentListMap.get(item.getUid()));
            }

            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.MOMENT.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 获取点赞的用户列表
            List<User> userList = userPraiseRecordManager.getPraiseList(userPraiseRecordVO);
            item.setPraiseUserList(userList);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.MOMENT.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);

        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addUserMoment(UserMomentVO userMomentVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        // 判断账号是否被禁用
        String userUid = RequestHolder.getUserUid();
        User user = userService.getById(userUid);
        // 判断该用户是否被禁言
        if (user == null || user.getCommentStatus() == SysConf.ZERO) {
            return ResultUtil.errorWithMessage("暂时没有权限发表动态");
        }
        // 判断是否发送过多无意义内容
        String jsonResult = redisUtil.get(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid);
        if (!StringUtils.isEmpty(jsonResult)) {
            Integer count = Integer.valueOf(jsonResult);
            if (count >= Constants.NUM_FIVE) {
                return ResultUtil.errorWithMessage("由于发送过多无意义动态，您已被禁言一小时，请稍后在试~");
            }
        }
        // 判断当前发布内容是否需要审核
        String content = userMomentVO.getContent();
        String auditType = commonService.checkIsAudit(request, content);
        boolean flg = true;
        if (auditType.equals(EAuditStatus.AGREE)) {
            flg = false;
        }
        // 判断是否垃圾内容
        Map<String, String> sensitiveMap = sensitiveUtils.filter(content, false, SysConf.SYS_SENSITIVE_WORD);
        if (Integer.parseInt(sensitiveMap.get(SysConf.COUNT)) > Constants.NUM_ZERO) {
            if (StringUtils.isEmpty(jsonResult)) {
                Integer count = 0;
                redisUtil.setEx(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, count.toString(), 1, TimeUnit.HOURS);
            } else {
                redisUtil.incrBy(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, 1);
            }
            userMomentVO.setContent(sensitiveMap.get(SysConf.CONTENT));
        }

        String convertContent = "";
        if (SensitiveUtils.REPLACEMENT.equals(userMomentVO.getContent())) {
            convertContent = userMomentVO.getContent();
        } else {
            // 将Markdown转换成html
            convertContent = FileUtils.markdownToHtml(userMomentVO.getContent());
        }

        UserMoment userMoment = new UserMoment();
        BeanUtil.copyProperties(userMomentVO, userMoment, SysConf.STATUS);
        userMoment.setUserUid(userUid);
        userMoment.setContent(convertContent);
        userMoment.setIsPublish(EPublish.PUBLISH); // 设置上架
        //TODO 目前动态暂不开启审核要求，只要发表即可获取积分
        if (!flg) {
            userMoment.setAuditName("系统");
            userMoment.setAuditTime(new Date());
            userMoment.setAuditStatus(EAuditStatus.AGREE);
        } else {
            userMomentVO.setAuditStatus(EAuditStatus.WAIT);
        }
        boolean isSave = userMoment.insert();

        // 发送变更请求
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.MOMENT_ADD, userMoment);
        }

        if (!flg) {
            return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
        } else {
            return ResultUtil.successWithMessage(MessageConf.SUBMIT_SUCCESS_AUDIT);
        }

    }

    @Override
    public String editUserMoment(UserMomentVO userMomentVO) {
        UserMoment userMoment = userMomentService.getById(userMomentVO.getUid());
        String userUid = RequestHolder.getUserUid();
        if (userUid != null && !userUid.equals(userMoment.getUserUid())) {
            throw new DeleteException("仅能编辑自己发表的动态");
        }
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(userMomentVO, userMoment, SysConf.STATUS, SysConf.UID, SysConf.USER_UID);
        userMoment.setUpdateTime(new Date());
        boolean isSave = userMoment.updateById();
        // 发送变更请求
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.MOMENT_EDIT, userMoment);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchUserMoment(List<UserMomentVO> userMomentVOList) {
        if (userMomentVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        String userUid = RequestHolder.getUserUid();

        List<String> uids = new ArrayList<>();
        userMomentVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<UserMoment> userMoments = userMomentService.listByIds(uids);

        for (UserMoment userMoment : userMoments) {
            if (userUid != null && !userUid.equals(userMoment.getUserUid())) {
                throw new DeleteException("仅能删除自己发表的动态");
            }
            userMoment.setUpdateTime(new Date());
            userMoment.setStatus(EStatus.DISABLED);
        }

        userMoments.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean isSuccess = userMomentService.updateBatchById(userMoments);

        if (isSuccess) {
            userMoments.forEach(userMoment -> {
                // 发送变更请求
                domainEventUtil.publishEvent(EventAction.MOMENT_DELETE, userMoment);
            });
        }

        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public Integer getUserMomentCount(UserMomentVO userMomentVO) {
        QueryWrapper<UserMoment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userMomentVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, userMomentVO.getUserUid());
        }
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Integer momentCount = userMomentService.count(queryWrapper);
        return momentCount;
    }

    /**
     * 查询动态排行榜
     *
     * @return
     */
    @Override
    public List<UserMoment> getLeaderMoment(Boolean refresh) {
        String rankMomentListJson = redisUtil.get(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_MOMENT_LIST);
        List<UserMoment> leaders;
        if (StringUtils.isNotEmpty(rankMomentListJson) && refresh) {
            leaders = JsonUtils.jsonToList(rankMomentListJson, UserMoment.class);
        } else {
            QueryWrapper<UserMoment> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("user_uid, count(uid) as sumCredits ");
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.eq(SQLConf.IS_PUBLISH, EStatus.ENABLE);
            queryWrapper.isNotNull(SQLConf.USER_UID);
            queryWrapper.groupBy(SQLConf.USER_UID);
            queryWrapper.orderByDesc(SQLConf.SUM_CREDITS);
            queryWrapper.last("limit 10");
            leaders = userMomentService.list(queryWrapper);
            List<String> userIds = new ArrayList<>();
            leaders.forEach((item) -> {
                userIds.add(item.getUserUid());
            });

            // 获取用户列表
            Map<String, User> userMap = this.usersConvert(userIds);
            leaders.forEach((item) -> {
                item.setUser(userMap.get(item.getUserUid()));
            });
            redisUtil.setEx(RedisConf.RANK_LIST + Constants.SYMBOL_COLON + RedisConf.RANK_MOMENT_LIST, JsonUtils.objectToJson(leaders), 10, TimeUnit.MINUTES);
        }
        return leaders;
    }

    /**
     * List<userIds> 转换为 Map<userId , User>
     *
     * @param userIds
     * @return
     */
    private Map<String, User> usersConvert(List<String> userIds) {
        Map<String, User> userMap = new HashMap<>();
        if (userIds.size() > 0) {
            Collection<User> collection = userService.listByIds(userIds);
            // 设置头像
            userService.setUserAvatar(collection);
            // 过滤敏感信息
            List<User> userList = userService.convertUserList(collection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }
        return userMap;
    }

    /**
     * 审批动态
     *
     * @param userMomentVO
     * @return
     */
    @Override
    public String auditMoment(UserMomentVO userMomentVO) {
        UserMoment userMoment = getById(userMomentVO.getUid());
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        userMoment.setAuditStatus(userMomentVO.getAuditStatus());
        userMoment.setRejectReason(userMomentVO.getRejectReason());
        userMoment.setAuditName(admin.getNickName());
        userMoment.setAuditTime(new Date());
        if (EAuditStatus.WAIT.equals(userMomentVO.getAuditStatus()) || EAuditStatus.REJECT.equals(userMomentVO.getAuditStatus())) {
            userMoment.setIsPublish(EPublish.NO_PUBLISH);
        } else {
            userMoment.setIsPublish(EPublish.PUBLISH);
        }
        boolean isSave = userMomentService.updateById(userMoment);
        if (isSave) {
            // 发送变更请求
            domainEventUtil.publishEvent(EventAction.MOMENT_AUDIT, userMoment);
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

}
