package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.UserMomentTopic;
import com.moxi.mogublog.commons.vo.UserMomentTopicVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 用户动态话题 服务类
 *
 * @author 陌溪
 * @date 2021年12月25日23:55:15
 */
public interface UserMomentTopicService extends SuperService<UserMomentTopic> {
    /**
     * 获取动态话题列表
     *
     * @param userMomentTopicVO
     * @return
     */
    IPage<UserMomentTopic> getPageList(UserMomentTopicVO userMomentTopicVO);

    /**
     * 获取全部动态话题列表
     *
     * @return
     */
    List<UserMomentTopic> getList();

    /**
     * 新增动态话题
     *
     * @param userMomentTopicVO
     */
    String addUserMomentTopic(UserMomentTopicVO userMomentTopicVO);

    /**
     * 编辑动态话题
     *
     * @param userMomentTopicVO
     */
    String editUserMomentTopic(UserMomentTopicVO userMomentTopicVO);

    /**
     * 批量删除动态话题
     *
     * @param userMomentTopicVOList
     */
    String deleteBatchUserMomentTopic(List<UserMomentTopicVO> userMomentTopicVOList);
}
