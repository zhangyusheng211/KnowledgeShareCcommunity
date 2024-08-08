package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

/**
 * MediaPlayRecordVO
 *
 * @author thh
 * @date 2021-12-14
 */
@ToString
@Data
public class MediaPlayRecordVO extends BaseVO<MediaPlayRecordVO> {

    /**
     * 用户id
     */
    private String userUid;

    /**
     * 视频id
     */
    private String videoUid;

    /**
     * 自定义id
     */
    private String customUid;

    /**
     * 播放时长
     */
    private Long playDuration;

    /**
     * 最后播放位置
     */
    private Long playPosition;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 来源域名
     */
    private String referer;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 操作系统
     */
    private String operatingSystem;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 终端类型
     */
    private String terminal;
}
