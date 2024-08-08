package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MedalClassify;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 勋章分类表 服务类
 *
 * @author 陌溪
 * @date 2022年12月27日09:17:57
 */
public interface MedalClassifyService extends SuperService<MedalClassify> {


    /**
     * 获取全部勋章分类列表
     *
     * @return
     */
    IPage<MedalClassify> getPageList(MedalClassifyVO medalClassifyVO);

    /**
     * 新增勋章分类
     *
     * @param medalClassifyVO
     * @return
     */
    String addMedalClassify(MedalClassifyVO medalClassifyVO);

    /**
     * 编辑勋章分类
     *
     * @param medalClassifyVO
     * @return
     */
    String editMedalClassify(MedalClassifyVO medalClassifyVO);

    /**
     * 批量删除勋章分类
     *
     * @param medalClassifyVOList
     * @return
     */
    String deleteBatchMedalClassify(List<MedalClassifyVO> medalClassifyVOList);

}
