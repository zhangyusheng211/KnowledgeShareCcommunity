package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 资源表
 *
 * @author 陌溪
 * @since 2023年3月23日09:11:52
 */
@Data
@TableName("t_resource")
public class Resource extends SuperEntity<Resource> {

    private static final long serialVersionUID = 1L;

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
     * 资源分类UID
     */
    private Integer resourceSort;

    /**
     * 云盘路径
     */
    private String panPath;

    /**
     * 云盘提取码
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
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 收藏数
     */
    private Integer collectCount;

    /**
     * 点赞数
     */
    private Integer praiseCount;

    /**
     * 收费模式 0:免费  1:正常  2:折扣价  3:会员免费
     */
    private Integer chargeType;

    /**
     * 支付类型：1:积分支付，2: 现金支付
     */
    private Integer payType;

    /**
     * 折扣开始时间
     */
    private Date discountStartTime;

    /**
     * 折扣结束时间
     */
    private Date discountEndTime;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 折扣价格
     */
    private Integer discountPrice;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 资源标题图
     */
    @TableField(exist = false)
    private String photoUrl;

    /**
     * 发布的用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 是否支付
     */
    @TableField(exist = false)
    private boolean isPay;

    /**
     * 支付订单数
     */
    @TableField(exist = false)
    private int payOrderCount;
}
