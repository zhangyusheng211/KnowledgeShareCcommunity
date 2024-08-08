package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.commons.vo.UserWatchVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.Collection;
import java.util.List;

/**
 * 用户关注表 服务类
 *
 * @author 陌溪
 * @date 2021年6月13日17:19:42
 */
public interface UserWatchService extends SuperService<UserWatch> {

    /**
     * 获取用户关注列表
     *
     * @param userWatchVO
     * @return
     */
    IPage<UserWatch> getPageList(UserWatchVO userWatchVO);

    /**
     * 关注某人
     *
     * @return
     */
    String addUserWatch(UserWatchVO userWatchVO);

    /**
     * 取消关注
     *
     * @return
     */
    String deleteUserWatch(UserWatchVO userWatchVO);


    /**
     * 判断是否关注了该用户
     *
     * @param userWatchVO
     * @return
     */
    Boolean checkUserWatch(UserWatchVO userWatchVO);

    /**
     * 获取粉丝或者关注者数量
     *
     * @param userWatchVO
     * @return
     */
    Integer getUserWatchCount(UserWatchVO userWatchVO);

    /**
     * 转换UserWatch
     *
     * @param userWatchList
     * @return
     */
    List<UserWatch> convertUserWatchList(Collection<UserWatch> userWatchList);

    /**
     * 检查当前用户是否关注过默认
     *
     * @param userWatchVO
     * @return
     */
    Integer checkCurrentUserWatch(UserWatchVO userWatchVO);


}
