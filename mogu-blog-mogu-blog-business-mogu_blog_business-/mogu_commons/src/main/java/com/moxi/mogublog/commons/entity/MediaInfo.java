package com.moxi.mogublog.commons.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 媒资信息对象 t_media_info
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_info")
public class MediaInfo extends SuperEntity<MediaInfo>
{

    /** 封面 */
    private String images;

    /** 标题 */
    private String title;

    /** 类型 */
    private String type;

    /** 国家 */
    private String country;

    /** 标签 */
    private String tagId;

    /** 描述 */
    private String description;

    /** 发布人 */
    private String publishByUid;

    /** 发布时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishTime;

    /** 创建人 */
    private String createByUid;

    /** 更新人 */
    private String updateByUid;

    /** 删除标志 */
    private String delFlag;

    /** 阅读数 */
    private Integer clickCount;

    /** 评论数 */
    private Integer commentCount;

    /** 关注数 */
    private Integer followCount;

    /** 收藏数 */
    private Integer collectionCount;

    /** 支持数 */
    private Integer supportCount;

    /** 反对数 */
    private Integer opposeCount;

    /** 分享数 */
    private Integer shareCount;

    /** 是否可以评论 */
    private Integer openComment;

    /** 是否可以下载 */
    private Integer openDownload;

    /** 价格 */
    private BigDecimal price;

    /** 评分 */
    private BigDecimal rate;

    /** 分类uid */
    private String categoryUid;

    /** 分类名称 */
    private String categoryName;

    /** 专属二维码 */
    private String qrcodePath;

    /** 开启二维码 0 关闭 1开启 */
    private Integer openPassword;

    /** 秘钥 */
    private String password;

    /** 剧照 */
    private String stills;

    /** 视频总长度 */
    private Long totalVideoLength;

    /** 简介 */
    private String summary;

    /** 拼音 */
    private String en;

    /** 首字母大写 */
    private String letter;

    /** 语言 */
    private String lang;

    /** 备注 */
    private String remark;

    /** 排序字段 */
    private Integer sort;

    /** 电影视频信息 */
    @TableField(exist = false)
    private List<MediaVideo> videoList;

    /** 演员信息 */
    @TableField(exist = false)
    private List<MediaInfoActor> actorList;

    /** 演员信息 */
    @TableField(exist = false)
    private List<MediaInfoActor> directorList;

    /**
     * 发布用户
     */
    @TableField(exist = false)
    private User user;

}
