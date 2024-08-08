package com.moxi.mogublog.picture.service;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 上传文件接口
 *
 * @author thh
 * @date 2022年10月23日01:12:14
 */
public interface IUploadService {

    /**
     * 文件上传
     *
     * @param systemConfig
     * @param tempFile
     * @return
     * @throws IOException
     */
    String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String fileNewName) throws IOException;


    /**
     * 删除文件
     * @return
     */
    String deleteFile(String fileName, SystemConfig systemConfig);
}
