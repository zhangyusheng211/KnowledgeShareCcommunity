package com.moxi.mogublog.picture.service.impl;

import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.picture.annotation.UploadType;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.util.MoGuFileUtil;
import com.moxi.mogublog.utils.DateUtils;
import com.moxi.mogublog.utils.FileUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * 本地文件系统实现类【通过IO流存储到本地】
 *
 * @author 陌溪
 * @since 2020年10月19日16:58:38
 */
@Slf4j
@RefreshScope
@Service
@UploadType({UploadTypeEnum.LOCAL_UPLOAD})
public class LocalFileServiceImpl extends AbstractUploadFile {
    @Override
    public String uploadFile(SystemConfig systemConfig, File tempFile, FileSort fileSort, String newFileName) throws IOException {
        String sortUrl = org.apache.commons.lang3.StringUtils.defaultIfEmpty(fileSort.getUrl(), "base/common/");
        //获取新文件名 【根据扩展名保存】
        String uploadDir = path + sortUrl + "/" + FileUtils.getPicExpandedName(newFileName) +"/" + DateUtils.getYears() + "/"
                + DateUtils.getMonth() + "/" + DateUtils.getDay() + "/";
        // 保存的路径
        String saveUrl = FileUtils.getAbsoluteFile(uploadDir, newFileName).getAbsolutePath();
        FileUtils.uploadFile(tempFile, saveUrl);

        // 返回文件相对路径
        return "/" + StringUtils.substring(uploadDir, path.length() + 1) + newFileName;
    }

    @Override
    public String deleteFile(String fileName, SystemConfig systemConfig) {
        boolean isDelete = MoGuFileUtil.deleteFile(fileName);
        if (isDelete) {
            log.info("[deleteFile] 本地文件 {} 删除成功", fileName);
            return ResultUtil.successWithMessage("删除成功");
        } else {
            log.error("[deleteFile] 本地文件 {} 删除失败", fileName);
            return ResultUtil.errorWithMessage("删除失败");
        }
    }
}
