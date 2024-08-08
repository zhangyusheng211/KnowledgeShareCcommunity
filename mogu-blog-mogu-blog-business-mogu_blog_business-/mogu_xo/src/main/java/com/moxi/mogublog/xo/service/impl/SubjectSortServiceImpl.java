package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.entity.SubjectSort;
import com.moxi.mogublog.commons.vo.SubjectSortVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.SubjectSortMapper;
import com.moxi.mogublog.xo.service.SubjectService;
import com.moxi.mogublog.xo.service.SubjectSortService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 专题分类表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class SubjectSortServiceImpl extends SuperServiceImpl<SubjectSortMapper, SubjectSort> implements SubjectSortService {

    @Resource
    SubjectSortService subjectSortService;

    @Resource
    SubjectService subjectService;

    @Resource
    private FileFeignUtil FileFeignUtil;

    @Override
    public IPage<SubjectSort> getPageList(SubjectSortVO subjectSortVO) {
        QueryWrapper<SubjectSort> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(subjectSortVO.getKeyword()) && !StringUtils.isEmpty(subjectSortVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, subjectSortVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(subjectSortVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, subjectSortVO.getIsPublish());
        }
        if (StringUtils.isNotEmpty(subjectSortVO.getIsSelection())) {
            queryWrapper.eq(SQLConf.IS_SELECTION, subjectSortVO.getIsSelection());
        }
        if (subjectSortVO.getSystemPreinstall() != null) {
            queryWrapper.eq(SQLConf.SYSTEM_PREINSTALL, subjectSortVO.getSystemPreinstall());
        }
        Page<SubjectSort> page = new Page<>();
        page.setCurrent(subjectSortVO.getCurrentPage());
        page.setSize(subjectSortVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(subjectSortVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(subjectSortVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(subjectSortVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(subjectSortVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<SubjectSort> pageList = subjectSortService.page(page, queryWrapper);
        List<String> fileUidList = new ArrayList<>();
        List<SubjectSort> subjectSortList = pageList.getRecords();
        subjectSortList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
        });
        Map<String, String> pictureMap = FileFeignUtil.fileUidToFileUrlMap(fileUidList);

        subjectSortList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                item.setPhotoUrl(pictureMap.get(item.getFileUid()));
            }
        });
        return pageList;
    }


    @Override
    public String addSubjectSort(SubjectSortVO subjectSortVO) {
        QueryWrapper<SubjectSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.NAME, subjectSortVO.getName());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        SubjectSort tempTag = subjectSortService.getOne(queryWrapper);
        if (tempTag != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }

        SubjectSort subjectSort = new SubjectSort();
        BeanUtil.copyProperties(subjectSortVO, subjectSort, SysConf.STATUS);
        subjectSort.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editSubjectSort(SubjectSortVO subjectSortVO) {
        SubjectSort subjectSort = subjectSortService.getById(subjectSortVO.getUid());
        if (subjectSort != null && !subjectSort.getName().equals(subjectSortVO.getName())) {
            QueryWrapper<SubjectSort> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.NAME, subjectSortVO.getName());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            SubjectSort tempTag = subjectSortService.getOne(queryWrapper);
            if (tempTag != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(subjectSortVO, subjectSort, SysConf.STATUS, SysConf.UID);
        subjectSort.setUpdateTime(new Date());
        subjectSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSubjectSort(List<SubjectSortVO> subjectSortVOList) {
        if (subjectSortVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        subjectSortVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        // 判断要删除的分类，是否有资源
        QueryWrapper<Subject> subjectQueryWrapper = new QueryWrapper<>();
        subjectQueryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        subjectQueryWrapper.in(BaseSQLConf.SUBJECT_SORT_UID, uids);
        Integer count = subjectService.count(subjectQueryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage("该分类下还存在专栏，无法删除");
        }

        Collection<SubjectSort> tagList = subjectSortService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = subjectSortService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
