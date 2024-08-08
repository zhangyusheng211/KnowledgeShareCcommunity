package com.moxi.mogublog.commons.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.moxi.mogublog.commons.schema.VisitAuthExtra;
import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * SubjectVO
 *
 * @author: 陌溪
 * @create: 2020年8月22日21:53:40
 */
@Data
public class SubjectVO extends BaseVO<SubjectVO> {

    /**
     * 专题名
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String subjectName;

    /**
     * 专题介绍
     */
    private String summary;

    /**
     * 封面图片UID
     */
    private String fileUid;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 专题点击数
     */
    private String clickCount;

    /**
     * 专题收藏数
     */
    private String collectCount;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

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
    @NotBlank(groups = {Insert.class, Update.class, Default.class})
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
     *  访问可见扩展字段
     */
    @TableField(exist = false)
    private VisitAuthExtra visitAuthExtraVo;
}
