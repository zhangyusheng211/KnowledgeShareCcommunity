package com.moxi.mogublog.xo.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.CreditsLog;
import com.moxi.mogublog.commons.entity.LuckyActivity;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mogublog.commons.vo.LuckyActivityVO;
import com.moxi.mougblog.base.service.SuperService;
import com.moxi.mougblog.base.vo.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * 积分日志表 服务类
 *
 * @author 陌溪
 * @date 2021年11月27日16:38:41
 */
public interface CreditsLogService extends SuperService<CreditsLog> {

    /**
     * 分页查询积分流水
     * @param creditsLogVO
     * @return
     */
    IPage<CreditsLog> getPageList(CreditsLogVO creditsLogVO);

    /**
     * 新增积分日志
     *
     * @return
     */
    String add(CreditsLogVO creditsLogVO);

    /**
     * 查询积分记录
     *
     * @param pageInfo
     * @return
     */
    Page queryList(PageInfo pageInfo);

    /**
     * 新增积分操作记录
     *
     * @param creditsLogVO
     */
    void addCreditsLog(CreditsLogVO creditsLogVO);

    /**
     * 查询积分
     *
     * @param page
     * @param uid
     * @return
     */
    Page<CreditsLogVO> queryCredits(Page page, String uid);

    /**
     * 查询指定动作
     *
     * @param userUid 用户Uid
     * @param date    时间
     * @param code    动作
     * @return
     */
    Integer queryCount(String userUid, Date date, String code);

    /**
     * 查询积分月榜
     *
     * @return
     */
    List<CreditsLog> getLeaderMonth(Boolean refresh);

    /**
     * 查询积分周榜
     *
     * @return
     */
    List<CreditsLog> getLeaderWeek(Boolean refresh);

}
