package com.moxi.mogublog.commons.schema;

import lombok.Data;

/**
 * 用于传输的文件DTO对象
 */

@Data
public class FileDto {
    private String fileUid;

    /**
     * url
     */
    private String url;

    /**
     * m3u8文件
     */
    private String m3u8Url;
}
