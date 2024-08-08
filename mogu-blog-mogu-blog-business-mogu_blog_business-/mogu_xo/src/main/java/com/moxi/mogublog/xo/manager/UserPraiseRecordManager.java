package com.moxi.mogublog.xo.manager;

import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mougblog.base.enums.EResourceType;

import java.util.List;

/**
 * 用户点赞模块
 *
 * @author 遇见
 */
public interface UserPraiseRecordManager {

    /**
     * 点赞指定资源文件
     *
     * @return
     */
    String praise(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 点踩指定资源文件
     *
     * @return
     */
    String tread(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 取消点赞
     *
     * @return
     */
    String cancelPraise(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 取消点踩
     *
     * @return
     */
    String cancelTread(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 是否已点赞/踩
     *
     * @param userPraiseRecordVO
     * @return
     */
    Boolean hasPraised(UserPraiseRecordVO userPraiseRecordVO);

    /**
     * 获取点赞的用户列表
     *
     * @param userPraiseRecordVO
     * @return
     */
    List<User> getPraiseList(UserPraiseRecordVO userPraiseRecordVO);
}
