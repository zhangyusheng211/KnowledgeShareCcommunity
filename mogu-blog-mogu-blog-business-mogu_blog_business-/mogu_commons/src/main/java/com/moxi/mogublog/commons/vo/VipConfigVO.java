package com.moxi.mogublog.commons.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * RoleVO
 * </p>
 *
 * @author 陌溪
 * @since 2019年12月20日16:47:02
 */
@Data
public class VipConfigVO extends BaseVO<VipConfigVO> {

    // 会员名称
    private String name;

    // 会员简介
    private String summary;

    // 价格（分）
    private int price;

    // 折扣价格（分）
    private int discountPrice;

    // 会员生效天数
    private int effectDay;

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

    // 额外配置
    private String extra;

    // 备注
    private String remark;

    // 排序字段
    private int sort;

    // 是否发布(1:是，0:否)
    private Integer isPublish;

    /**
     * 用户标签
     */
    private int userTag;

}
