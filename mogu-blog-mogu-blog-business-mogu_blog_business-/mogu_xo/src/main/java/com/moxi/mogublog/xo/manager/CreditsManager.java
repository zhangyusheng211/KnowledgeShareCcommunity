package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.schema.UpdateCreditsRequest;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.ECreditType;

/**
 * 积分管理模块
 *
 * @author 遇见
 */
public interface CreditsManager {


    /**
     * 查询用户获取积分情况
     * 该接口为了解决 用户积分获取异常问题
     * 获取数据 包括
     * 签到数据
     * 博客数据
     * 问答数据
     * 评论数据
     *
     * @param userVO
     * @return
     */
    String queryCredits(UserVO userVO);

    /**
     * 补偿用户积分
     *
     * @param creditsLogVO
     * @return
     */
    String compensation(CreditsLogVO creditsLogVO);

    /**
     * 变更积分
     *
     * @param userUid     用户ID
     * @param eCreditType 积分类型
     * @param credit      积分
     * @param resourceUid 资源Uid
     * @return
     */
    String updateCredit(String userUid, ECreditType eCreditType, Integer credit, String resourceUid);

    /**
     * 更新用户积分日志
     * @param updateCreditsRequest
     * @return
     */
    String updateCredit(UpdateCreditsRequest updateCreditsRequest);
}
