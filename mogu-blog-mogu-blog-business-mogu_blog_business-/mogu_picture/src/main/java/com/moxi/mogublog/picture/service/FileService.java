package com.moxi.mogublog.picture.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mogublog.picture.entity.QiNiuCallback.QiNiuAuditCallback;
import com.moxi.mougblog.base.service.SuperService;
import com.moxi.mougblog.base.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件服务类
 *
 * @author 陌溪
 * @since 2018-09-17
 */
public interface FileService extends SuperService<File> {

    /**
     * 截图上传
     *
     * @param multipartFile
     * @return
     */
    String cropperPicture(MultipartFile multipartFile);

    /**
     * 通过fileIds获取图片信息
     *
     * @param fileIds
     * @param code
     * @return
     */
    String getPicture(String fileIds, String code);

    /**
     * 批量文件上传
     *
     * @param request
     * @param multipartFileList
     * @param systemConfig
     * @return
     */
    String batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList, SystemConfig systemConfig);

    /**
     * 通过URL上传图片
     *
     * @param fileVO
     * @return
     */
    String uploadPictureByUrl(FileVO fileVO);

    /**
     * CKeditor图像中的图片上传
     *
     * @param request
     * @return
     */
    Object ckeditorUploadFile(HttpServletRequest request);

    /**
     * CKeditor上传 复制的图片
     *
     * @return
     */
    Object ckeditorUploadCopyFile();

    /**
     * 工具栏 “插入\编辑超链接”的文件上传
     *
     * @return
     */
    Object ckeditorUploadToolFile(HttpServletRequest request);

    /**
     * 上传视频文件
     *
     * @param request
     * @param file
     * @param systemConfig
     * @return
     */
    String uploadVideoFile(HttpServletRequest request, MultipartFile file, SystemConfig systemConfig);

    /**
     * 通过md5码获取文件
     * @param md5
     * @return
     */
    File getFileByMd5(String md5, SystemConfig systemConfig);

    /**
     * 获取文件列表
     * @param fileVO
     * @return
     */
    IPage<File> getFileList(FileVO fileVO);

    /**
     * 批量删除源文件
     * @param fileUidList
     * @return
     */
    String batchDeleteFile(List<String> fileUidList);

    /**
     * 七牛云审核文件
     * @param qiNiuAuditCallback
     * @return
     */
    String qiNiuAuditCallback(QiNiuAuditCallback qiNiuAuditCallback);

    /**
     * 生成M3U8文件
     * @param req
     * @return
     */
    File generateM3u8File(GenerateM3u8FileRequest req);


    /**
     * 获取用户最近上传的文件
     * @return
     */
    IPage<File> getUserRecentlyUploadFile(FileVO fileVO);

}
