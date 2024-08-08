package com.moxi.mogublog.xo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Problem;
import com.moxi.mogublog.commons.vo.ExamVO;
import com.moxi.mogublog.commons.vo.GeneralEditVO;
import com.moxi.mogublog.commons.vo.ProblemVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;
import java.util.Map;

/**
 * 问题表 服务类
 *
 * @author 陌溪
 * @date 2022年3月7日22:23:22
 */
public interface ProblemService extends SuperService<Problem> {

    /**
     * 获取问题列表
     *
     * @param problemVO
     * @return
     */
    IPage<Problem> getPageList(ProblemVO problemVO);

    /**
     * 分页查询博客列表
     *
     * @param problemVO
     * @return
     */
    Page<Problem> queryPage(ProblemVO problemVO);

    /**
     * 问题详情
     *
     * @param problemVO
     * @return
     */
    String getProblem(ProblemVO problemVO);

    /**
     * 新增问题
     *
     * @param problemVO
     * @return
     */
    String addProblem(ProblemVO problemVO);

    /**
     * 编辑问题
     *
     * @param problemVO
     * @return
     */
    String editProblem(ProblemVO problemVO);

    /**
     * 批量删除问题
     *
     * @param problemVOList
     * @return
     */
    String deleteBatchProblem(List<ProblemVO> problemVOList);

    /**
     * 根据关键字搜索面经
     *
     * @param keywords
     * @param currentPage
     * @param pageSize
     * @return
     */
    Map<String, Object> searchProblem(String keywords, Long currentPage, Long pageSize);

    /**
     * 获取问题数目
     *
     * @param problemVO
     * @return
     */
    Integer getProblemCount(ProblemVO problemVO);

    /**
     * 随机一题
     *
     * @param problemVO
     * @return
     */
    Problem randomProblem(ProblemVO problemVO);

    /**
     * 审核问题
     *
     * @param problemVO
     * @return
     */
    String auditProblem(ProblemVO problemVO);

    /**
     * 修改答案
     *
     * @param generalEditVO
     * @return
     */
    String editAnswer(GeneralEditVO generalEditVO);

    /**
     * 生成试卷（问题分类）
     *
     * @param examVO
     * @return
     */
    Map<String, Object> builderExam(ExamVO examVO);

    /**
     * 标签问题数刷新
     * @param
     * @return
     */
    String flushTagProblemCount();
}
