package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Medal;
import com.moxi.mogublog.commons.entity.MedalClassify;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mogublog.commons.vo.MedalVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;
import java.util.Map;

/**
 * 勋章表 服务类
 *
 * @author 陌溪
 * @date 2022年12月27日09:17:57
 */
public interface MedalService extends SuperService<Medal> {

    /**
     * 获取勋章列表
     *
     * @param medalVO
     * @return
     */
    IPage<Medal> getPageList(MedalVO medalVO);

    /**
     * 获取全部勋章列表
     *
     * @return
     */
    List<Medal> getList();

    /**
     * 新增勋章
     *
     * @param medalVO
     * @return
     */
    String addMedal(MedalVO medalVO);

    /**
     * 编辑勋章
     *
     * @param medalVO
     * @return
     */
    String editMedal(MedalVO medalVO);

    /**
     * 批量删除勋章
     *
     * @param medalVOList
     * @return
     */
    String deleteBatchMedal(List<MedalVO> medalVOList);

    /**
     * 给某人颁发某个勋章
     *
     * @param userUid
     * @param medalUid
     * @return
     */
    void awardMedal(String userUid, String medalUid);


    /**
     * 查询用户最近获取的勋章
     *
     * @param medalVO
     * @return
     */
    Medal getMedalByRecent(MedalVO medalVO);

    /**
     * 查询用户获得的勋章 【按分类划分】
     *
     * @param medalClassifyVO
     * @return
     */
    List<MedalClassify> getUserMedalList(MedalClassifyVO medalClassifyVO);

    /**
     * 查询用户展示的勋章
     *
     * @param medalVO
     * @return
     */
    List<Medal> getUserShowMedalList(MedalVO medalVO);

    /**
     * 获取所有勋章出现频率
     * @return
     */
    Map<String, String> getAllMedalFrequency();

}
