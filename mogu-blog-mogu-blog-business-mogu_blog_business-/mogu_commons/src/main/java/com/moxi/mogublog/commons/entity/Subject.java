package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 专题表
 * </p>
 *
 * @author 陌溪
 * @since 2020年8月22日21:56:18
 */
@Data
@TableName("t_subject")
public class Subject extends SuperEntity<Subject> {

    private static final long serialVersionUID = 1L;

    /**
     * 专题名
     */
    private String subjectName;

    /**
     * 分类简介
     */
    private String summary;

    /**
     * 封面图片UID
     */
    private String fileUid;

    /**
     * 专题点击数
     */
    private String clickCount;

    /**
     * 专题收藏数
     */
    private String collectCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;

    /**
     * 专题分类uid
     */
    private String subjectSortUid;

    /**
     * 是否精选
     */
    private Integer isSelection;

    /**
     * 是否上架
     */
    private Integer isPublish;

    /**
     * 专栏作者
     */
    private String author;

    /**
     * 是否原创
     */
    private String isOriginal;

    /**
     * 专栏出处
     */
    private String origin;

    /**
     * 阅读权限：1 公开、2 登录后可见、3 评论后可见、4 验证可见、5 会员可见、6 付费可见、7 点赞阅读、8 收藏阅读、9 关注阅读、10 密码可见、11 仅自己可见
     */
    private String visitAuth;

    /**
     * 访问可见扩展字段【如：存储哪些会员可见、密码可见对应的密码】 对应 VisitAuthExtra 序列化得到
     */
    private String visitAuthExtra;

    /**
     * 价格【单位分】
     */
    private Integer price;

    /**
     * 支付类型：1:积分支付，2: 现金支付
     */
    private Integer payType;

    /**
     * 专栏内容
     */
    private String content;

    /**
     * 开启图片标题
     */
    private Integer openPictureTitle;

    /**
     * 分类图
     */
    @TableField(exist = false)
    private List<String> photoList;

    /**
     * 专题章节数
     */
    @TableField(exist = false)
    private Integer subjectItemCount;

    /**
     *  访问可见扩展字段
     */
    @TableField(exist = false)
    private VisitAuthExtra visitAuthExtraVo;

    /**
     * 支付订单数
     */
    @TableField(exist = false)
    private Integer payOrderCount;

    /**
     * 专栏分类
     */
    @TableField(exist = false)
    private SubjectSort subjectSort;

}
