package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.SubjectSort;
import com.moxi.mogublog.commons.vo.SubjectSortVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 专题分类表 服务类
 *
 * @author 陌溪
 * @date 2022年9月25日22:06:14
 */
public interface SubjectSortService extends SuperService<SubjectSort> {

    /**
     * 获取专题分类列表
     *
     * @param subjectSortVO
     * @return
     */
    IPage<SubjectSort> getPageList(SubjectSortVO subjectSortVO);

    /**
     * 新增专题分类
     *
     * @param subjectSortVO
     * @return
     */
    String addSubjectSort(SubjectSortVO subjectSortVO);

    /**
     * 编辑专题分类
     *
     * @param subjectSortVO
     * @return
     */
    String editSubjectSort(SubjectSortVO subjectSortVO);

    /**
     * 批量删除专题分类
     *
     * @param subjectSortVOList
     * @return
     */
    String deleteBatchSubjectSort(List<SubjectSortVO> subjectSortVOList);
}
