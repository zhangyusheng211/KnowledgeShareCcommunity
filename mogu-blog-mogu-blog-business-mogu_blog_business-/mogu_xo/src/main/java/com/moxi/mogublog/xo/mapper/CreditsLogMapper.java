package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.CreditsLog;
import com.moxi.mogublog.commons.vo.CreditsLogVO;
import com.moxi.mougblog.base.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 积分日志表 Mapper 接口
 *
 * @author 陌溪
 * @since 2021年11月27日16:38:02
 */
public interface CreditsLogMapper extends SuperMapper<CreditsLog> {
    /**
     * 查询分页
     *
     * @param page
     * @param userUid 用户Uid
     * @return
     */
    Page<CreditsLogVO> queryList(Page page, @Param("userUid") String userUid);

    /**
     * 新增日志操作记录
     *
     * @param creditsLogVO
     */
    void addCreditsLog(@Param("creditsLogVO") CreditsLogVO creditsLogVO);

    /**
     * 查询积分发放情况
     *
     * @param page
     * @param userUid
     * @return
     */
    Page<CreditsLogVO> queryCredits(Page page, @Param("userUid") String userUid);

    /**
     * 查询某个用户某天的 动作是否上限
     * 包含后续补签
     *
     * @param userUid
     * @param day
     * @param actionCode
     * @return
     */
    Integer queryCount(@Param("userUid") String userUid, @Param("day") String day, @Param("actionCode") String actionCode);
}
