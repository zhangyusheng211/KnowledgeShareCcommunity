package com.moxi.mogublog.commons.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 媒资视频对象 t_media_video
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_video")
public class MediaVideo extends SuperEntity<MediaVideo>
{

    /** 标题 */
    private String title;

    /** url地址 */
    private String url;

    /** 文件后缀 */
    private String ext;

    /** 播放长度 */
    private String length;

    /** 类型 */
    private String type;

    /** 存储类型 */
    private String storageType;

    /** 创建人 */
    private String createByUid;

    /** 更新人 */
    private String updateByUid;

    /** 删除标志 */
    private String delFlag;

    /** 媒资uid */
    private String mediaInfoUid;

    /** 文件大小 */
    private Long filesize;

    /**
     * 视频转化状态
     */
    private String videoStatus;

    /** 超清 */
    private String superDefinitionUrl;

    /** 高清 */
    private String highDefinitionUrl;

    /** 标清 */
    private String standardDefinitionUrl;

    /** 转换错误信息 */
    private String errorMsg;

    /** 备注 */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 文件uid
     */
    private String fileUid;

    @TableField(exist = false)
    private String m3u8Url;
}
