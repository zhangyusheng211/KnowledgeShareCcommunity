package com.moxi.mogublog.picture.service.impl;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.service.FileSortService;
import com.moxi.mogublog.picture.service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

/**
 * @ClassName
 * @Desc
 * @Author tanhh
 * @Date 2022/10/23 1:18
 * @Version 1.0
 **/
public abstract class AbstractUploadFile implements IUploadService {

    @Autowired
    protected FileSortService fileSortService;
    /**
     * 本地图片上传路径
     */
    @Value(value = "${file.upload.path}")
    protected String path;

    @Value(value = "${file.upload.scaleSize}")
    protected Long scaleSize;

    // 聊天图片默认压缩大小
    @Value(value = "${file.upload.chatScaleSize}")
    protected Long chatScaleSize;

    // 动态图片默认压缩大小
    @Value(value = "${file.upload.momentScaleSize}")
    protected Long momentScaleSize;

    public abstract String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String newFileName) throws IOException;

    public abstract String deleteFile(String fileName, SystemConfig systemConfig);
}
