package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.vo.SubjectVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;
import java.util.Map;

/**
 * 专题表 服务类
 *
 * @author 陌溪
 * @date 2020年8月22日22:03:52
 */
public interface SubjectService extends SuperService<Subject> {

    /**
     * 获取专题列表
     *
     * @param subjectVO
     * @return
     */
    public IPage<Subject> getPageList(SubjectVO subjectVO);

    /**
     * 新增专题
     *
     * @param subjectVO
     */
    public String addSubject(SubjectVO subjectVO);

    /**
     * 通过业务uid，获取专栏列表
     *
     * @param bizUidList
     * @return
     */
    Map<String, List<Subject>> getSubjectByBizList(List<String> bizUidList);

    /**
     * 编辑专题
     *
     * @param subjectVO
     */
    public String editSubject(SubjectVO subjectVO);

    /**
     * 批量删除专题
     *
     * @param subjectVOList
     */
    public String deleteBatchSubject(List<SubjectVO> subjectVOList);

    /**
     * 转化专栏信息【增加图片】
     * @param subject
     * @return
     */
    Subject convertSubject(Subject subject);

}
