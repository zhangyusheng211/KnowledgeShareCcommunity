package com.moxi.mogublog.picture.service.impl;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.annotation.UploadType;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.util.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RefreshScope
@Slf4j
@UploadType({UploadTypeEnum.ALIOSS_UPLOAD})
public class AliOssServiceImpl extends AbstractUploadFile {

    @Autowired
    AliyunOssUtil aliyunOssUtil;


    @Override
    public String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String newFileName) {
        try {
            return aliyunOssUtil.uploadFile(systemConfig, tempFile, newFileName);
        } catch (IOException e) {
            log.error("阿里云上传文件失败:", e);
            throw new BusinessException("阿里云上传文件失败");
        }
    }

    @Override
    public String deleteFile(String fileName, SystemConfig systemConfig) {
        return aliyunOssUtil.deleteFile(fileName, systemConfig);
    }

}
