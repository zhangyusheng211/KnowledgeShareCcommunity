package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.ProblemTag;
import com.moxi.mogublog.commons.vo.ProblemTagVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 问题标签表 服务类
 *
 * @author 陌溪
 * @date 2022年3月7日22:23:18
 */
public interface ProblemTagService extends SuperService<ProblemTag> {

    /**
     * 获取问题标签列表
     *
     * @param problemTagVO
     * @return
     */
    IPage<ProblemTag> getPageList(ProblemTagVO problemTagVO);

    /**
     * 获取全部的标签【按层级】
     *
     * @return
     */
    List<ProblemTag> getAllList();

    /**
     * 新增问题标签
     *
     * @param problemTagVO
     * @return
     */
    String addProblemTag(ProblemTagVO problemTagVO);

    /**
     * 编辑问题标签
     *
     * @param problemTagVO
     * @return
     */
    String editProblemTag(ProblemTagVO problemTagVO);

    /**
     * 批量删除问题标签
     *
     * @param problemTagVOList
     * @return
     */
    String deleteBatchProblemTag(List<ProblemTagVO> problemTagVOList);
}
