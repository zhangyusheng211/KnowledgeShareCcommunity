package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.entity.SubjectItem;
import com.moxi.mogublog.commons.entity.SubjectSort;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mogublog.commons.vo.SubjectVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.SubjectMapper;
import com.moxi.mogublog.xo.service.SubjectItemService;
import com.moxi.mogublog.xo.service.SubjectService;
import com.moxi.mogublog.xo.service.SubjectSortService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.expression.Lists;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 专题item表 服务实现类
 * </p>
 *
 * @author 陌溪
 * @since 2020年8月23日07:58:12
 */
@Service
public class SubjectServiceImpl extends SuperServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SubjectItemService subjectItemService;
    @Resource
    private SubjectSortService subjectSortService;
    @Resource
    FileFeignUtil fileFeignUtil;

    @Override
    public IPage<Subject> getPageList(SubjectVO subjectVO) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(subjectVO.getKeyword()) && !StringUtils.isEmpty(subjectVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.SUBJECT_NAME, subjectVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(subjectVO.getUid()) && !StringUtils.isEmpty(subjectVO.getUid().trim())) {
            queryWrapper.eq(SQLConf.UID, subjectVO.getUid().trim());
        }
        if (StringUtils.isNotEmpty(subjectVO.getSubjectSortUid()) && !StringUtils.isEmpty(subjectVO.getSubjectSortUid().trim())) {
            queryWrapper.eq(SQLConf.SUBJECT_SORT_UID, subjectVO.getSubjectSortUid().trim());
        }
        if (subjectVO.getIsSelection() != null) {
            queryWrapper.eq(SQLConf.IS_SELECTION, subjectVO.getIsSelection());
        }
        if (subjectVO.getIsOriginal() != null) {
            queryWrapper.eq(SQLConf.IS_ORIGINAL, subjectVO.getIsOriginal());
        }
        if (subjectVO.getIsPublish() != null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, subjectVO.getIsPublish());
        }
        if (subjectVO.getVisitAuth() != null) {
            queryWrapper.eq(SQLConf.VISIT_AUTH, subjectVO.getVisitAuth());
        }

        if (StringUtils.isNotEmpty(subjectVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(subjectVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(subjectVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(subjectVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column, SQLConf.SORT);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }

        Page<Subject> page = new Page<>();
        page.setCurrent(subjectVO.getCurrentPage());
        page.setSize(subjectVO.getPageSize());
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        IPage<Subject> pageList = subjectService.page(page, queryWrapper);
        List<Subject> list = pageList.getRecords();

        List<String> fileUidList = new ArrayList<>();
        List<String> subjectUidList = new ArrayList<>();
        List<String> subjectSortUidList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidList.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getSubjectSortUid())) {
                subjectSortUidList.add(item.getSubjectSortUid());
            }
            subjectUidList.add(item.getUid());
        });
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        //获取专栏下博客数
        Map<String, Integer> subjectCountMap = subjectItemService.getCountBySubjectUidList(subjectUidList);
        // 获取专栏分类
        Map<String, SubjectSort> subjectSortMap = new HashMap<>();
        if (subjectSortUidList.size() > 0) {
            List<SubjectSort> subjectSortList = subjectSortService.listByIds(subjectSortUidList);
            subjectSortMap = subjectSortList.stream().collect(Collectors.toMap(SubjectSort::getUid, obj -> obj));
        }

        for (Subject item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), BaseSysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
            // 设置专栏数量
            Integer count = subjectCountMap.get(item.getUid()) != null ? subjectCountMap.get(item.getUid()) : 0;
            item.setSubjectItemCount(count);

            // 设置专栏分类
            item.setSubjectSort(subjectSortMap.get(item.getSubjectSortUid()));

            // 对数据进行反序列化
            if (StringUtils.isNotEmpty(item.getVisitAuthExtra())) {
                VisitAuthExtra visitAuthExtraVo = JsonUtils.jsonToPojo(item.getVisitAuthExtra(), VisitAuthExtra.class);
                item.setVisitAuthExtraVo(visitAuthExtraVo);
            } else {
                item.setVisitAuthExtraVo(new VisitAuthExtra());
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addSubject(SubjectVO subjectVO) {
        /**
         * 判断需要增加的分类是否存在
         */
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.SUBJECT_NAME, subjectVO.getSubjectName());
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(BaseSysConf.LIMIT_ONE);
        Subject tempSubject = subjectService.getOne(queryWrapper);
        if (tempSubject != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }
        Subject subject = new Subject();
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(subjectVO, subject, SysConf.STATUS);
        // 对数据进行序列化存储
        if (subjectVO.getVisitAuthExtraVo() != null) {
            String visitAuthExtra = JsonUtils.objectToJson(subjectVO.getVisitAuthExtraVo());
            subject.setVisitAuthExtra(visitAuthExtra);
        }
        subject.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public Map<String, List<Subject>> getSubjectByBizList(List<String> bizUidList) {
        Map<String, List<Subject>> map = new HashMap<>();
        if (bizUidList.size() == 0) {
            return map;
        }
        QueryWrapper<SubjectItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.in(SQLConf.BLOG_UID, bizUidList);
        queryWrapper.orderByDesc(SQLConf.SORT);
        List<SubjectItem> subjectItemList = subjectItemService.list(queryWrapper);

        List<String> subjectUidList = new ArrayList<>();
        for (SubjectItem subjectItem : subjectItemList) {
            subjectUidList.add(subjectItem.getSubjectUid());
        }
        // 查询出所有类目
        Map<String, Subject> subjectMap = new HashMap<>();
        if (subjectUidList.size() > 0) {
            List<Subject> subjectList = subjectService.listByIds(subjectUidList);
            for (Subject subject : subjectList) {
                subjectMap.put(subject.getUid(), subject);
            }
        }

        for (SubjectItem subjectItem : subjectItemList) {
            if (StringUtils.isEmpty(subjectItem.getUid()) || StringUtils.isEmpty(subjectItem.getSubjectUid())) {
                continue;
            }
            Subject subject = subjectMap.get(subjectItem.getSubjectUid());
            List<Subject> tempList = map.get(subjectItem.getBlogUid());
            if (tempList == null) {
                tempList = new ArrayList<>();
            }
            tempList.add(subject);
            map.put(subjectItem.getBlogUid(), tempList);
        }
        return map;
    }

    @Override
    public String editSubject(SubjectVO subjectVO) {
        Subject subject = subjectService.getById(subjectVO.getUid());
        /**
         * 判断需要编辑的分类是否存在
         */
        if (!subject.getSubjectName().equals(subjectVO.getSubjectName())) {
            QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(BaseSQLConf.SUBJECT_NAME, subjectVO.getSubjectName());
            queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
            Subject tempSubject = subjectService.getOne(queryWrapper);
            if (tempSubject != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(subjectVO, subject, SysConf.STATUS, SysConf.UID);
        // 对数据进行序列化存储
        if (subjectVO.getVisitAuthExtraVo() != null) {
            String visitAuthExtra = JsonUtils.objectToJson(subjectVO.getVisitAuthExtraVo());
            subject.setVisitAuthExtra(visitAuthExtra);
        }
        subject.setUpdateTime(new Date());
        subject.updateById();

        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSubject(List<SubjectVO> subjectVOList) {
        if (subjectVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        subjectVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Subject> subjectList = subjectService.listByIds(uids);
        subjectList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = subjectService.updateBatchById(subjectList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public Subject convertSubject(Subject subject) {
        if (subject == null) {
            return null;
        }
        if (StringUtils.isEmpty(subject.getFileUid())) {
            return subject;
        }
        List<String> fileUidList = new ArrayList<>();
        fileUidList.add(subject.getFileUid());
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        //获取图片
        List<String> pictureUidsTemp = StringUtils.changeStringToString(subject.getFileUid(), BaseSysConf.FILE_SEGMENTATION);
        List<String> pictureListTemp = new ArrayList<>();
        pictureUidsTemp.forEach(picture -> {
            pictureListTemp.add(pictureMap.get(picture));
        });
        subject.setPhotoList(pictureListTemp);
        return subject;
    }

}
