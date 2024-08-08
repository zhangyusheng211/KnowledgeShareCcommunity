package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.ProblemMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
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
public class ProblemServiceImpl extends SuperServiceImpl<ProblemMapper, Problem> implements ProblemService {

    @Resource
    ProblemService problemService;
    @Resource
    ProblemMapper problemMapper;
    @Resource
    UserService userService;
    @Resource
    AdminService adminService;
    @Resource
    ProblemTagService problemTagService;
    @Resource
    ProblemTagRelationService problemTagRelationService;
    @Resource
    ProblemUserRecordService problemUserRecordService;
    @Resource
    private RedisUtil redisUtil;
    /**
     * 系统参数服务
     */
    @Resource
    private SysParamsService sysParamsService;
    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    UserPraiseRecordService userPraiseRecordService;
    @Resource
    CollectService collectService;
    /**
     * 异步执行服务
     */
    @Resource
    private AsyncService asyncService;
    @Resource
    private CommonService commonService;

    @Override
    public IPage<Problem> getPageList(ProblemVO problemVO) {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(problemVO.getKeyword()) && !StringUtils.isEmpty(problemVO.getKeyword())) {
            queryWrapper.like(SQLConf.TITLE, problemVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(problemVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, problemVO.getIsPublish());
        }
        if (StringUtils.isNotEmpty(problemVO.getIsSelection())) {
            queryWrapper.eq(SQLConf.IS_SELECTION, problemVO.getIsSelection());
        }
        if (problemVO.getProblemDifficulty() != null && problemVO.getProblemDifficulty() > 0) {
            queryWrapper.eq(SQLConf.PROBLEM_DIFFICULTY, problemVO.getProblemDifficulty());
        }
        if (problemVO.getProblemType() != null && problemVO.getProblemType() > 0) {
            queryWrapper.eq(SQLConf.PROBLEM_TYPE, problemVO.getProblemType());
        }
        if (StringUtils.isNotEmpty(problemVO.getProblemTagUid())) {
            queryWrapper.like(SQLConf.PROBLEM_TAG_UID, problemVO.getProblemTagUid());
        }
        if (StringUtils.isNotEmpty(problemVO.getHasAnswer())) {
            queryWrapper.like(SQLConf.HAS_ANSWER, problemVO.getHasAnswer());
        }

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        if (EOpenStatus.OPEN.equals(problemVO.getIsShowAnswer())) {
            queryWrapper.select(Problem.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
            queryWrapper.select(Problem.class, i -> !i.getProperty().equals(SQLConf.ANSWER));
        }

        Page<Problem> page = new Page<>();
        page.setCurrent(problemVO.getCurrentPage());
        page.setSize(problemVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(problemVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(problemVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(problemVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(problemVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<Problem> pageList = problemService.page(page, queryWrapper);
        List<String> problemTagUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        for (Problem problem : pageList.getRecords()) {
            if (StringUtils.isNotEmpty(problem.getUserUid())) {
                userUidList.add(problem.getUserUid());
            }

            List<String> tempTagUidList = StringUtils.changeStringToString(problem.getProblemTagUid(), Constants.SYMBOL_COMMA);
            for (String tagUid : tempTagUidList) {
                problemTagUidList.add(tagUid);
            }
        }
        Map<String, ProblemTag> problemTagMap = new HashMap<>();
        if (problemTagUidList.size() > 0) {
            Collection<ProblemTag> problemTags = problemTagService.listByIds(problemTagUidList);

            for (ProblemTag problemTag : problemTags) {
                problemTagMap.put(problemTag.getUid(), problemTag);
            }
        }
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> userCollection = userService.listByIds(userUidList);
            userService.setUserAvatar(userCollection);
            List<User> userList = userService.convertUserList(userCollection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        for (Problem problem : pageList.getRecords()) {
            if (StringUtils.isNotEmpty(problem.getUserUid())) {
                problem.setUser(userMap.get(problem.getUserUid()));
            }
            List<String> tempTagUidList = StringUtils.changeStringToString(problem.getProblemTagUid(), Constants.SYMBOL_COMMA);
            List<ProblemTag> problemTagList = new ArrayList<>();
            for (String tagUid : tempTagUidList) {
                ProblemTag problemTag = problemTagMap.get(tagUid);
                if (problemTag != null) {
                    problemTagList.add(problemTag);
                }
            }
            problem.setProblemTagList(problemTagList);
        }

        return pageList;
    }

    @Override
    public Page<Problem> queryPage(ProblemVO problemVO) {
        Page<ProblemVO> page = new Page<>();
        page.setCurrent(problemVO.getCurrentPage());
        page.setSize(problemVO.getPageSize());

        // 解析TagUid
        if (StringUtils.isNotEmpty(problemVO.getProblemTagUid())) {
            String[] tagUidList = StringUtils.split(problemVO.getProblemTagUid(), Constants.SYMBOL_COMMA);
            problemVO.setTagUidList(tagUidList);
        }


        problemVO.setStatus(EStatus.ENABLE);
        Page<Problem> pageList = problemMapper.queryPage(page, problemVO);
        List<Problem> problemList = this.setProblemTagAndUserList(pageList.getRecords());

        // 判断是否是翻页操作、用户未登录就不校验
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(problemVO.getDegreeStatus()) && Constants.STR_ONE.equals(problemVO.getDegreeStatus())) {
            if (problemList.size() > 0) {

                Problem problem = problemList.get(0);
                // 设置点击数
                //从Redis取出数据，判断该用户是否点击过
                String ip = IpUtils.getIpAddr(RequestHolder.getRequest());
                String jsonResult = redisUtil.get("PROBLEM_CLICK:" + ip + "#" + problem.getUid());
                if (StringUtils.isEmpty(jsonResult)) {
                    problem.setClickCount(problem.getClickCount() + 1);
                    problem.updateById();
                    // 设置1天过期
                    redisUtil.setEx("PROBLEM_CLICK:" + ip + "#" + problem.getUid(), "1", 24, TimeUnit.HOURS);
                }

                // 如果是登录的用户，需要修改状态为已出现
                if (StringUtils.isNotEmpty(userUid)) {
                    ProblemUserRecordVO problemUserRecord = new ProblemUserRecordVO();
                    problemUserRecord.setProblemUid(problem.getProblemTagUid());
                    problemUserRecord.setUserUid(userUid);
                    problemUserRecord.setDegree(EDegreeStatus.VISIT.getType());
                    this.problemUserRecordService.addProblemUserRecord(problemUserRecord);
                }
            }
        }

        for (Problem item : problemList) {
            if (EOpenStatus.CLOSE.equals(problemVO.getIsShowAnswer())) {
                item.setAnswer("");
            }
            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(item.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.PROBLEM.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            item.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(item.getUid());
            collectVO.setCollectType(EResourceType.PROBLEM.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            item.setCollectInfo(collectInfo);
        }

        pageList.setRecords(problemList);
        return pageList;
    }

    @Override
    public String getProblem(ProblemVO problemVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        int oid = problemVO.getOid();
        if (oid == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.OID, oid);
        queryWrapper.last(SysConf.LIMIT_ONE);
        Problem problem = problemService.getOne(queryWrapper);

        // 判断是否是登录的用户在预览自己的博客
        String userUid = RequestHolder.getUserUid();
        Boolean isMyProblem = false;
        if (StringUtils.isNotEmpty(userUid)) {
            if (problem != null && userUid.equals(problem.getUserUid())) {
                isMyProblem = true;
            }
        }
        // 自己的博客，可以预览进行未发布的预览，否则无法打开
        if (!isMyProblem && (problem == null ||
                problem.getStatus() == EStatus.DISABLED ||
                EPublish.NO_PUBLISH.equals(problem.getIsPublish()) ||
                !EAuditStatus.AGREE.equals(problem.getAuditStatus()))) {
            return ResultUtil.result(ECode.ERROR, MessageConf.PROBLEM_IS_DELETE);
        }

        // 获取标签相似的问题
        if (StringUtils.isNotEmpty(problem.getProblemTagUid())) {
            ProblemVO problemVO1 = new ProblemVO();
            problemVO1.setProblemTagUid(problem.getProblemTagUid());
            problemVO1.setPageSize(6L);
            problemVO1.setCurrentPage(1L);
            Page<Problem> problemPage = this.queryPage(problemVO1);
            // 不显示本篇文章
            List<Problem> sameProblemList = new ArrayList<>();
            for (Problem item : problemPage.getRecords()) {
                if (problem.getUid().equals(item.getUid())) {
                    continue;
                }
                sameProblemList.add(item);
                // 超过5个跳出
                if (sameProblemList.size() == 5) {
                    break;
                }
            }
            problem.setSameProblemList(sameProblemList);
        }
        // 设置用户和标签
        List<Problem> problemList = new ArrayList<>();
        problemList.add(problem);
        this.setProblemTagAndUserList(problemList);
        problem = problemList.get(0);

        // 设置用户发表的博客数以及访问数
        if (problem.getUser() != null) {
            User user = problem.getUser();
            Integer blogPublishCount = 0;
            Integer problemPublishCount = 0;
            blogPublishCount = userService.getBlogPublishCount(problem.getUserUid());
            problemPublishCount = userService.getProblemPublishCount(problem.getUserUid());
            user.setBlogPublishCount(blogPublishCount);
            user.setProblemPublishCount(problemPublishCount);
            problem.setUser(user);
        }

        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = redisUtil.get("PROBLEM_CLICK:" + ip + "#" + problem.getUid());
        if (StringUtils.isEmpty(jsonResult)) {
            problem.setClickCount(problem.getClickCount() + 1);
            problem.updateById();
            // 设置1天过期
            redisUtil.setEx("PROBLEM_CLICK:" + ip + "#" + problem.getUid(), "1", DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
            // 发送面经访问的领域事件
            domainEventUtil.publishEvent(EventAction.PROBLEM_VISIT, problem);
        }

        // 判断是否开启图片懒加载
        if (Constants.STR_ONE.equals(problemVO.getIsLazy())) {
            String content = problem.getContent();
            String answer = problem.getAnswer();
            if (StringUtils.isNotEmpty(content)) {
                problem.setContent(content.replaceAll(" src=", " data-src="));
            }
            if (StringUtils.isNotEmpty(answer)) {
                problem.setAnswer(answer.replaceAll(" src=", " data-src="));
            }
        }
        return ResultUtil.result(SysConf.SUCCESS, problem);
    }

    /**
     * 设置用户和标签
     *
     * @param problemList
     */
    public List<Problem> setProblemTagAndUserList(List<Problem> problemList) {
        List<String> problemTagUidList = new ArrayList<>();
        List<String> userUidList = new ArrayList<>();
        for (Problem problem : problemList) {
            if (StringUtils.isNotEmpty(problem.getUserUid())) {
                userUidList.add(problem.getUserUid());
            }

            List<String> tempTagUidList = StringUtils.changeStringToString(problem.getProblemTagUid(), Constants.SYMBOL_COMMA);
            for (String tagUid : tempTagUidList) {
                problemTagUidList.add(tagUid);
            }
        }
        Map<String, ProblemTag> problemTagMap = new HashMap<>();
        if (problemTagUidList.size() > 0) {
            Collection<ProblemTag> problemTags = problemTagService.listByIds(problemTagUidList);

            for (ProblemTag problemTag : problemTags) {
                problemTagMap.put(problemTag.getUid(), problemTag);
            }
        }

        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> userCollection = userService.listByIds(userUidList);
            userService.setUserAvatar(userCollection);
            List<User> userList = userService.convertUserList(userCollection);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        for (Problem problem : problemList) {
            if (StringUtils.isNotEmpty(problem.getUserUid())) {
                problem.setUser(userMap.get(problem.getUserUid()));
            }
            List<String> tempTagUidList = StringUtils.changeStringToString(problem.getProblemTagUid(), Constants.SYMBOL_COMMA);
            List<ProblemTag> problemTagList = new ArrayList<>();
            for (String tagUid : tempTagUidList) {
                ProblemTag problemTag = problemTagMap.get(tagUid);
                if (problemTag != null) {
                    problemTagList.add(problemTag);
                }
            }
            problem.setProblemTagList(problemTagList);
        }
        return problemList;
    }

    @Override
    public String addProblem(ProblemVO problemVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        // 如果不是管理员发布， 那么为普通用户发布
        if (RequestHolder.checkUser()) {
            String userUid = RequestHolder.getUserUid();

            if (StringUtils.isEmpty(userUid)) {
                throw new InsertException("无此用户，请重新登录");
            }
            User user = userService.getById(userUid);
            Problem problem = new Problem();
            BeanUtil.copyProperties(problemVO, problem, SysConf.STATUS);

            /**
             * 判断该用户是否超过今日发布面经次数
             */
            Integer userMaxPublishCount = Integer.valueOf(sysParamsService.getSysParamsValueByKey(SysConf.USER_PUBLISH_PROBLEM_COUNT));
            String countJson = redisUtil.get(RedisConf.USER_PUBLISH_PROBLEM_COUNT + Constants.SYMBOL_COLON + userUid);
            Integer userPublishCount = 0;
            if (StringUtils.isNotEmpty(countJson)) {
                userPublishCount = Integer.valueOf(countJson);
                if (userPublishCount >= userMaxPublishCount) {
                    return ResultUtil.errorWithMessage("您发布面经次数已达今日上限");
                }
            }

            // 判断是否有答案
            if (StringUtils.isNotEmpty(problemVO.getAnswer())) {
                problem.setHasAnswer(Constants.STR_ONE);
            } else {
                problem.setHasAnswer(Constants.STR_ZERO);
            }
            if (StringUtils.isEmpty(problemVO.getSummary())) {
                if (problemVO.getContent().length() > 180) {
                    problem.setSummary(problemVO.getContent().substring(0, 180));
                } else {
                    problem.setSummary(problemVO.getContent());
                }
            }
            problem.setUserUid(user.getUid());

            /**
             * 是否是需要审核
             */
            Boolean isNormalUser = false;

            // 判断是否需要审核
            String auditType = commonService.checkIsAudit(request, problem.getContent(), problem.getTitle());
            if (auditType.equals(EAuditStatus.AGREE)) {
                problem.setAuditName("系统");
                problem.setIsPublish(EPublish.PUBLISH);
                problem.setAuditTime(new Date());
                problem.setAuditStatus(EAuditStatus.AGREE);
                isNormalUser = true;
            } else {
                problem.setIsPublish(EPublish.NO_PUBLISH);
                problem.setAuditStatus(EAuditStatus.WAIT);
                isNormalUser = false;
            }

            boolean isSave = problem.insert();

            /**
             * 保存成功才 ++;
             */
            if (isSave) {
                userPublishCount++;
                /**
                 *   设置一天过期
                 */
                redisUtil.setEx(RedisConf.USER_PUBLISH_PROBLEM_COUNT + Constants.SYMBOL_COLON + userUid, JsonUtils.objectToJson(userPublishCount), 1, TimeUnit.DAYS);

                domainEventUtil.publishEvent(EventAction.PROBLEM_ADD, problem);
            }

            // 在问题-标签关系表中也创建记录【会冗余一份在问题表】
            List<String> problemTagUidList = StringUtils.changeStringToString(problemVO.getProblemTagUid(), Constants.SYMBOL_COMMA);
            List<ProblemTagRelation> problemTagRelations = new ArrayList<>();
            for (int i = 0; i < problemTagUidList.size(); i++) {
                ProblemTagRelation problemTagRelation = new ProblemTagRelation();
                problemTagRelation.setProblemUid(problem.getUid());
                problemTagRelation.setTagUid(problemTagUidList.get(i));
                problemTagRelations.add(problemTagRelation);
            }
            if (problemTagRelations.size() > 0) {
                problemTagRelationService.saveOrUpdateBatch(problemTagRelations);
            }

            if (isNormalUser) {
                return ResultUtil.successWithMessage("面经提交成功，请等待管理员审核后上架~");
            }
            return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
        }

        // 获取权限附身的用户uid
        String adminUid = RequestHolder.getAdminUid();
        Admin admin = adminService.getById(adminUid);
        if (StringUtils.isEmpty(admin.getUserUid())) {
            log.error("[addProblem] 不支持直接添加问题，请先到管理员管理进行附身操作");
            throw new InsertException("不支持直接添加问题，请先到管理员管理进行附身操作");
        }

        Problem problem = new Problem();
        BeanUtil.copyProperties(problemVO, problem, SysConf.STATUS);
        // 判断是否有答案
        if (StringUtils.isNotEmpty(problemVO.getAnswer())) {
            problem.setHasAnswer(Constants.STR_ONE);
        } else {
            problem.setHasAnswer(Constants.STR_ZERO);
        }
        if (StringUtils.isEmpty(problemVO.getSummary())) {
            if (problemVO.getContent().length() > 180) {
                problem.setSummary(problemVO.getContent().substring(0, 180));
            } else {
                problem.setSummary(problemVO.getContent());
            }
        }
        problem.setUserUid(admin.getUserUid());
        problem.setAdminUid(admin.getUid());
        problem.setAuditName(admin.getNickName());
        problem.setAuditStatus(EAuditStatus.AGREE);
        problem.setAuditTime(new Date());
        problem.setIsPublish(EPublish.PUBLISH);
        problem.setPublishTime(new Date());
        boolean isSave = problem.insert();

        /**
         * 发布新增事件
         */
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.PROBLEM_ADD, problem);
        }

        // 在问题-标签关系表中也创建记录【会冗余一份在问题表】
        List<String> problemTagUidList = StringUtils.changeStringToString(problemVO.getProblemTagUid(), Constants.SYMBOL_COMMA);
        List<ProblemTagRelation> problemTagRelations = new ArrayList<>();
        for (int i = 0; i < problemTagUidList.size(); i++) {
            ProblemTagRelation problemTagRelation = new ProblemTagRelation();
            problemTagRelation.setProblemUid(problem.getUid());
            problemTagRelation.setTagUid(problemTagUidList.get(i));
            problemTagRelations.add(problemTagRelation);
        }
        if (problemTagRelations.size() > 0) {
            problemTagRelationService.saveOrUpdateBatch(problemTagRelations);
        }

        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editProblem(ProblemVO problemVO) {
        Problem problem = problemService.getById(problemVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(problemVO, problem, SysConf.STATUS, SysConf.UID);
        problem.setUpdateTime(new Date());
        // 判断是否有答案
        if (StringUtils.isNotEmpty(problemVO.getAnswer())) {
            problem.setHasAnswer(Constants.STR_ONE);
        } else {
            problem.setHasAnswer(Constants.STR_ZERO);
        }
        Boolean isSave = problem.updateById();

        /**
         * 发布编辑事件
         */
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.PROBLEM_ADD, problem);
        }

        // 查询出编辑问题对应的全部标签【会冗余一份在问题表】
        List<String> problemTagUidList = StringUtils.changeStringToString(problemVO.getProblemTagUid(), Constants.SYMBOL_COMMA);
        QueryWrapper<ProblemTagRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.PROBLEM_UID, problem.getUid());
        List<ProblemTagRelation> problemTagRelationList = problemTagRelationService.list(queryWrapper);

        List<String> containTagUidList = new ArrayList<>();
        for (ProblemTagRelation item : problemTagRelationList) {
            containTagUidList.add(item.getTagUid());
        }

        List<ProblemTagRelation> updateProblemTagRelation = new ArrayList<>();

        Map<String, Integer> map = new HashMap<>();
        for (String newTagUid : problemTagUidList) {
            Boolean isContain = false;
            for (ProblemTagRelation item : problemTagRelationList) {
                if (newTagUid.equals(item.getTagUid())) {
                    map.put(item.getTagUid(), 1);
                    isContain = true;
                    break;
                }
            }

            if (!isContain) {
                ProblemTagRelation problemTagRelation = new ProblemTagRelation();
                problemTagRelation.setTagUid(newTagUid);
                problemTagRelation.setProblemUid(problem.getUid());
                updateProblemTagRelation.add(problemTagRelation);
            }
        }

        for (ProblemTagRelation item : problemTagRelationList) {
            if (map.get(item.getTagUid()) == null) {
                item.setStatus(EStatus.DISABLED);
                updateProblemTagRelation.add(item);
            }
        }

        if (updateProblemTagRelation.size() > 0) {
            problemTagRelationService.saveOrUpdateBatch(updateProblemTagRelation);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchProblem(List<ProblemVO> problemVOList) {
        if (problemVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        problemVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Problem> problemList = problemService.listByIds(uids);
        problemList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = problemService.updateBatchById(problemList);
        if (save) {
            problemList.forEach(item -> {
                // 发布删除领域事件
                domainEventUtil.publishEvent(EventAction.PROBLEM_DELETE, item);
            });
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public Map<String, Object> searchProblem(String keywords, Long currentPage, Long pageSize) {

        Page<ProblemVO> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        ProblemVO problemVO = new ProblemVO();
        problemVO.setKeyword(keywords);
        problemVO.setIsPublish(EPublish.PUBLISH);
        Page<Problem> pageList = problemMapper.queryPage(page, problemVO);
        List<Problem> problemList = this.setProblemTagAndUserList(pageList.getRecords());

        // 判断是否是翻页操作、用户未登录就不校验
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(problemVO.getDegreeStatus()) && Constants.STR_ONE.equals(problemVO.getDegreeStatus())) {
            if (problemList.size() > 0) {

                Problem problem = problemList.get(0);
                // 设置点击数
                //从Redis取出数据，判断该用户是否点击过
                String ip = IpUtils.getIpAddr(RequestHolder.getRequest());
                String jsonResult = redisUtil.get("PROBLEM_CLICK:" + ip + "#" + problem.getUid());
                if (StringUtils.isEmpty(jsonResult)) {
                    problem.setClickCount(problem.getClickCount() + 1);
                    problem.updateById();
                    // 设置1天过期
                    redisUtil.setEx("PROBLEM_CLICK:" + ip + "#" + problem.getUid(), "1", 24, TimeUnit.HOURS);
                }

                // 如果是登录的用户，需要修改状态为已出现
                if (StringUtils.isNotEmpty(userUid)) {
                    ProblemUserRecordVO problemUserRecordVO = new ProblemUserRecordVO();
                    problemUserRecordVO.setProblemUid(problem.getUid());
                    problemUserRecordVO.setUserUid(userUid);
                    problemUserRecordVO.setDegree(EDegreeStatus.VISIT.getType());
                    this.problemUserRecordService.addProblemUserRecord(problemUserRecordVO);
                }
            }
        }

        // 设置点赞和搜索相关信息
        for (Problem problem : problemList) {
            // 设置点赞相关信息
            UserPraiseRecordVO userPraiseRecordVO = new UserPraiseRecordVO();
            userPraiseRecordVO.setResourceUid(problem.getUid());
            userPraiseRecordVO.setResourceType(EResourceType.PROBLEM.getType());
            Map<String, Object> praiseMap = userPraiseRecordService.praisedCount(userPraiseRecordVO);
            problem.setPraiseInfo(praiseMap);

            // 设置收藏相关信息
            CollectVO collectVO = new CollectVO();
            collectVO.setBizUid(problem.getUid());
            collectVO.setCollectType(EResourceType.PROBLEM.getType());
            Map<String, Object> collectInfo = collectService.getCollectCount(collectVO);
            problem.setCollectInfo(collectInfo);
        }

        pageList.setRecords(problemList);

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, pageList.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, pageList.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, pageList.getSize());
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, pageList.getCurrent());
        // 返回数据
        map.put(SysConf.BLOG_LIST, pageList.getRecords());

        return map;
    }

    /**
     * 审批问题
     *
     * @param problemVO
     * @return
     */
    @Override
    public String auditProblem(ProblemVO problemVO) {
        Problem problem = getById(problemVO.getUid());
        Admin admin = adminService.getById(RequestHolder.getAdminUid());
        problem.setAuditStatus(problemVO.getAuditStatus());
        problem.setRejectReason(problemVO.getRejectReason());
        problem.setAuditName(admin.getNickName());
        problem.setAuditTime(new Date());
        if (EAuditStatus.WAIT.equals(problemVO.getAuditStatus()) || EAuditStatus.REJECT.equals(problemVO.getAuditStatus())) {
            problem.setIsPublish(EPublish.NO_PUBLISH);
        } else {
            problem.setIsPublish(EPublish.PUBLISH);
        }
        boolean isSave = problemService.updateById(problem);
        if (isSave) {
            // 发布审核领域事件
            domainEventUtil.publishEvent(EventAction.PROBLEM_AUDIT, problem);
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public Integer getProblemCount(ProblemVO problemVO) {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(problemVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, problemVO.getUserUid());
        }
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.AUDIT_STATUS, EAuditStatus.AGREE);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return problemService.count(queryWrapper);
    }

    @Override
    public Problem randomProblem(ProblemVO problemVO) {
        QueryWrapper<Problem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        int problemCount = problemService.count(queryWrapper);
        int currentPage = RandomUtil.randomInt(problemCount);
        Page<Problem> page = new Page<>();
        page.setSize(1);
        page.setCurrent(currentPage);
        IPage<Problem> problemIPage = problemService.page(page, queryWrapper);
        if (problemIPage.getRecords().size() > 0) {
            // 返回第一个元素
            return problemIPage.getRecords().get(0);
        }
        return null;
    }

    /**
     * 修改答案
     *
     * @param generalEditVO
     * @return
     */
    @Override
    public String editAnswer(GeneralEditVO generalEditVO) {
        String userUid = RequestHolder.getUserUid();
        if (userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录，无法提交修改，请先登录");
        }
        GeneralEdit generalEdit = new GeneralEdit();
        Problem problem = problemService.getById(generalEditVO.getUid());
        GeneralEditVO generalEditVO1 = new GeneralEditVO();
        generalEditVO1.setOldContent(problem.getAnswer());
        generalEditVO1.setReason(generalEditVO.getReason());
        generalEditVO1.setUserUid(userUid);
        generalEditVO1.setSummary(generalEditVO.getSummary());
        generalEditVO1.setBizUid(generalEditVO.getUid());
        generalEditVO1.setContent(generalEditVO.getContent());
        generalEditVO1.setAuditStatus(EAuditStatus.WAIT);
        generalEditVO1.setCreateTime(new Date());
        generalEditVO1.setUpdateTime(new Date());
        BeanUtil.copyProperties(generalEditVO1, generalEdit, SysConf.STATUS);
        Integer result = problemMapper.addEditAnswer(generalEdit);
        if (result == 1) {
            asyncService.executeAsyncBusinessBlackNotice(false, generalEdit.getUserUid(), generalEdit.getUid(), EBusinessType.GENERAL_EDIT.getCode(), generalEdit.getReason());
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
    }

    /**
     * 生成试卷（问题分类）
     *
     * @param examVO
     * @return
     */
    @Override
    public Map<String, Object> builderExam(ExamVO examVO) {
        String userUid = RequestHolder.getUserUid();
        List<String> problemUids = new ArrayList<>();
        problemUids = examVO.getProblemUid();
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Problem::getIsPublish, EPublish.PUBLISH);
        queryWrapper.eq(Problem::getStatus, EStatus.ENABLE);
        queryWrapper.in(Problem::getUid, problemUids);
        List<Problem> problemList = problemService.list(queryWrapper);
        // 创建三个集合 分别存储 简答题， 单选题， 多选题
        List<Problem> shortAnswerList = new ArrayList<>();
        List<Problem> singleChoiceList = new ArrayList<>();
        List<Problem> multipleChoiceList = new ArrayList<>();

        for (Problem problem : problemList) {
            if (EProblemType.SHORT_ANSWER.equals(problem.getProblemType())) {
                shortAnswerList.add(problem);
            }
            if (EProblemType.SINGLE_CHOICE.equals(problem.getProblemType())) {
                singleChoiceList.add(problem);
            }
            if (EProblemType.MULTIPLE_CHOICE.equals(problem.getProblemType())) {
                multipleChoiceList.add(problem);
            }
        }

        Map<String, Object> map = new HashMap<>();
        // 简答题
        map.put(EProblemType.SHORT_ANSWERS, shortAnswerList);
        // 单选题
        map.put(EProblemType.SINGLE_CHOICES, singleChoiceList);
        // 多选题
        map.put(EProblemType.MULTIPLE_CHOICES, multipleChoiceList);
        return map;
    }

    @Override
    public String flushTagProblemCount() {
        QueryWrapper<ProblemTag> problemTagQueryWrapper = new QueryWrapper<>();
        problemTagQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<ProblemTag> problemTagList = problemTagService.list(problemTagQueryWrapper);
        for (ProblemTag problemTag : problemTagList) {
            QueryWrapper<ProblemTagRelation> problemTagRelationQueryWrapper = new QueryWrapper<>();
            problemTagRelationQueryWrapper.eq(SQLConf.TAG_UID, problemTag.getUid());
            problemTagRelationQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            int count = problemTagRelationService.count(problemTagRelationQueryWrapper);
            problemTag.setProblemCount(count);
            problemTag.updateById();
        }
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }
}
