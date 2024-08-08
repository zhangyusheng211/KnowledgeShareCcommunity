package com.moxi.mogublog.picture.service.impl;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.annotation.UploadType;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.util.MinioUtil;
import com.moxi.mogublog.picture.util.MoGuFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地对象存储服务 Minio文件上传实现类
 *
 * @author 陌溪
 * @date 2020年10月19日11:13:21
 */
@Service
@Slf4j
@UploadType({UploadTypeEnum.MINIO_UPLOAD})
public class MinioServiceImpl extends AbstractUploadFile {

    @Autowired
    MinioUtil minioUtil;

    @Override
    public String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String newFileName) {
        return minioUtil.uploadFile(systemConfig, tempFile, newFileName);
    }

    @Override
    public String deleteFile(String fileName, SystemConfig systemConfig) {
        // 批量删除本地图片
        MoGuFileUtil.deleteFile(fileName);
        return minioUtil.deleteFile(systemConfig, fileName);
    }
}
