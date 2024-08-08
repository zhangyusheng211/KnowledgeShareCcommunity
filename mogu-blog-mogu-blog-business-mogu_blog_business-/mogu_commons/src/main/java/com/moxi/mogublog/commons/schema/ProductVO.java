package com.moxi.mogublog.commons.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * ProductVO 商品VO
 *
 * @author: 陌溪
 * @create: 2022年11月6日18:33:54
 */
@Data
@ToString
public class ProductVO extends BaseVO<ProductVO> {
    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品简介
     */
    private String summary;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 资源uid
     */
    private String resourceUid;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 商品价格（单位分）
     */
    private long price;

    /**
     * 优惠价格
     */
    private long discountPrice;

    /**
     * 收费模式
     */
    private Integer chargeType;

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 折扣开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountStartTime;

    /**
     * 折扣结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountEndTime;

    /**
     * 标题图
     */
    private String photoUrl;
}
