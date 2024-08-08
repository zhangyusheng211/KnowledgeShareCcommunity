package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mogublog.commons.schema.ProductVO;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付订单实体
 */
@Data
@TableName("t_pay_order")
public class PayOrder extends SuperEntity<PayOrder> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 商家用户id
     */
    private String merchantUserUid;


    /**
     * 订单状态：0：待支付，1：服务中，2：已完成，3：已取消
     */
    private Integer orderStatus;

    /**
     * 支付类型:  1: 积分支付, 2: 现金支付
     */
    private Integer payType;

    /**
     * 支付方式：0：默认 1: 支付宝, 2: 微信 3: 第三方支付宝 4: 第三方微信
     */
    private Integer payMethod;

    /**
     * 资源uid
     */
    private String resourceUid;

    /**
     * 商品类型
     */
    private String resourceType;

    /**
     * 订单金额（单位分）
     */
    private Long price;

    /**
     * 资源标题
     */
    private String  title;

    /**
     * 资源描述
     */
    private String summary;

    /**
     * 商品价格
     */
    private Long productPrice;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 商品快照
     */
    private String snapshot;


    // 以下字段不存入数据库
    /**
     * 商品信息
     */
    @TableField(exist = false)
    private ProductVO productVO;

    /**
     * 下单人用户信息
     */
    @TableField(exist = false)
    private User user;
}
