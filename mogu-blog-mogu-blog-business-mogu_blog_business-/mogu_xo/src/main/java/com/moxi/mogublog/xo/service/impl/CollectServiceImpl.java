package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.mapper.*;
import com.moxi.mogublog.xo.service.CollectService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 收藏表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-08
 */
@Service
@Slf4j
public class CollectServiceImpl extends SuperServiceImpl<CollectMapper, Collect> implements CollectService {
    @Resource
    private CollectService collectService;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private UserService userService;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private UserMomentMapper userMomentMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private ResourceMapper resourceMapper;
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    private CommonManager commonManager;

    @Override
    public Map<String, Object> getCollectCount(CollectVO collectVo) {
        String userUid = RequestHolder.getUserUid();
        Map<String, Object> map = new HashMap<>(Constants.NUM_TWO);
        String collectCountJson = redisUtil.get(RedisConf.COLLECT_COUNT + RedisConf.SEGMENTATION + collectVo.getBizUid());
        String isCollectJson = "";

        if (StringUtils.isNotEmpty(userUid)) {
            isCollectJson = redisUtil.get(RedisConf.IS_USER_COLLECT + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + collectVo.getBizUid());
            if (StringUtils.isNotEmpty(isCollectJson)) {
                Boolean isCollect = Boolean.valueOf(isCollectJson);
                map.put("isCollect", isCollect);
            }
        } else {
            map.put("isCollect", false);
        }

        if (StringUtils.isNotEmpty(collectCountJson)) {
            Integer collectCount = Integer.valueOf(collectCountJson);
            map.put("collectCount", collectCount);
        } else {
            map.put("collectCount", 0);
        }

        // 未登录直接返回false
        if (StringUtils.isNotEmpty(collectCountJson) && (StringUtils.isNotEmpty(isCollectJson) || StringUtils.isEmpty(userUid))) {
            return map;
        }
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.BIZ_UID, collectVo.getBizUid());
        queryWrapper.eq(SQLConf.COLLECT_TYPE, collectVo.getCollectType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Collect> collectList = collectService.list(queryWrapper);
        boolean isCollect = false;
        if (StringUtils.isNotEmpty(userUid)) {
            for (Collect collect : collectList) {
                if (collect.getUserUid().equals(userUid)) {
                    isCollect = true;
                    break;
                }
            }
        }
        map.put("collectCount", collectList.size());
        map.put("isCollect", isCollect);

        // 缓存资源id的收藏数 和 是否收藏
        redisUtil.setEx(RedisConf.COLLECT_COUNT + RedisConf.SEGMENTATION + collectVo.getBizUid(), JsonUtils.objectToJson(collectList.size()), 10, TimeUnit.MINUTES);
        if (StringUtils.isNotEmpty(userUid)) {
            redisUtil.setEx(RedisConf.IS_USER_COLLECT + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + collectVo.getBizUid(), JsonUtils.objectToJson(isCollect), 10, TimeUnit.MINUTES);
        }
        return map;
    }


    @Override
    public String addCollect(CollectVO collectVo) {
        // 类型校验
        EResourceType resourceType = EResourceType.getType(collectVo.getCollectType());
        String userUid = RequestHolder.getUserUid();
        // 判断该资源是否被该用户收藏过
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.BIZ_UID, collectVo.getBizUid());
        queryWrapper.eq(SQLConf.COLLECT_TYPE, collectVo.getCollectType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int count = collectService.count(queryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage("您已收藏过该资源");
        }
        Collect collect = new Collect();
        collect.setUserUid(userUid);
        collect.setBizUid(collectVo.getBizUid());
        collect.setStatus(EStatus.ENABLE);
        collect.setCollectType(resourceType.getType());
        boolean isSave = collect.insert();

        // 发送领域事件，处理收藏资源的后续处理
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.COLLECT_ADD, collect);
        }
        return ResultUtil.successWithMessage("收藏成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String deleteCollect(CollectVO collectVo) {
        String userUid = RequestHolder.getUserUid();
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.BIZ_UID, collectVo.getBizUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        Collect collect = collectService.getOne(queryWrapper);
        if (collect == null) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        } else {
            collect.setStatus(EStatus.DISABLED);
            boolean isSave = collect.updateById();
            if (isSave) {
                domainEventUtil.publishEvent(EventAction.COLLECT_CANCEL, collect);
            }
        }

        return ResultUtil.successWithMessage("取消收藏成功");
    }

    @Override
    public IPage<Collect> getPageList(CollectVO collectVo) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(collectVo.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, collectVo.getUserUid());
        }
        //分页
        Page<Collect> page = new Page<>();
        page.setCurrent(collectVo.getCurrentPage());
        page.setSize(collectVo.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(collectVo.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(collectVo.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(collectVo.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(collectVo.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            // 使用默认按sort值大小倒序
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<Collect> collectPage = collectService.page(page, queryWrapper);
        List<Collect> collectList = collectPage.getRecords();
        List<String> userUidList = new ArrayList<>();
        List<String> blogUidList = new ArrayList<>();
        List<String> questionUidList = new ArrayList<>();
        List<String> momentUidList = new ArrayList<>();
        List<String> problemUidList = new ArrayList<>();
        List<String> resourceUidList = new ArrayList<>();
        collectList.forEach(item -> {
            if (EResourceType.BLOG.getType().equals(item.getCollectType())) {
                blogUidList.add(item.getBizUid());
            } else if (EResourceType.Question.getType().equals(item.getCollectType())) {
                questionUidList.add(item.getBizUid());
            } else if (EResourceType.MOMENT.getType().equals(item.getCollectType())) {
                momentUidList.add(item.getBizUid());
            } else if (EResourceType.PROBLEM.getType().equals(item.getCollectType())) {
                problemUidList.add(item.getBizUid());
            } else if (EResourceType.RESOURCE.getType().equals(item.getCollectType())) {
                resourceUidList.add(item.getBizUid());
            }

        });
        Collection<Blog> blogCollection = new ArrayList<>();
        Collection<Question> questionCollection = new ArrayList<>();
        Collection<UserMoment> momentCollection = new ArrayList<>();
        Collection<Problem> problemCollection = new ArrayList<>();
        Collection<com.moxi.mogublog.commons.entity.Resource> resourceCollection = new ArrayList<>();
        Map<String, Blog> blogMap = new HashMap<>();
        Map<String, Question> questionMap = new HashMap<>();
        Map<String, UserMoment> momentMap = new HashMap<>();
        Map<String, Problem> problemMap = new HashMap<>();
        Map<String, com.moxi.mogublog.commons.entity.Resource> resourceMap = new HashMap<>();
        if (blogUidList.size() > 0) {
            blogCollection = blogMapper.selectBatchIds(blogUidList);
            blogCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
                // 排除内容
                item.setContent("");
                blogMap.put(item.getUid(), item);
            });
        }
        if (questionUidList.size() > 0) {
            questionCollection = questionMapper.selectBatchIds(questionUidList);
            questionCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
                // 排除内容
                item.setContent("");
                questionMap.put(item.getUid(), item);
            });
        }
        if (momentUidList.size() > 0) {
            momentCollection = userMomentMapper.selectBatchIds(momentUidList);
            momentCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
                momentMap.put(item.getUid(), item);
            });
        }
        if (problemUidList.size() > 0) {
            problemCollection = problemMapper.selectBatchIds(problemUidList);
            problemCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
                problemMap.put(item.getUid(), item);
            });
        }
        if (resourceUidList.size() > 0) {
            resourceCollection = resourceMapper.selectBatchIds(resourceUidList);
            resourceCollection.forEach(item -> {
                if (StringUtils.isNotEmpty(item.getUserUid())) {
                    userUidList.add(item.getUserUid());
                }
                resourceMap.put(item.getUid(), item);
            });
        }
        List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });
        collectList.forEach(item -> {
            if (EResourceType.BLOG.getType().equals(item.getCollectType())) {
                Blog blog = blogMap.get(item.getBizUid());
                item.setBlog(blog);
                if (blog != null && StringUtils.isNotEmpty(blog.getUserUid())) {
                    item.setUser(userMap.get(blog.getUserUid()));
                }
            } else if (EResourceType.Question.getType().equals(item.getCollectType())) {
                Question question = questionMap.get(item.getBizUid());
                item.setQuestion(question);
                if (question != null && StringUtils.isNotEmpty(question.getUserUid())) {
                    item.setUser(userMap.get(question.getUserUid()));
                }
            } else if (EResourceType.MOMENT.getType().equals(item.getCollectType())) {
                UserMoment userMoment = momentMap.get(item.getBizUid());
                item.setUserMoment(userMoment);
                if (userMoment != null && StringUtils.isNotEmpty(userMoment.getUserUid())) {
                    item.setUser(userMap.get(userMoment.getUserUid()));
                }
            } else if (EResourceType.PROBLEM.getType().equals(item.getCollectType())) {
                Problem problem = problemMap.get(item.getBizUid());
                item.setProblem(problem);
                if (problem != null && StringUtils.isNotEmpty(problem.getUserUid())) {
                    item.setUser(userMap.get(problem.getUserUid()));
                }
            } else if (EResourceType.RESOURCE.getType().equals(item.getCollectType())) {
                com.moxi.mogublog.commons.entity.Resource resource = resourceMap.get(item.getBizUid());
                item.setResource(resource);
                if (resource != null && StringUtils.isNotEmpty(resource.getUserUid())) {
                    item.setUser(userMap.get(resource.getUserUid()));
                }
            }
        });

        collectPage.setRecords(collectList);
        return collectPage;
    }

    @Override
    public String refreshCollect() {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        Page<Collect> page = new Page<>();
        queryWrapper.isNull("to_user_uid");
        page.setCurrent(1);
        page.setPages(10);
        int count = 0;
        while (true) {
            IPage<Collect> collectPage = collectService.page(page, queryWrapper);
            if (collectPage.getRecords().size() == 0) {
                break;
            }
            for (Collect collect : collectPage.getRecords()) {
                EResourceType resourceType = EResourceType.getType(collect.getCollectType());
                String toUserUid = commonManager.getUserUidByResource(resourceType, collect.getBizUid());
                collect.setToUserUid(toUserUid);
                collect.updateById();
            }
            count += 1;
            page.setCurrent(count);
            log.info("[refreshCollect] refreshCollect count, count: {}", count);
        }

        return ResultUtil.successWithMessage("刷数成功");
    }

    @Override
    public Integer getUserCollectCount(CollectVO collectVO) {
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(collectVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, collectVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(collectVO.getBizUid())) {
            queryWrapper.eq(SQLConf.BIZ_UID, collectVO.getBizUid());
        }
        if (StringUtils.isNotEmpty(collectVO.getToUserUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, collectVO.getToUserUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return collectService.count(queryWrapper);
    }

}
