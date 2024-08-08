package com.moxi.mogublog.utils.ffmpeg.m3u8;

import lombok.Data;
import lombok.ToString;

/**
 * 生成m3u8文件request
 */
@Data
@ToString
public class GenerateM3u8FileResp {
    /**
     * 媒资长度
     */
    private String videoLength;

    /**
     * M3u8文件URL
     */
    private String m3u8Url;
}
