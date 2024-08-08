package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.GeneralEdit;
import com.moxi.mogublog.commons.vo.GeneralEditVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 通用修改表
 *
 * @author 陌溪
 * @date 2022年4月14日22:23:22
 */
public interface GeneralEditService extends SuperService<GeneralEdit> {


    /**
     * 获取问题列表
     *
     * @param generalEditVO
     * @return
     */
    IPage<GeneralEdit> getGeneralEditList(GeneralEditVO generalEditVO);

    /**
     * 批量删除
     *
     * @param generalEditVOList
     * @return
     */
    String deleteBatchGeneralEdit(List<GeneralEditVO> generalEditVOList);

    /**
     * 审核修改
     *
     * @param generalEditVO
     * @return
     */
    String auditGeneralEdit(GeneralEditVO generalEditVO);

    /**
     * 修改答案
     *
     * @param generalEditVO
     * @return
     */
    public String generalEdit(GeneralEditVO generalEditVO);
}
