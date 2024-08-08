package com.moxi.mogublog.picture.service.impl;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.annotation.UploadType;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.util.QiniuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 七牛云实现类
 *
 * @author 陌溪
 * @since 2020年1月20日20:05:45
 */
@Service
@RefreshScope
@Slf4j
@UploadType({UploadTypeEnum.QINIU_UPLOAD})
public class QiniuServiceImpl extends AbstractUploadFile {
    @Autowired
    QiniuUtil qiniuUtil;


    @Override
    public String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String newFileName) {
        try {
            return qiniuUtil.uploadQiniu(tempFile, systemConfig, newFileName);
        } catch (Exception e) {
            log.error("上传七牛云失败！", e);
        }
        return "";
    }

    @Override
    public String deleteFile(String fileName, SystemConfig systemConfig) {
        return qiniuUtil.deleteFile(fileName, systemConfig);
    }
}
