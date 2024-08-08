package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mogublog.commons.entity.UserAccount;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mogublog.commons.vo.VipConfigVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 会员配置
 *
 * @author 陌溪
 * @date 2024年5月26日08:28:04
 */
public interface VipConfigService extends SuperService<VipConfig> {
    /**
     * 获取会员配置列表
     *
     * @param resourceVO
     * @return
     */
    IPage<VipConfig> getPageList(VipConfigVO resourceVO);

    /**
     * 新增会员配置
     *
     * @param resourceVO
     */
    String addVipConfig(VipConfigVO resourceVO);

    /**
     * 编辑会员配置
     *
     * @param resourceVO
     */
    String editVipConfig(VipConfigVO resourceVO);

    /**
     * 批量删除会员配置
     *
     * @param resourceVOList
     */
    String deleteBatchVipConfig(List<VipConfigVO> resourceVOList);
}
