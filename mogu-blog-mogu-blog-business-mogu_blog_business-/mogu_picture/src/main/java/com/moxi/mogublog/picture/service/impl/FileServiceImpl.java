package com.moxi.mogublog.picture.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.File;
import com.moxi.mogublog.commons.entity.FileSort;
import com.moxi.mogublog.commons.entity.SystemConfig;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mogublog.picture.entity.QiNiuCallback.Items;
import com.moxi.mogublog.picture.entity.QiNiuCallback.QiNiuAuditCallback;
import com.moxi.mogublog.picture.entity.QiNiuCallback.Result;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.factory.UploadFileFactory;
import com.moxi.mogublog.picture.global.MessageConf;
import com.moxi.mogublog.picture.global.RedisConf;
import com.moxi.mogublog.picture.global.SQLConf;
import com.moxi.mogublog.picture.global.SysConf;
import com.moxi.mogublog.picture.mapper.FileMapper;
import com.moxi.mogublog.picture.service.*;
import com.moxi.mogublog.picture.util.FeignUtil;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.utils.ffmpeg.m3u8.GenerateM3u8FileResp;
import com.moxi.mogublog.utils.ffmpeg.m3u8.M3u8VideoUtils;
import com.moxi.mogublog.utils.text.Convert;
import com.moxi.mougblog.base.enums.EFilePriority;
import com.moxi.mougblog.base.enums.EOpenStatus;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.EUserTag;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ErrorCode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import com.moxi.mougblog.base.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务实现类【上传需调用本地文件服务、七牛云文件服务、Minio文件服务】
 *
 * @author 陌溪
 * @since 2018-09-17
 */
@Slf4j
@Service
public class FileServiceImpl extends SuperServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    FileSortService fileSortService;
    @Resource
    SmsFeignClient smsFeignClient;
    @Autowired
    FeignUtil feignUtil;
    @Autowired
    private FileService fileService;
    @Autowired
    private RedisUtil redisUtil;
    @Value(value = "${file.upload.limitSize}")
    private Long LIMIT_SIZE;

    /**
     * 本地图片上传路径
     */
    @Value(value = "${file.upload.path}")
    private String path;

    // 上传图片默认压缩大小
    @Value(value = "${file.upload.scaleSize}")
    private Long scaleSize;
    // 聊天图片默认压缩大小
    @Value(value = "${file.upload.chatScaleSize}")
    private Long chatScaleSize;
    // 动态图片默认压缩大小
    @Value(value = "${file.upload.momentScaleSize}")
    private Long momentScaleSize;


    @Override
    public String cropperPicture(MultipartFile multipartFile) {
        HttpServletRequest request = RequestHolder.getRequest();
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        return uploadFileByMultipartFile(request, systemConfig, multipartFile);
    }

    @Override
    public String getPicture(String fileIds, String code) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isEmpty(code)) {
            code = Constants.SYMBOL_COMMA;
        }
        // 文件id为空，直接返回
        if (StringUtils.isEmpty(fileIds)) {
            return ResultUtil.successWithData(list);
        }

        List<String> changeStringToString = StringUtils.changeStringToString(fileIds, code);
        QueryWrapper<com.moxi.mogublog.commons.entity.File> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(SQLConf.UID, changeStringToString);
        List<com.moxi.mogublog.commons.entity.File> fileList = fileService.list(queryWrapper);
        if (fileList.size() > 0) {
            for (com.moxi.mogublog.commons.entity.File file : fileList) {
                if (file != null) {
                    Map<String, Object> remap = new HashMap<>();
                    // 获取七牛云地址
                    remap.put(SysConf.QI_NIU_URL, file.getQiNiuUrl());
                    // 获取Minio对象存储地址
                    remap.put(SysConf.MINIO_URL, file.getMinioUrl());
                    //获取阿里OSS对象存储地址
                    remap.put(SysConf.ALIOSS_URL, file.getAliOssUrl());
                    // 获取本地地址
                    remap.put(SysConf.URL, file.getPicUrl());
                    // 获取m3u8文件
                    remap.put(SysConf.M3U8_URL, file.getM3u8Url());
                    // 后缀名，也就是类型
                    remap.put(SysConf.EXPANDED_NAME, file.getPicExpandedName());
                    remap.put(SysConf.FILE_OLD_NAME, file.getFileOldName());
                    //名称
                    remap.put(SysConf.NAME, file.getPicName());
                    remap.put(SysConf.UID, file.getUid());
                    remap.put(SQLConf.FILE_OLD_NAME, file.getFileOldName());
                    list.add(remap);
                }
            }
        }
        return ResultUtil.result(SysConf.SUCCESS, list);
    }

    @Override
    public String uploadVideoFile(HttpServletRequest request, MultipartFile multipartFile, SystemConfig systemConfig) {
        return batchUploadFile(request, Lists.newArrayList(multipartFile), systemConfig);
    }

    @Override
    public File getFileByMd5(String md5, SystemConfig systemConfig) {
        // 未获取到m5d，直接返回空
        if (StringUtils.isEmpty(md5) || systemConfig == null) {
            return null;
        }
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.FILE_MD5, md5);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        List<File> fileList = fileService.list(queryWrapper);
        // 如果有多少，只取第一个
        if (fileList.size() > 0) {
            File file = fileList.get(0);
            // 判断历史的文件，是否都满足上传要求
            // 1. 开启了上传本地，但是本地没有图片
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal()) && StringUtils.isEmpty(file.getPicUrl())) {
                return null;
            }
            // 2. 开启了上传七牛云，但是七牛云没有图片
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu()) && StringUtils.isEmpty(file.getQiNiuUrl())) {
                return null;
            }
            // 3. 开启了上传阿里云，但是阿里云没有图片
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadAlioss()) && StringUtils.isEmpty(file.getAliOssUrl())) {
                return null;
            }
            // 4. 开启了上传minio，但是minio没有图片
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadMinio()) && StringUtils.isEmpty(file.getMinioUrl())) {
                return null;
            }
            // 根据图片优先级设置图片
            setFilePriority(file, systemConfig);
            log.info("【命中重复的文件】md5:{},fileUid: {}, url:{}", md5, file.getUid(), file.getPictureUrl());
            // 都满足条件，那么返回已存在的图片
            return file;
        }
        return null;
    }

    @Override
    public String batchUploadFile(HttpServletRequest request, List<MultipartFile> filedatas, SystemConfig systemConfig) {
        //如果是用户上传，则包含用户uid
        String userUid = Convert.toStr(request.getAttribute(SysConf.USER_UID), BaseSysConf.DEFAULT_UID);
        //如果是管理员上传，则包含管理员uid
        String adminUid = Convert.toStr(request.getAttribute(SysConf.ADMIN_UID), BaseSysConf.DEFAULT_UID);
        //项目名
        String projectName = Convert.toStr(request.getParameter(SysConf.PROJECT_NAME), "blog");
        //模块名
        String sortName = Convert.toStr(request.getParameter(SysConf.SORT_NAME), "admin");
        // 校验用户上传权限
        verifyUpload(userUid, adminUid);

        // 获取文件准入Sort，用于划分不同模块
        FileSort fileSort = getFileSort(sortName, projectName);
        if (fileSort == null) {
            log.error("图片数据库file_sort表未匹配数据, sortName: {}, projectName: {}", sortName, projectName);
            return ResultUtil.errorWithMessage("文件不被允许上传");
        }

        List<File> lists = new ArrayList<>();
        //文件上传
        if (!CollectionUtils.isEmpty(filedatas)) {
            java.io.File tempFile = null;
            long start = System.currentTimeMillis();
            for (MultipartFile filedata : filedatas) {
                try {
                    String oldName = filedata.getOriginalFilename();
                    //获取新文件名
                    String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + FileUtils.getPicExpandedName(oldName);
                    // 写临时文件
                    tempFile = writTempFile(filedata);
                    // 上传文件
                    File file = uploadFile(systemConfig, oldName, newFileName, tempFile, fileSort);
                    lists.add(file);
                } catch (Exception e) {
                    log.error("上传文件异常: {}", e.getMessage());
                    e.printStackTrace();
                    return ResultUtil.errorWithMessage(e.getMessage());
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }
            log.info("本次上传文件总耗时： " + (System.currentTimeMillis() - start));
            //保存成功返回数据
            return ResultUtil.successWithData(lists);
        }
        return ResultUtil.errorWithMessage("请上传图片");
    }

    private java.io.File writTempFile(MultipartFile filedata) throws IOException {
        String uploadTempDir = path + "temp/";
        java.io.File tempFile = FileUtils.getAbsoluteFile(uploadTempDir, IdUtil.fastSimpleUUID() + Constants.SYMBOL_POINT + FileUtils.getFileSuffix(filedata.getOriginalFilename()));
        String tempPath = tempFile.getAbsolutePath();
        filedata.transferTo(Paths.get(tempPath));
        return FileUtil.file(tempPath);
    }

    private FileSort getFileSort(String sortName, String projectName) {
        QueryWrapper<FileSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.SORT_NAME, sortName);
        queryWrapper.eq(SQLConf.PROJECT_NAME, projectName);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        return fileSortService.getOne(queryWrapper, false);
    }

    private File uploadFile(SystemConfig systemConfig, String oldFileName, String newfileName, java.io.File tempFile, FileSort fileSort) throws IOException {
        // checkMd5
        String fileMd5 = FileUtils.getFileMd5(org.apache.commons.io.FileUtils.readFileToByteArray(tempFile));
        File duplicateFile = this.getFileByMd5(fileMd5, systemConfig);
        // 文件重复
        if (duplicateFile != null) {
            return duplicateFile;
        }
        // 获取文件大小
        long size = tempFile.length();
        // 判断上传的文件，是否每日上传限制 【仅针对WEB端用户上传】
        uploadFileVerify(systemConfig, size, oldFileName);
        // 获取文件临时路径
        String tempPath = tempFile.getAbsoluteFile().getAbsolutePath();
        // 判断上传的格式是否合法
        boolean isVideo = FileUtils.isVideo(newfileName);
        boolean isPic = FileUtils.isPic(newfileName);
        boolean isSafeFile = FileUtils.isSafeFile(newfileName);
        boolean isMiniFile = FileUtils.isMiniFile(newfileName);
        if (!isSafeFile) {
            log.error("[uploadFile] 后台暂不不支持上传该文件, newfileName: {}", newfileName);
            throw new BusinessException("后台暂不不支持上传该文件");
        }
        //单图片大小进行限制
        if (isPic && size > (15 * 1024 * 1024)) {
            log.error("[uploadFile] 图片大小不能超过15M: size: {}", size);
            throw new BusinessException("图片大小不能超过15M");
        }

        // 对单个视频文件进行限制
        if (isVideo && !(EOpenStatus.OPEN.equals(systemConfig.getUploadLocal()) || EOpenStatus.OPEN.equals(systemConfig.getUploadMinio()))) {
            log.error("视频文件仅支持上传本地和MiniIO，请到系统设置进行修改。 fileName: {}", oldFileName);
            throw new BusinessException("视频文件仅支持上传本地和Minio，请到系统设置进行修改");
        }

        // 校验图片是否需要压缩,对临时文件做压缩操作
        if (isPic && checkZip(fileSort, tempFile.length())) {
            // 压缩一半
            ImgUtil.scale(
                    FileUtil.file(tempPath),
                    FileUtil.file(tempPath),
                    0.5f
            );
        }
        File file = new File();
        // 判断是否能够上传至本地
        if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
            file.setPicUrl(UploadFileFactory.getUploadService(UploadTypeEnum.LOCAL_UPLOAD).uploadFile(systemConfig, tempFile, fileSort, newfileName));
        }

        // 判断是否能够上传Minio文件服务器
        if (EOpenStatus.OPEN.equals(systemConfig.getUploadMinio())) {
            file.setMinioUrl(UploadFileFactory.getUploadService(UploadTypeEnum.MINIO_UPLOAD).uploadFile(systemConfig, tempFile, fileSort, newfileName));
        }

        // 上传七牛云，判断是否能够上传七牛云
        if (isMiniFile && EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu())) {
            file.setQiNiuUrl(UploadFileFactory.getUploadService(UploadTypeEnum.QINIU_UPLOAD).uploadFile(systemConfig, tempFile, fileSort, newfileName));
        }

        //判断能否上传阿里Oss
        if (isMiniFile && EOpenStatus.OPEN.equals(systemConfig.getUploadAlioss())) {
            file.setAliOssUrl(UploadFileFactory.getUploadService(UploadTypeEnum.ALIOSS_UPLOAD).uploadFile(systemConfig, tempFile, fileSort, newfileName));
        }

        // 设置图片显示的优先级，上游无需感知对应的图片优先级了
        String picturePriority = systemConfig.getPicturePriority();
        switch (picturePriority) {
            case EFilePriority.QI_NIU: {
                file.setPictureUrl(file.getQiNiuUrl());
            } break;
            case EFilePriority.MINIO: {
                file.setPictureUrl(file.getMinioUrl());
            } break;
            case EFilePriority.ALIOSS: {
                file.setPictureUrl(file.getAliOssUrl());
            } break;
            default: {
                file.setPictureUrl(file.getPictureUrl());
            }
        }

        file.setCreateTime(new Date());
        file.setFileSortUid(fileSort.getUid());
        file.setFileOldName(oldFileName);
        file.setFileSize(tempFile.length());
        file.setPicExpandedName(FileUtils.getPicExpandedName(newfileName));
        file.setPicName(newfileName);
        file.setStatus(EStatus.ENABLE);
        file.setUserUid(RequestHolder.getUserUid() != null ? RequestHolder.getUserUid() : ServletUtils.getRequest().getParameter(SysConf.USER_UID));
        file.setAdminUid(RequestHolder.getUserUid() != null ? RequestHolder.getAdminUid() : ServletUtils.getRequest().getParameter(SysConf.ADMIN_UID));
        file.setFileMd5(fileMd5);
        this.save(file);
        setFilePriority(file, systemConfig);
        return file;
    }


    /**
     * 校验是否要压缩图片
     *
     * @param fileSort
     * @param size
     * @return boolean
     */
    private boolean checkZip(FileSort fileSort, long size) {
        // 判断文件大小是否大于给定的阈值【聊天的图片、动态图片】
        boolean isZip = false;
        if (SysConf.CHAT.equals(fileSort.getSortName()) && size > chatScaleSize) {
            isZip = true;
        } else if (SysConf.MOMENT.equals(fileSort.getSortName()) && size > momentScaleSize) {
            isZip = true;
        } else if (size > scaleSize) {
            isZip = true;
        }
        return isZip;
    }

    private void verifyUpload(String userUid, String adminUid) {
        //TODO 检测用户上传，如果不是网站的用户就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            throw new BusinessException("请先注册");
        }
        // 判断用户是否被封禁
        String userActiveStatus = redisUtil.get(RedisConf.USER_ACTIVE_STATUS + Constants.SYMBOL_COLON + userUid);
        if (Constants.STR_ZERO.equals(userActiveStatus)) {
            throw new BusinessException("你无权限进行该操作");
        }
    }

    /**
     * 上传网络图片
     *
     * @param systemConfig
     * @param fileSort
     * @param url
     * @return com.moxi.mogublog.commons.entity.File
     */
    private File uploadFileByUrl(SystemConfig systemConfig, FileSort fileSort, String url) {
        java.io.File tempFile = null;
        try {
            String oldName = FileUtil.getName(url);
            //获取扩展名，默认是jpg
            String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + "jpg";
            String uploadTempDir = path + "temp/";
            String tempFilePath = uploadTempDir + newFileName;
            HttpUtil.downloadFile(url, tempFilePath);
            tempFile = FileUtil.file(tempFilePath);
            return uploadFile(systemConfig, oldName, newFileName, tempFile, fileSort);
        } catch (IOException e) {
            log.error("上传文件失败，请检查配置", e);
            throw new BusinessException("上传文件失败，请检查配置");
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public String uploadPictureByUrl(FileVO fileVO) {
        // 获取配置文件
        SystemConfig systemConfig;
        if (fileVO.getSystemConfig() != null) {
            Map<String, String> resultMap = fileVO.getSystemConfig();
            systemConfig = feignUtil.getSystemConfigByMap(resultMap);
        } else {
            // 从Redis中获取七牛云配置文件
            systemConfig = feignUtil.getSystemConfig();
        }
        String userUid = fileVO.getUserUid();
        String adminUid = fileVO.getAdminUid();
        String projectName = fileVO.getProjectName();
        String sortName = fileVO.getSortName();
        List<String> urlList = fileVO.getUrlList();
        //projectName现在默认base
        if (StringUtils.isEmpty(projectName)) {
            projectName = "base";
        }
        //这里可以检测用户上传，如果不是网站的用户或会员就不能调用
        if (StringUtils.isEmpty(userUid) && StringUtils.isEmpty(adminUid)) {
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, "请先注册");
        }
        FileSort fileSort = getFileSort(sortName, projectName);
        if (fileSort == null) {
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, "文件不被允许上传, 请填写文件分类信息");
        }
        List<com.moxi.mogublog.commons.entity.File> lists = new ArrayList<>();
        //文件上传
        if (urlList != null && urlList.size() > 0) {
            for (String url : urlList) {
                lists.add(uploadFileByUrl(systemConfig, fileSort, url));
            }
            //保存成功返回数据
            return ResultUtil.result(SysConf.SUCCESS, lists);
        }
        return ResultUtil.errorWithMessage("请上传图片");
    }

    @Override
    public Object ckeditorUploadFile(HttpServletRequest request) {
        // 从Redis中获取七牛云配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        // 设置图片上传服务必要的信息
        String userUid = RequestHolder.getUserUid();
        String adminUid = RequestHolder.getAdminUid();
        request.setAttribute(SysConf.USER_UID, userUid);
        request.setAttribute(SysConf.ADMIN_UID, adminUid);
        request.setAttribute(SysConf.PROJECT_NAME, SysConf.BLOG);
        request.setAttribute(SysConf.SORT_NAME, SysConf.ADMIN);

        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        List<MultipartFile> multipartFileList = new ArrayList<>();
        MultipartFile multipartFile = null;
        while (iter.hasNext()) {
            // 只取一个
            multipartFile = multiRequest.getFile(iter.next());
            break;

        }
        return uploadFileByMultipartFile(request, systemConfig, multipartFile);
    }

    @Override
    public Object ckeditorUploadCopyFile() {
        HttpServletRequest request = RequestHolder.getRequest();
        // 从参数中获取token【该方法用于ckeditor复制图片上传，所以会携带token在参数中】
        String token = request.getParameter(SysConf.TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, "未读取到携带token");
        }

        String[] params = token.split("\\?url=");
        request.setAttribute(SysConf.TOKEN, params[0]);

        SystemConfig systemConfig = feignUtil.getSystemConfig();
        String projectName = "blog";
        String sortName = "admin";
        // 需要上传的URL
        String itemUrl = params[1];

        // 判断需要上传的域名和本机图片域名是否一致
        if (EFilePriority.QI_NIU.equals(systemConfig.getContentPicturePriority())) {
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getQiNiuPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) &&
                    itemUrl.contains(systemConfig.getQiNiuPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        } else if (EFilePriority.MINIO.equals(systemConfig.getContentPicturePriority())) {
            // 表示优先显示Minio对象存储
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getMinioPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) &&
                    itemUrl.contains(systemConfig.getMinioPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        } else if (EFilePriority.ALIOSS.equals(systemConfig.getContentPicturePriority())) {
            // 表示优先显示阿里oss对象存储
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getAliossPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) &&
                    itemUrl.contains(systemConfig.getAliossPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        } else {
            // 表示优先显示本地服务器
            // 判断需要上传的域名和本机图片域名是否一致，如果一致，那么就不需要重新上传，而是直接返回
            if (StringUtils.isNotEmpty(systemConfig.getLocalPictureBaseUrl()) && StringUtils.isNotEmpty(itemUrl) &&
                    itemUrl.contains(systemConfig.getLocalPictureBaseUrl())) {
                Map<String, Object> result = new HashMap<>();
                result.put(SysConf.UPLOADED, 1);
                result.put(SysConf.FILE_NAME, itemUrl);
                result.put(SysConf.URL, itemUrl);
                return result;
            }
        }
        FileSort fileSort = getFileSort(sortName, projectName);
        return ckeditorUploadCopyFile(systemConfig, fileSort, itemUrl);
    }

    private Map<String, Object> ckeditorUploadCopyFile(SystemConfig systemConfig, FileSort fileSort, String itemUrl) {
        Map<String, Object> result = new HashMap<>();
        File file = uploadFileByUrl(systemConfig, fileSort, itemUrl);
        result.put(SysConf.UPLOADED, 1);
        result.put(SysConf.FILE_NAME, file.getPicName());
        // 设置显示方式
        result.put(SysConf.URL, file.getPictureUrl());
        return result;
    }

    private String uploadFileByMultipartFile(HttpServletRequest request, SystemConfig systemConfig, MultipartFile multipartFile) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        try {
            String result = this.batchUploadFile(request, Lists.newArrayList(multipartFile), systemConfig);
            Map picMap = JSON.parseObject(result, Map.class);
            if (SysConf.SUCCESS.equals(picMap.get(SysConf.CODE))) {
                String listStr = MapUtils.getString(picMap, SysConf.DATA);
                List<File> files = JSON.parseArray(listStr, File.class);
                if (files.size() > 0) {
                    File file = files.get(0);
                    map.put(SysConf.UPLOADED, 1);
                    map.put(SysConf.UID, file.getUid());
                    map.put(SysConf.FILE_NAME, file.getPicName());
                    // 设置显示方式
                    map.put(SysConf.URL, file.getPictureUrl());
                    map.put(SysConf.DATA, null);
                }
            } else {
                log.error("[uploadFileByMultipartFile] 上传文件失败, err: {}", picMap.get(SysConf.MESSAGE));
                map.put(SysConf.UPLOADED, 0);
                errorMap.put(SysConf.MESSAGE, picMap.get(SysConf.MESSAGE));
                map.put(SysConf.ERROR, errorMap);
            }
        } catch (Exception e) {
            log.error("[uploadFileByMultipartFile] 上传文件失败, err: {}", e.getMessage());
            map.put(SysConf.UPLOADED, 0);
            errorMap.put(SysConf.MESSAGE, e.getMessage());
            map.put(SysConf.ERROR, errorMap);
        }
        return JsonUtils.objectToJson(map);
    }


    @Override
    public Object ckeditorUploadToolFile(HttpServletRequest request) {
        String token = request.getParameter(SysConf.TOKEN);
        // 从Redis中获取系统配置【需要传入token】
        Map<String, String> qiNiuResultMap = feignUtil.getSystemConfigMap(token);
        SystemConfig systemConfig = feignUtil.getSystemConfigByMap(qiNiuResultMap);
        // 设置图片上传服务必要的信息
        request.setAttribute(SysConf.USER_UID, SysConf.DEFAULT_UID);
        request.setAttribute(SysConf.ADMIN_UID, SysConf.DEFAULT_UID);
        request.setAttribute(SysConf.PROJECT_NAME, SysConf.BLOG);
        request.setAttribute(SysConf.SORT_NAME, SysConf.ADMIN);
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        MultipartFile multipartFile = null;
        while (iter.hasNext()) {
            multipartFile = multiRequest.getFile(iter.next());
            break;
        }
        return uploadFileByMultipartFile(request, systemConfig, multipartFile);
    }

    @Override
    public IPage<File> getFileList(FileVO fileVO) {
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(fileVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, fileVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(fileVO.getSortUid())) {
            queryWrapper.eq(SQLConf.SORT_UID, fileVO.getSortUid());
        }
        if (StringUtils.isNotEmpty(fileVO.getUid())) {
            queryWrapper.eq(SQLConf.UID, fileVO.getUid());
        }
        if (StringUtils.isNotEmpty(fileVO.getUid())) {
            queryWrapper.eq(SQLConf.UID, fileVO.getUid());
        }
        if (fileVO.getFileUidList() != null && fileVO.getFileUidList().size() > 0) {
            queryWrapper.in(SQLConf.UID, fileVO.getFileUidList());
        }
        Page<File> page = new Page<>();
        page.setCurrent(fileVO.getCurrentPage());
        page.setSize(fileVO.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<File> pageIPage = fileService.page(page, queryWrapper);
        List<File> fileList = pageIPage.getRecords();

        Map<String, User> userMap = new HashMap<>();
        if (fileVO.isShowUser()) {
            List<String> userUidList = new ArrayList<>();
            fileList.forEach(item -> {
                userUidList.add(item.getUserUid());
            });
            if (userUidList.size() > 0) {
                UserVO userVO = new UserVO();
                userVO.setUserUidList(userUidList);
                userVO.setCurrentPage(1L);
                userVO.setPageSize(100L);
                List<User> userList = smsFeignClient.getUserListByPage(userVO);
                userList.forEach(item -> {
                    userMap.put(item.getUid(), item);
                });
            }
        }

        // 设置图片展示的优先级
        for (File file : fileList) {
            file.setUser(userMap.get(file.getUserUid()));
            // 按优先级设置图片
            setFilePriority(file, systemConfig);
        }
        pageIPage.setRecords(fileList);


        return pageIPage;
    }

    @Override
    public String batchDeleteFile(List<String> fileUidList) {
        if (fileUidList.size() == 0 || fileUidList.size() > 100) {
            return ResultUtil.errorWithMessage("参数异常");
        }
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        // 查询出File信息
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.in(SQLConf.UID, fileUidList);
        List<File> fileList = fileService.list(queryWrapper);
        for (File file: fileList) {
            if (file.getStatus() != EStatus.ENABLE) {
                log.error("[batchDeleteFile] 该文件已被删除，跳过; fileUid: {}", file.getUid());
                continue;
            }
            // 判断是否能够上传至本地
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
                UploadFileFactory.getUploadService(UploadTypeEnum.LOCAL_UPLOAD).deleteFile(path + file.getPicUrl(), systemConfig);
            }
            // 上传七牛云，判断是否能够上传七牛云
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadQiNiu())) {
                UploadFileFactory.getUploadService(UploadTypeEnum.QINIU_UPLOAD).deleteFile(file.getQiNiuUrl(), systemConfig);
            }

            // 判断是否能够上传Minio文件服务器
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadMinio())) {
                UploadFileFactory.getUploadService(UploadTypeEnum.MINIO_UPLOAD).deleteFile(file.getMinioUrl(), systemConfig);
            }
            //判断能否上传阿里Oss
            if (EOpenStatus.OPEN.equals(systemConfig.getUploadAlioss())) {
                UploadFileFactory.getUploadService(UploadTypeEnum.ALIOSS_UPLOAD).deleteFile(file.getAliOssUrl(), systemConfig);
            }
            // TODO 后续可以考虑放到回收站中
            file.setStatus(EStatus.DISABLED);
        }
        // 批量删除
        fileService.updateBatchById(fileList);
        return ResultUtil.successWithMessage("删除成功");
    }

    @Override
    public String qiNiuAuditCallback(QiNiuAuditCallback qiNiuAuditCallback) {
        // 获取审核文件名
        if (qiNiuAuditCallback.getCode() != 0) {
            log.error("[qiNiuAuditCallback] 七牛云审核回调失败");
            return null;
        }
        List<Items> resultItemList = qiNiuAuditCallback.getItems();
        if (resultItemList == null || resultItemList.size() == 0) {
            log.error("[qiNiuAuditCallback] 七牛云审核回调失败");
            return ResultUtil.successWithMessage("七牛云审核回调失败");
        }
        // 获取第一个结果
        Items item = resultItemList.get(0);
        if (item.getCode() != 0 ) {
            log.error("[qiNiuAuditCallback] 七牛云审核回调失败");
            return ResultUtil.successWithMessage("七牛云审核回调失败");
        }
        Result result = item.getResult();
        boolean disable = result.isDisable();
        if (!disable) {
            // 生效中，不需要处理
            log.info("[qiNiuAuditCallback] 审核结果为生效，不需要做处理");
            return ResultUtil.successWithMessage("审核结果为生效，不需要做处理");
        }

        // 获取审核回调文件名
        String fileName = qiNiuAuditCallback.getInputKey();
        // 根据文件名，获取文件
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.QI_NIU_URL, fileName);
        queryWrapper.last(SysConf.LIMIT_ONE);
        File file = fileService.getOne(queryWrapper);
        if (file != null) {
            // 根据 fileSortUid 判断文件类型
            FileSort fileSort = fileSortService.getById(file.getFileSortUid());
            // 目前只处理聊天和动态的图片
            if (SysConf.CHAT.equals(fileSort.getSortName()) || SysConf.MOMENT.equals(fileSort.getSortName())) {
                log.info("[qiNiuAuditCallback] 命中聊天或动态图片，开始进行删除, fileUid: {}", file.getUid());
                batchDeleteFile(Lists.newArrayList(file.getUid()));
                log.info("[qiNiuAuditCallback] 处理成功");
            } else {
                log.info("[qiNiuAuditCallback] 非聊天和动态图片，暂不处理！");
            }
        } else {
            log.error("[qiNiuAuditCallback] 文件已被删除， qiNiuFileName: {}", fileName);
        }
        return ResultUtil.successWithMessage("审核结果处理完成");
    }

    @Override
    public File generateM3u8File(GenerateM3u8FileRequest req) {
        // 获取文件地址
        String uid = req.getFileUid();
        File file = fileService.getById(uid);
        if (!"mp4".equals(file.getPicExpandedName())) {
            log.error("[generateM3u8File] 只支持格式化Mp4文件, fileUid: {}", uid);
            throw new BusinessException("只支持格式化Mp4文件");
        }
        if (StringUtils.isNotEmpty(file.getM3u8Url())) {
            return file;
        }
        String localFileUrl = file.getPicUrl();
        if (StringUtils.isEmpty(localFileUrl)) {
            log.error("[generateM3u8File] 只支持本地文件进行转换, fileUid: {}", uid);
            throw new BusinessException("只支持本地文件进行转换");
        }

        // 源文件所在路径
        String sourcePath = path + localFileUrl;
        // 目标文件所在路径
        String targetPath = path + "/m3u8/" + req.getMediaUid() +"/" + req.getVideoUid() +"/";
        GenerateM3u8FileResp resp = M3u8VideoUtils.generateM3u8File(path, sourcePath, targetPath);
        if (resp == null) {
            log.error("[generateM3u8File] 转换m3u8失败, fileUid: {}", uid);
            throw new BusinessException("转换m3u8失败");
        }
        file.setM3u8Url(resp.getM3u8Url());
        file.setVideoLength(resp.getVideoLength());
        file.updateById();

        return file;
    }

    @Override
    public IPage<File> getUserRecentlyUploadFile(FileVO fileVO) {
        // 获取当前登录的用户ID
        HttpServletRequest request = RequestHolder.getRequest();
        String userUid = Convert.toStr(request.getAttribute(SysConf.USER_UID), BaseSysConf.DEFAULT_UID);
        fileVO.setUserUid(userUid);
        IPage<File> iPage = fileService.getFileList(fileVO);
        return iPage;
    }


    /**
     * 上传校验
     *
     * @param systemConfig
     * @param size
     * @return
     */
    private void uploadFileVerify(SystemConfig systemConfig, Long size, String fileName) {
        // 判断上传的文件，是否达到每日上传限制 【仅针对WEB端用户上传】
        if (SysConf.WEB.equals(systemConfig.getPlatform())) {
            if (size > LIMIT_SIZE) {
                throw new BusinessException("上传图片失败, 文件大小超过今日最大上传数");
            }
            String userInfo = redisUtil.get(RedisConf.USER_TOKEN + Constants.SYMBOL_COLON + systemConfig.getToken());
            if (StringUtils.isNotEmpty(userInfo)) {
                Map<String, Object> map = JsonUtils.jsonToMap(userInfo);
                String uid = map.get(SysConf.UID).toString();
                double userTag = map.get(SysConf.USER_TAG) == null? 0: (Double) map.get(SysConf.USER_TAG);
                long currentLimitSize = LIMIT_SIZE;
                // 如果是特权用户，上传图片限制*2
                if (userTag > EUserTag.NORMAL_USER.getValue()) {
                    currentLimitSize = 2 * currentLimitSize;
                } else {
                    // 拉取到用户的创建时间【刚注册的用户，不具备图片上传功能】【特权用户无限制】
                    String createTimeStr = map.get(SysConf.CREATE_TIME).toString();
                    Date userCreateTime = DateUtils.strToDateTime(createTimeStr);
                    Date now = new Date();
                    String newUserLimitTimeStr = redisUtil.get(RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + RedisConf.NEW_USER_LIMIT_TIME);
                    // 默认为0
                    long newUserLimitTime = 0L;
                    if (StringUtils.isNotEmpty(newUserLimitTimeStr)) {
                        newUserLimitTime = Integer.parseInt(newUserLimitTimeStr);
                    }
                    if (now.getTime() - userCreateTime.getTime() < newUserLimitTime * 60 * 60 * 1000) {
                        throw new BusinessException("注册时间小于" + newUserLimitTime + "小时，无法使用该功能");
                    }
                }

                String fileUploadLimitSizeStr = redisUtil.get(RedisConf.FILE_UPLOAD_LIMIT_SIZE + Constants.SYMBOL_COLON + uid);
                if (StringUtils.isNotEmpty(fileUploadLimitSizeStr)) {
                    Long fileUploadLimitSize = Long.parseLong(fileUploadLimitSizeStr);
                    Long uploadLimitSize = fileUploadLimitSize + size;
                    if (fileUploadLimitSize + size > currentLimitSize) {
                        throw new BusinessException("上传图片失败，您已超过今日最大上传数");
                    } else {
                        // 设置过期时间1天
                        redisUtil.setEx(RedisConf.FILE_UPLOAD_LIMIT_SIZE + Constants.SYMBOL_COLON + uid, JsonUtils.objectToJson(uploadLimitSize), 1, TimeUnit.DAYS);
                    }
                } else {
                    // 设置过期时间1天
                    redisUtil.setEx(RedisConf.FILE_UPLOAD_LIMIT_SIZE + Constants.SYMBOL_COLON + uid, String.valueOf(size), 1, TimeUnit.DAYS);
                }
            } else {
                throw new BusinessException("上传图片失败，无法获取到用户信息");
            }
        }
    }

    /**
     * 根据图片优先级，设置图片
     *
     * @param file
     * @param systemConfig
     */
    private void setFilePriority(File file, SystemConfig systemConfig) {
        if (file == null || systemConfig == null) {
            return;
        }
        String picturePriority = systemConfig.getPicturePriority();
        if (EFilePriority.QI_NIU.equals(picturePriority)) {
            file.setPictureUrl(systemConfig.getQiNiuPictureBaseUrl() + file.getQiNiuUrl());
        } else if (EFilePriority.MINIO.equals(picturePriority)) {
            file.setPictureUrl(systemConfig.getMinioPictureBaseUrl() + file.getMinioUrl());
        } else if (EFilePriority.LOCAL.equals(picturePriority)) {
            file.setPictureUrl(systemConfig.getLocalPictureBaseUrl() + file.getPicUrl());
        } else if (EFilePriority.ALIOSS.equals(picturePriority)) {
            file.setPictureUrl(systemConfig.getAliossPictureBaseUrl() + file.getAliOssUrl());
        }
    }
}


