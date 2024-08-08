package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 播放记录对象 t_media_play_record
 * 
 * @author thh
 * @date 2021-12-14
 */
@Data
@TableName("t_media_play_record")
public class MediaPlayRecord extends SuperEntity<MediaPlayRecord>
{

    /** 用户id */
    private String userUid;

    /** 视频id */
    private String videoUid;

    /** 自定义id */
    private String customUid;

    /** 播放时长 */
    private Long playDuration;

    /** 最后播放位置 */
    private Long playPosition;

    /** ip地址 */
    private String ip;

    /** 省份名称 */
    private String province;

    /** 城市名称 */
    private String city;

    /** 来源域名 */
    private String referer;

    /** 设备类型 */
    private String device;

    /** 操作系统 */
    private String operatingSystem;

    /** 浏览器类型 */
    private String browser;

    /** 终端类型 */
    private String terminal;
}
