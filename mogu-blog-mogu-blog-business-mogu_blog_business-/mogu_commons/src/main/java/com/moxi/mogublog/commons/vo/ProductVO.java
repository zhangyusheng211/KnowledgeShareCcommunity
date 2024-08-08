package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 商品实体类
 *
 * @author 陌溪
 * @date 2024年6月1日21:42:29
 */
@ToString
@Data
public class ProductVO extends BaseVO<ProductVO> {

    // 商品名称
    private String name;

    // 商品简介
    private String summary;

    // 分类uid
    private String categoryUid;

    // 价格（分）
    private Integer price;

    // 折扣价格（分）
    private Integer discountPrice;

    // 收费模式 1:免费 2:付费 3:折扣价 4:会员免费
    private Integer chargeType;

    // 支付类型：1:积分支付，2: 现金支付
    private Integer payType;

    // 折扣开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountStartTime;

    // 折扣结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date discountEndTime;

    // 库存数量
    private Integer quantity;

    // 备注
    private String remark;

    // 排序字段
    private Integer sort;

    // 是否发布(1:是，0:否)
    private String isPublish;

    /**
     * 库存数量【-1表示无限库存】
     */
    private Integer stockCount;

    /**
     * 运费【-1表示无需邮寄，0 表示包邮，其他表示运费金额】
     */
    private Integer freight;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 是否精选
     */
    private Integer isSelection;

    /**
     * 封面图文件uid
     */
    private String coverFileUid;

    /**
     * 轮播图文件slidesFileUidList
     */
    private String slidesFileUidList;

    /**
     * 正文内容
     */
    private String content;
}
