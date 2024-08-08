package com.moxi.mogublog.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Withdraw;
import com.moxi.mogublog.commons.vo.WithdrawVO;
import com.moxi.mougblog.base.service.SuperService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提现表 服务类
 *
 * @author 陌溪
 * @date 2022年7月17日22:12:19
 */
@Transactional(rollbackFor = Exception.class)
public interface WithdrawService extends SuperService<Withdraw> {
    /**
     * 获取提现列表
     *
     * @param withdrawVO
     * @return
     */
    IPage<Withdraw> getPageList(WithdrawVO withdrawVO);

    /**
     * 获取提现金额
     *
     * @param withdrawVO
     * @return
     */
    Long getWithdrawAmount(WithdrawVO withdrawVO);

    /**
     * 新增提现
     *
     * @param withdrawVO
     */
    String addWithdraw(WithdrawVO withdrawVO);


    /**
     * 审核提现
     *
     * @param withdrawVO
     */
    String auditWithdraw(WithdrawVO withdrawVO);

    /**
     * 编辑提现
     *
     * @param withdrawVO
     */
    String editWithdraw(WithdrawVO withdrawVO);


    /**
     * 获取当前用户最近的一次抽奖
     * @return
     */
    Withdraw getRecentWithdraw();
}
