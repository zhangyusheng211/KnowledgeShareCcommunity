package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.ProblemTag;
import com.moxi.mogublog.commons.vo.ProblemTagVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.ProblemTagMapper;
import com.moxi.mogublog.xo.service.ProblemTagService;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 问答标签表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class ProblemTagServiceImpl extends SuperServiceImpl<ProblemTagMapper, ProblemTag> implements ProblemTagService {

    @Autowired
    ProblemTagService problemTagService;

    @Override
    public IPage<ProblemTag> getPageList(ProblemTagVO problemTagVO) {
        QueryWrapper<ProblemTag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(problemTagVO.getKeyword()) && !StringUtils.isEmpty(problemTagVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, problemTagVO.getKeyword().trim());
        }
        if (problemTagVO.getTagLevel() != null && problemTagVO.getTagLevel() > 0) {
            queryWrapper.eq(SQLConf.TAG_LEVEL, problemTagVO.getTagLevel());
        }
        if (problemTagVO.getTagType() != null && problemTagVO.getTagType() > 0) {
            queryWrapper.eq(SQLConf.TAG_TYPE, problemTagVO.getTagType());
        }
        if (StringUtils.isNotEmpty(problemTagVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, problemTagVO.getIsPublish());
        }
        if (StringUtils.isNotEmpty(problemTagVO.getIsSelection())) {
            queryWrapper.eq(SQLConf.IS_SELECTION, problemTagVO.getIsSelection());
        }
        if (problemTagVO.getTagUidList() != null && problemTagVO.getTagUidList().size() > 0) {
            queryWrapper.in(SQLConf.UID, problemTagVO.getTagUidList());
        }
        if (problemTagVO.getProblemCountGe() != null) {
            queryWrapper.ge(SQLConf.PROBLEM_COUNT, problemTagVO.getProblemCountGe());
        }

        Page<ProblemTag> page = new Page<>();
        page.setCurrent(problemTagVO.getCurrentPage());
        page.setSize(problemTagVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(problemTagVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(problemTagVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(problemTagVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(problemTagVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<ProblemTag> pageList = problemTagService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public List<ProblemTag> getAllList() {
        QueryWrapper<ProblemTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.SORT);
        List<ProblemTag> problemTagList = problemTagService.list(queryWrapper);
        List<ProblemTag> firstProblemTagList = new ArrayList<>();
        Map<String, List<ProblemTag>> problemTagMap = new HashMap<>();
        for (ProblemTag problemTag : problemTagList) {
            if (problemTag.getTagLevel() == Constants.NUM_ONE) {
                firstProblemTagList.add(problemTag);
            } else {
                if (problemTagMap.get(problemTag.getParentUid()) == null) {
                    List<ProblemTag> problemTagList2 = new ArrayList<>();
                    problemTagList2.add(problemTag);
                    problemTagMap.put(problemTag.getParentUid(), problemTagList2);
                } else {
                    List<ProblemTag> problemTagList2 = problemTagMap.get(problemTag.getParentUid());
                    problemTagList2.add(problemTag);
                    problemTagMap.put(problemTag.getParentUid(), problemTagList2);
                }
            }
        }
        for (ProblemTag problemTag : firstProblemTagList) {
            // 找出一级标签下所有的二级标签
            problemTag.setChildren(problemTagMap.get(problemTag.getUid()));
        }
        return firstProblemTagList;
    }

    @Override
    public String addProblemTag(ProblemTagVO problemTagVO) {
        QueryWrapper<ProblemTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.NAME, problemTagVO.getName());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        ProblemTag tempTag = problemTagService.getOne(queryWrapper);
        if (tempTag != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }

        ProblemTag problemTag = new ProblemTag();
        BeanUtil.copyProperties(problemTagVO, problemTag, SysConf.STATUS);
        problemTag.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editProblemTag(ProblemTagVO problemTagVO) {
        ProblemTag problemTag = problemTagService.getById(problemTagVO.getUid());
        if (problemTag != null && !problemTag.getName().equals(problemTagVO.getName())) {
            QueryWrapper<ProblemTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.NAME, problemTagVO.getName());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            ProblemTag tempTag = problemTagService.getOne(queryWrapper);
            if (tempTag != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(problemTagVO, problemTag, SysConf.STATUS, SysConf.UID);
        problemTag.setUpdateTime(new Date());
        problemTag.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchProblemTag(List<ProblemTagVO> problemTagVOList) {
        if (problemTagVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        problemTagVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<ProblemTag> tagList = problemTagService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = problemTagService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
