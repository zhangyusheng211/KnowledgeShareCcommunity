package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * 提现表VO
 * @author 陌溪
 * @date 2023年8月2日20:29:42
 */
@Data
public class WithdrawVO extends BaseVO<WithdrawVO> {
    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 提现账号
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String account;

    /**
     * 提现收款码文件UID【微信收款码、支付宝收款码】
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String fileUid;

    /**
     * 提现订单号
     */
    private String orderUid;

    /**
     * 提现的金额
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private Long amount;

    /**
     * 提现状态 0：初始状态，1：提现中，2：提现失败，3：提现完成
     */
    private Integer withdrawStatus;

    /**
     * 审核人
     */
    private String adminUid;

    /**
     * 审核状态
     */
    private String auditStatus;
    /**
     * 审批拒绝原因
     */
    private String rejectReason;

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 备注
     */
    private String remark;
}
