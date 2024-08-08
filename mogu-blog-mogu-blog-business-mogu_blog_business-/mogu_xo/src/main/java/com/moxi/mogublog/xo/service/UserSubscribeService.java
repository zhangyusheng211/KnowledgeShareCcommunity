package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Collect;
import com.moxi.mogublog.commons.entity.Tag;
import com.moxi.mogublog.commons.entity.UserSubscribe;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.commons.vo.TagVO;
import com.moxi.mogublog.commons.vo.UserSubscribeVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 订阅表 服务类
 *
 * @author 陌溪
 * @date 2023年12月23日10:19:15
 */
public interface UserSubscribeService extends SuperService<UserSubscribe> {

    /**
     * 新增订阅
     *
     * @param userSubscribeVO
     * @return
     */
    String addUserSubscribe(UserSubscribeVO userSubscribeVO);

    /**
     * 取消订阅
     *
     * @param userSubscribeVO
     * @return
     */
    String deleteUserSubscribe(UserSubscribeVO userSubscribeVO);

    /**
     * 获取订阅列表
     *
     * @param userSubscribeVO
     * @return
     */
    IPage<UserSubscribe> getPageList(UserSubscribeVO userSubscribeVO);

    /**
     * 校验用户是否订阅
     * @param userSubscribeVO
     * @return
     */
    boolean checkUserSubscribe(UserSubscribeVO userSubscribeVO);
}
