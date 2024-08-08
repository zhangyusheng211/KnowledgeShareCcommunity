package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.GeneralEditVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.GeneralEditMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class GeneralEditServiceImpl extends SuperServiceImpl<GeneralEditMapper, GeneralEdit> implements GeneralEditService {

    @Resource
    GeneralEditMapper generalEditMapper;
    @Resource
    GeneralEditService generalEditService;
    @Resource
    AdminService adminService;
    @Resource
    ProblemService problemService;
    @Resource
    BlogService blogService;
    @Resource
    QuestionService questionService;
    @Resource
    UserService userService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<GeneralEdit> getGeneralEditList(GeneralEditVO generalEditVO) {
        QueryWrapper<GeneralEdit> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(generalEditVO.getKeyword()) && !StringUtils.isEmpty(generalEditVO.getKeyword())) {
            queryWrapper.like(SQLConf.CONTENT, generalEditVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(generalEditVO.getAuditStatus())) {
            queryWrapper.eq(SQLConf.AUDIT_STATUS, generalEditVO.getAuditStatus());
        }

        Page<GeneralEdit> page = new Page<>();
        page.setCurrent(generalEditVO.getCurrentPage());
        page.setSize(generalEditVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(generalEditVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(generalEditVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(generalEditVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(generalEditVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<GeneralEdit> pageList = generalEditService.page(page, queryWrapper);

        // 设置用户信息
        List<String> userUidList = new ArrayList<>();
        List<GeneralEdit> generalEdits = pageList.getRecords();
        generalEdits.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
        });
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            Collection<User> userCollection = userService.listByIds(userUidList);
            List<User> userList = userService.convertUserList(userCollection);
            userService.setUserAvatar(userList);
            userList.forEach(item -> {
                userMap.put(item.getUid(), item);
            });
        }

        generalEdits.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
        });
        pageList.setRecords(generalEdits);
        return pageList;
    }

    @Override
    public String deleteBatchGeneralEdit(List<GeneralEditVO> generalEditVOList) {
        if (generalEditVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        generalEditVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<GeneralEdit> generalEditCollection = generalEditService.listByIds(uids);
        generalEditCollection.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            // 发送删除勘误事件
            domainEventUtil.publishEvent(EventAction.GENERAL_EDIT_DELETE, item);
        });
        Boolean save = generalEditService.updateBatchById(generalEditCollection);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }


    /**
     * 审批修改
     *
     * @param generalEditVO
     * @return
     */
    @Override
    public String auditGeneralEdit(GeneralEditVO generalEditVO) {
        boolean flg = false;
        GeneralEdit generalEdit = getById(generalEditVO.getUid());
        //判断审批的资源类型
        if (EResourceType.PROBLEM.getType().equals(generalEditVO.getBizType())) {
            LambdaQueryWrapper<Problem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Problem::getUid, generalEditVO.getBizUid());
            Problem problem = problemService.getOne(wrapper);
            if (problem == null) {
                return ResultUtil.errorWithMessage("该问答不存在");
            }
            problem.setAnswer(generalEditVO.getContent());
            flg = problemService.updateById(problem);
        } else if (EResourceType.BLOG.getType().equals(generalEditVO.getBizType())) {
            //判断是博客
            LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Blog::getUid, generalEditVO.getBizUid());
            Blog blog = blogService.getOne(wrapper);
            if (blog == null) {
                return ResultUtil.errorWithMessage("该博客不存在");
            }
            blog.setContent(generalEditVO.getContent());
            flg = blogService.updateById(blog);
        } else if (EResourceType.Question.getType().equals(generalEditVO.getBizType())) {
            //判断是博客
            LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Question::getUid, generalEditVO.getBizUid());
            Question question = questionService.getOne(wrapper);
            if (question == null) {
                return ResultUtil.errorWithMessage("该问题不存在");
            }
            question.setContent(generalEditVO.getContent());
            flg = questionService.updateById(question);
        } else {
            return ResultUtil.errorWithMessage("审核的资源不存在");
        }

        if (flg) {
            Admin admin = adminService.getById(RequestHolder.getAdminUid());
            generalEdit.setAuditStatus(generalEditVO.getAuditStatus());
            generalEdit.setRejectReason(generalEditVO.getRejectReason());
            generalEdit.setAuditName(admin.getNickName());
            generalEdit.setAuditTime(new Date());
            boolean isSave = generalEditService.updateById(generalEdit);
            if (isSave) {
                // 发送删除勘误事件
                domainEventUtil.publishEvent(EventAction.GENERAL_EDIT_AUDIT, generalEdit);
            }
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
    }

    /**
     * 通用修改
     *
     * @param generalEditVO
     * @return
     */
    @Override
    public String generalEdit(GeneralEditVO generalEditVO) {
        String userUid = RequestHolder.getUserUid();
        if (userUid == null) {
            return ResultUtil.errorWithMessage("用户未登录，无法提交修改，请先登录");
        }
        GeneralEdit generalEdit = new GeneralEdit();
        generalEditVO.setUserUid(userUid);
        generalEditVO.setAuditStatus(EAuditStatus.WAIT);
        generalEditVO.setCreateTime(new Date());
        generalEditVO.setUpdateTime(new Date());
        BeanUtil.copyProperties(generalEditVO, generalEdit, SysConf.STATUS);
        Integer result = generalEditMapper.generalEdit(generalEdit);
        if (result == 1) {
            // 发送新增勘误事件
            domainEventUtil.publishEvent(EventAction.GENERAL_EDIT_ADD, generalEdit);
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
    }
}
