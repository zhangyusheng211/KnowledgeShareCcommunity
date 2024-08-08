package com.moxi.mogublog.commons.fallback;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片服务降级兜底方法【当服务不可用时会触发】
 *
 * @author: 陌溪
 * @create: 2020-10-03-20:54
 */
@Component
@Slf4j
public class PictureFeignFallback implements PictureFeignClient {

    @Override
    public String getPicture(String fileIds, String code) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，服务降级返回，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("获取图片服务降级返回");
    }

    @Override
    public String uploadPicsByUrl(FileVO fileVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，更新图片失败，服务降级返回，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("更新图片服务降级返回");
    }

    @Override
    public String initStorageSize(String adminUid, Long maxStorageSize) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，初始化网盘容量失败，服务降级返回，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("图片服务出现异常，初始化网盘容量失败");
    }

    @Override
    public String editStorageSize(String adminUid, Long maxStorageSize) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，更新网盘容量失败，服务降级返回，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("图片服务出现异常，更新网盘容量失败，服务降级返回");
    }

    @Override
    public String getStorageByAdminUid(List<String> adminUidList) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，获取网盘容量失败，服务降级返回，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("图片服务出现异常，获取网盘容量失败，服务降级返回");
    }

    @Override
    public String getFileList(FileVO fileVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，获取图片列表失败，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("图片服务出现异常，获取图片列表失败，服务降级返回");
    }

    @Override
    public Map<String, File> getFileListMap(FileVO fileVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，获取图片列表失败，请求路径: {}", requestURL);
        return new HashMap<>();
    }

    @Override
    public String batchDeleteFile(List<String> fileUidList) {
        HttpServletRequest request = RequestHolder.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.error("图片服务出现异常，删除素材失败，请求路径: {}", requestURL);
        return ResultUtil.errorWithMessage("图片服务出现异常，删除素材失败，服务降级返回");
    }

    @Override
    public String getFileUploadPath() {
        return null;
    }

    @Override
    public File generateM3u8File(GenerateM3u8FileRequest req) {
        return null;
    }
}
