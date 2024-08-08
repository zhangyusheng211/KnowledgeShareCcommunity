package com.moxi.mogublog.commons.schema;

import lombok.Data;
import lombok.ToString;

/**
 * 生成m3u8文件request
 */
@Data
@ToString
public class GenerateM3u8FileRequest {
    /**
     * 文件uid
     */
    private String fileUid;

    /**
     * 媒资uid
     */
    private String mediaUid;

    /**
     * 视频uid
     */
    private String videoUid;
}
