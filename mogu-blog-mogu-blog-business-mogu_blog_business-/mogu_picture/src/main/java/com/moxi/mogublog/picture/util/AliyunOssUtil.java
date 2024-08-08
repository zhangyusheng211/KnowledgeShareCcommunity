package com.moxi.mogublog.picture.util;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.global.MessageConf;
import com.moxi.mogublog.picture.global.SysConf;
import com.moxi.mogublog.utils.FileUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.global.Constants;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AliyunOssUtil {

    /**
     * 文件上传
     *
     * @param systemConfig 系统配置
     * @param tempFile     临时文件
     * @return
     * @throws Exception
     */
    public String uploadFile(SystemConfig systemConfig, File tempFile, String fileNewName) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(tempFile.toPath()));
        return uploadFile(systemConfig, bis, fileNewName);
    }

    /**
     * 文件上传
     *
     * @param systemConfig 系统配置
     * @param file     文件
     * @return
     * @throws Exception
     */
    public String uploadFile(SystemConfig systemConfig, MultipartFile file) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
        return uploadFile(systemConfig, bis, file.getOriginalFilename());
    }

    public String uploadFile(SystemConfig systemConfig, InputStream is, String fileName) {
        String url = "";
        // 使用AliOss服务的URL，端口，Access key和Secret key创建一个aliOssClient对象
        OSS ossClient = new OSSClient(systemConfig.getAliyunEndpoint(), systemConfig.getAliyunAccessKey(), systemConfig.getAliyunAccessKeySecret());
        try {
            //获取新文件名
            String newFileName = systemConfig.getAliyunCatalog() + Constants.SYMBOL_LEFT_OBLIQUE_LINE + fileName;
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("text/plain");
            UploadFileRequest uploadFileRequest = new UploadFileRequest(systemConfig.getAliyunBucketName(), newFileName);
            url = Constants.SYMBOL_LEFT_OBLIQUE_LINE + newFileName;
            uploadFileRequest.setUploadFile(url);
            // 指定上传并发线程数，默认值为1。
            uploadFileRequest.setTaskNum(5);
            // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
            uploadFileRequest.setPartSize(1024 * 1024);
            // 开启断点续传，默认关闭。
            uploadFileRequest.setEnableCheckpoint(true);
            //ossClient.uploadFile(uploadFileRequest);
            ossClient.putObject(systemConfig.getAliyunBucketName(), newFileName, is);
        } catch (Exception e) {
            e.getStackTrace();
            log.error("上传文件出现异常: {}", e.getMessage());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            //关闭OSSClient
            ossClient.shutdown();
        }
        return url;
    }

    /**
     * 批量文件上传
     *
     * @param list
     * @return
     * @throws Exception
     */
    public String batchUploadFile(List<MultipartFile> list, SystemConfig systemConfig) throws IOException {
        List<String> urlList = new ArrayList<>();
        for (MultipartFile file : list) {
            urlList.add(this.uploadFile(systemConfig, file.getInputStream(), file.getName()));
        }
        return ResultUtil.successWithData(urlList);
    }


    /**
     * 删除单个文件
     *
     * @param fileName
     * @return
     */
    public String deleteFile(String fileName, SystemConfig systemConfig) {
        try {
            // 获取系统配置
            OSS ossClient = new OSSClient(systemConfig.getAliyunEndpoint(), systemConfig.getAliyunAccessKey(), systemConfig.getAliyunAccessKeySecret());
            // Remove object.
            ossClient.deleteObject(systemConfig.getAliyunBucketName(), fileName);
        } catch (Exception e) {
            log.error("删除Minio中的文件失败 fileName: {}, 错误消息: {}", fileName, e.getMessage());
            return ResultUtil.errorWithData(MessageConf.DELETE_DEFAULT_ERROR);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    /**
     * 批量删除文件
     *
     * @param fileNameList
     * @param systemConfig 系统配置
     * @return
     */
    public String deleteBatchFile(SystemConfig systemConfig, List<String> fileNameList) {
        OSS ossClient = new OSSClient(systemConfig.getAliyunEndpoint(), systemConfig.getAliyunAccessKey(), systemConfig.getAliyunAccessKeySecret());
        try {
            for (String fileName : fileNameList) {
                ossClient.deleteObject(systemConfig.getAliyunBucketName(), fileName);
            }
        } catch (Exception e) {
            log.error("批量删除文件失败, 错误消息: {}", e.getMessage());
            return ResultUtil.errorWithData(MessageConf.DELETE_DEFAULT_ERROR);
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

}
