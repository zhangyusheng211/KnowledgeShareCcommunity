package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 资源VO
 *
 * @author: 陌溪
 * @date: 2023年3月23日23:41:45
 */
@ToString
@Data
public class ResourceVO extends BaseVO<ResourceVO> {

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源简介
     */
    private String summary;

    /**
     * 资源详情
     */
    private String content;

    /**
     * 资源封面图片UID
     */
    private String fileUid;

    /**
     * 资源分类
     */
    private Integer resourceSort;

    /**
     * 网盘路径
     */
    private String panPath;

    /**
     * 网盘提取码
     */
    private String panCode;

    /**
     * 网盘类型：1 阿里网盘，2 百度网盘
     */
    private Integer panType;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 点赞数
     */
    private Integer praiseCount;

    /**
     * 折扣价格
     */
    private Integer discountPrice;

    /**
     * 收费模式 1:免费  2:付费  2:折扣价  3:会员免费
     */
    private Integer chargeType;

    /**
     * 支付类型：1:积分支付，2: 现金支付
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
     * 价格
     */
    private Integer price;

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
     * 用户uid
     */
    private String userUid;


}
