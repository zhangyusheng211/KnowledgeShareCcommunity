package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * 提现表VO
 * @author 陌溪
 * @date 2023年8月2日20:32:17
 */
@Data
public class OrderAmountLogVO extends BaseVO<OrderAmountLogVO> {

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 操作类型：1 分账、 2 提现 【对应不同的订单】
     */
    private Integer businessType;

    /**
     * 资源UID
     */
    private String resourceUid;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 用户分账金额【分账完成时，用户获得的金额】
     */
    private Long userAmount;

    /**
     * 平台的金额【平台参与分账，收款金额默认到平台】
     */
    private Long platformAmount;

    /**
     * 账户原金额
     */
    private Long oldAmount;

    /**
     * 账户剩余金额
     */
    private Long newAmount;

    /**
     * 备注
     */
    private String remark;
}
