package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.UserMoment;
import com.moxi.mogublog.commons.vo.UserMomentVO;
import com.moxi.mogublog.xo.dto.UserMomentDto;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 用户动态 服务类
 *
 * @author 陌溪
 * @date 2021年12月25日23:53:44
 */
public interface UserMomentService extends SuperService<UserMoment> {

    /**
     * 用户动态信息
     *
     * @param userMomentVO
     * @return
     */
    Page<UserMomentDto> list(UserMomentVO userMomentVO);

    /**
     * 获取动态列表
     *
     * @param userMomentVO
     * @return
     */
    IPage<UserMoment> getPageList(UserMomentVO userMomentVO);

    /**
     * 新增动态
     *
     * @param userMomentVO
     */
    String addUserMoment(UserMomentVO userMomentVO);

    /**
     * 编辑动态
     *
     * @param userMomentVO
     */
    String editUserMoment(UserMomentVO userMomentVO);

    /**
     * 批量删除动态
     *
     * @param userMomentVOList
     */
    String deleteBatchUserMoment(List<UserMomentVO> userMomentVOList);

    /**
     * 获取动态数目
     *
     * @param userMomentVO
     * @return
     */
    Integer getUserMomentCount(UserMomentVO userMomentVO);

    /**
     * 查询动态积分排行榜
     *
     * @return
     */
    List<UserMoment> getLeaderMoment(Boolean refresh);

    /**
     * 审核问题
     *
     * @param userMomentVO
     * @return
     */
    String auditMoment(UserMomentVO userMomentVO);
}
