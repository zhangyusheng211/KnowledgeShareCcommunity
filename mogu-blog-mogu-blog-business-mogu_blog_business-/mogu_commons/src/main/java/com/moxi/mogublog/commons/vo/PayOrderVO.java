package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * OrderVO 订单支付相关
 *
 * @author: 陌溪
 * @create: 2022年6月9日23:49:58
 */
@Data
@ToString
public class PayOrderVO extends BaseVO<PayOrderVO> {
    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 商品uid
     */
    private String resourceUid;

    /**
     * 商品类型
     */
    private String resourceType;

    /**
     * 订单金额（单位分）
     */
    private long fee;

    /**
     * 资源标题
     */
    private String resourceTitle;

    /**
     * 资源描述
     */
    private String summary;

    /**
     * 支付类型:  1: 积分支付， 2：现金支付
     */
    private Integer payType;

    /**
     * 支付方式：0：默认 1: 支付宝, 2: 微信 3: 第三方支付宝 4: 第三方微信
     */
    private Integer payMethod;

    /**
     * 订单状态
     */
    private Integer orderStatus;


    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 下单时间
     */
    private Date createTimeGt;
}
