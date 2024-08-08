package com.moxi.mogublog.xo.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PictureFeignClient;
import com.moxi.mogublog.commons.schema.GenerateM3u8FileRequest;
import com.moxi.mogublog.commons.schema.TransformMediaRequest;
import com.moxi.mogublog.commons.vo.MediaInfoVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.utils.ffmpeg.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.MediaInfoActorMapper;
import com.moxi.mogublog.xo.mapper.MediaInfoMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.support.FeignUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 媒资信息Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
@Slf4j
public class MediaInfoServiceImpl extends SuperServiceImpl<MediaInfoMapper, MediaInfo> implements MediaInfoService {
    @Resource
    private MediaInfoService mediaInfoService;
    @Resource
    private UserService userService;
    @Resource
    private MediaVideoService mediaVideoService;
    @Resource
    private MediaInfoActorMapper mediaInfoActorMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private DomainEventUtil domainEventUtil;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private AdminService adminService;

    @Override
    public IPage<MediaInfo> getPageList(MediaInfoVO mediaInfoVO) {
        QueryWrapper<MediaInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mediaInfoVO.getKeyword())) {
            queryWrapper.like(SQLConf.TITLE, mediaInfoVO.getKeyword());
        }
        if (StringUtils.isNotEmpty(mediaInfoVO.getTitle())) {
            queryWrapper.like(SQLConf.TITLE, mediaInfoVO.getTitle());
        }
        if (StringUtils.isNotEmpty(mediaInfoVO.getCategoryUid())) {
            queryWrapper.eq("category_uid", mediaInfoVO.getCategoryUid());
        }

        if (mediaInfoVO.getStatus() != null) {
            queryWrapper.eq(SQLConf.STATUS, mediaInfoVO.getStatus());
        }

        if (StringUtils.isNotEmpty(mediaInfoVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(mediaInfoVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(mediaInfoVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(mediaInfoVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        Page<MediaInfo> page = new Page<>();
        page.setCurrent(mediaInfoVO.getCurrentPage());
        page.setSize(mediaInfoVO.getPageSize());
        IPage<MediaInfo> pageList = mediaInfoService.page(page, queryWrapper);
        List<MediaInfo> mediaInfoList = pageList.getRecords();

        List<String> publishByUid = new ArrayList<>();
        mediaInfoList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getPublishByUid())) {
                publishByUid.add(item.getPublishByUid());
            }
        });
        List<User> publishUserList = userService.getUserListByIds(publishByUid);
        Map<String, User> map = new HashMap<>();
        publishUserList.forEach(item -> {
            item.setPassWord("");
            map.put(item.getUid(), item);
        });
        pageList.setRecords(mediaInfoList);
        return pageList;
    }

    @Override
    @Transactional
    public String addMediaInfo(MediaInfoVO mediaInfoVO) {
        if (StringUtils.isEmpty(RequestHolder.getAdminUid())) {
            ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
        String userUid = adminService.getAdminUserUid(RequestHolder.getAdminUid());
        if (StringUtils.isEmpty(userUid)) {
            ResultUtil.errorWithMessage(MessageConf.NOT_SET_ADMIN_USER_UID);
        }
        MediaInfo mediaInfo = new MediaInfo();
        BeanUtils.copyProperties(mediaInfoVO, mediaInfo);
        mediaInfo.setCreateByUid(userUid);
        mediaInfo.setUpdateByUid(userUid);
        mediaInfo.setPublishByUid(userUid);
        mediaInfo.setCreateTime(new Date());
        boolean insertFlag = mediaInfo.insert();
        if (insertFlag) {
            this.insertVideo(mediaInfoVO);
            this.insertActor(mediaInfoVO, EMediaActorType.ACTOR);
            this.insertActor(mediaInfoVO, EMediaActorType.DIRECTOR);
            // 发送领域事件
            domainEventUtil.publishEvent(EventAction.MEDIA_ADD, mediaInfo);
        }
        return ResultUtil.successWithData(mediaInfo);
    }

    /**
     * 新增媒资视频信息
     *
     * @param mediaInfoVO 媒资信息对象
     */
    public void insertVideo(MediaInfoVO mediaInfoVO) {
        List<MediaVideo> videoList = mediaInfoVO.getVideoList();
        String mediaInfoUid = mediaInfoVO.getUid();
        if (CollectionUtil.isNotEmpty(videoList)) {
            List<MediaVideo> list = new ArrayList<>();
            for (MediaVideo mediaVideo : videoList) {
                mediaVideo.setMediaInfoUid(mediaInfoUid);
                list.add(mediaVideo);
                // 如果是本地存储，才需要转化,其它直接转化完成
                if (!EMediaStorageType.LOCAL_STORAGE.equals(mediaVideo.getStorageType())) {
                    mediaVideo.setVideoStatus(EVideoStatus.CONVERT_SUCCESS.getCode());
                }
            }
            if (list.size() > 0) {
                mediaVideoService.saveBatch(list);
            }
        }
    }

    /**
     * @param mediaInfoVO
     * @Desc 更新演员
     */
    private void insertActor(MediaInfoVO mediaInfoVO, EMediaActorType movieActorType) {
        String mediaInfoUid = mediaInfoVO.getUid();
        List<MediaInfoActor> actorList = mediaInfoVO.getActorList();
        List<MediaInfoActor> directorList = mediaInfoVO.getDirectorList();

        List<MediaInfoActor> insertList = null;
        switch (movieActorType) {
            case ACTOR:
                insertList = actorList;
                break;
            case DIRECTOR:
                insertList = directorList;
                break;
            default:
                return;
        }
        if (CollectionUtil.isNotEmpty(insertList)) {
            for (MediaInfoActor mediaInfoActor : insertList) {
                MediaInfoActor movieActor = new MediaInfoActor();
                BeanUtils.copyProperties(mediaInfoActor, movieActor);
                movieActor.setMediaInfoUid(mediaInfoUid);
                movieActor.setType(movieActorType.getValue());
                mediaInfoActorMapper.insert(movieActor);
            }
        }
    }

    @Override
    @Transactional
    public String editMediaInfo(MediaInfoVO mediaInfoVO) {
        if (StringUtils.isEmpty(RequestHolder.getAdminUid())) {
            ResultUtil.errorWithMessage(MessageConf.OPERATION_FAIL);
        }
        String userUid = adminService.getAdminUserUid(RequestHolder.getAdminUid());
        if (StringUtils.isEmpty(userUid)) {
            ResultUtil.errorWithMessage(MessageConf.NOT_SET_ADMIN_USER_UID);
        }

        String mediaInfoUid = mediaInfoVO.getUid();
        MediaInfo mediaInfo = new MediaInfo();
        BeanUtils.copyProperties(mediaInfoVO, mediaInfo);
        mediaInfo.setUpdateByUid(userUid);
        mediaInfo.setPublishByUid(userUid);
        boolean updateFlag = mediaInfo.updateById();
        if (updateFlag) {
            Map<String, Object> mediaVideoQueryMap = new HashMap();
            mediaVideoQueryMap.put("media_info_uid", mediaInfoUid);
            mediaVideoService.removeByMap(mediaVideoQueryMap);
            Map movieActorMap = new HashMap<>();
            movieActorMap.put("media_info_uid", mediaInfoUid);
            mediaInfoActorMapper.deleteByMap(movieActorMap);
            this.insertVideo(mediaInfoVO);
            this.insertActor(mediaInfoVO, EMediaActorType.ACTOR);
            this.insertActor(mediaInfoVO, EMediaActorType.DIRECTOR);

            // 发送领域事件
            domainEventUtil.publishEvent(EventAction.MEDIA_EDIT, mediaInfo);
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchMediaInfo(List<MediaInfoVO> mediaInfoVOList) {

        List<String> mediaInfoUidList = new ArrayList<>();
        mediaInfoVOList.forEach(item -> {
            mediaInfoUidList.add(item.getUid());
        });

        if (mediaInfoUidList.size() > 0) {
            Collection<MediaInfo> mediaInfoCollection = mediaInfoService.listByIds(mediaInfoUidList);
            mediaInfoCollection.forEach(item -> {
                item.setStatus(EStatus.DISABLED);
                // 发送领域事件
                domainEventUtil.publishEvent(EventAction.MEDIA_DELETE, item);
            });
            mediaInfoService.updateBatchById(mediaInfoCollection);

        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String transformMedia(TransformMediaRequest transformMediaRequest) {
        // 获取视频信息
        List<MediaVideo> videoList = new ArrayList<>();
        // 根据媒资uid获取商品列表
        if (StringUtils.isNotEmpty(transformMediaRequest.getMediaUid())) {
            QueryWrapper<MediaVideo> queryVideoWrapper = new QueryWrapper<>();
            queryVideoWrapper.eq("media_info_uid", transformMediaRequest.getMediaUid());
            videoList = mediaVideoService.list(queryVideoWrapper);
        }
        if (StringUtils.isNotEmpty(transformMediaRequest.getVideoUid())) {
            MediaVideo mediaVideo = mediaVideoService.getById(transformMediaRequest.getVideoUid());
            if (mediaVideo != null) {
                videoList.add(mediaVideo);
            }

        }
        if (videoList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        // 过滤获取到本地存储的文件
        List<MediaVideo> localMediaVideoList = new ArrayList<>();
        for (MediaVideo mediaVideo : videoList) {
            // 只处理本地存储的视频
            if (!EMediaStorageType.LOCAL_STORAGE.equals(mediaVideo.getStorageType())) {
                continue;
            }
            // 只处理待转换或者转化失败的视频
            if (EVideoStatus.CONVERTING.getCode().equals(mediaVideo.getVideoStatus())) {
                continue;
            }
            if (EVideoStatus.CONVERT_SUCCESS.getCode().equals(mediaVideo.getVideoStatus())) {
                continue;
            }
            localMediaVideoList.add(mediaVideo);
        }
        if (localMediaVideoList.size() == 0) {
            return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
        }

//        String fileUploadPath = pictureFeignClient.getFileUploadPath();
//        if (StringUtils.isEmpty(fileUploadPath)) {
//            log.error("[transformMedia] 获取上传文件路径异常");
//            return ResultUtil.errorWithMessage("获取上传文件路径异常");
//        }

        // 获取系统配置
        SystemConfig systemConfig = systemConfigService.getConfig();
        // 判断是否开启本地上传
        if (!EOpenStatus.OPEN.equals(systemConfig.getUploadLocal())) {
            log.error("[transformMedia] 未开启本地上传，无法进行转换");
            return ResultUtil.errorWithMessage("未开启文件本地上传，无法进行转换");
        }

        // 对视频文件进行转码
        for (MediaVideo mediaVideo : localMediaVideoList) {
            String url = mediaVideo.getUrl();
            File tempFile = null;
            try {
//                // 判断是否是本地模式的域名
//                String newFileName = getPath(systemConfig.getLocalPictureBaseUrl(), url);
//                // 将网络上的url文件下载到本地
////                String newFileName = System.currentTimeMillis() + Constants.SYMBOL_POINT + "mp4";
//                // 源文件所在路径
//                String sourcePath = fileUploadPath + newFileName;
//                // 目标文件所在路径
//                String targetPath = fileUploadPath + "/m3u8/" + mediaVideo.getMediaInfoUid() +"/" + mediaVideo.getUid() +"/";
//                // 网络图片才需要
//                HttpUtil.downloadFile(url, tempFilePath);
//
//                tempFile = FileUtil.file(tempFilePath);
//                // 在本地进行m3u8转化
//                generateAllM3u8(fileUploadPath, sourcePath, targetPath, mediaVideo);

                // 调用下游进行转码
                GenerateM3u8FileRequest req = new GenerateM3u8FileRequest();
                req.setFileUid(mediaVideo.getFileUid());
                req.setMediaUid(mediaVideo.getMediaInfoUid());
                req.setVideoUid(mediaVideo.getUid());
                com.moxi.mogublog.commons.entity.File file = pictureFeignClient.generateM3u8File(req);
                if (file == null) {
                    log.error("[transformMedia] 转换视频失败");
                    mediaVideo.setVideoStatus(EVideoStatus.CONVERT_FAIL.getCode());
                    mediaVideo.updateById();
                    continue;
                }
                mediaVideo.setVideoStatus(EVideoStatus.CONVERT_SUCCESS.getCode());
                mediaVideo.setLength(file.getVideoLength());
                mediaVideo.updateById();

            } catch (Exception e) {
                log.error("[transformMedia] 转换视频失败: {}" + e.getMessage());
                e.printStackTrace();
                mediaVideo.setErrorMsg(e.getMessage());
            } finally {
                // 删除临时文件
//                if (tempFile != null && tempFile.exists()) {
//                    tempFile.delete();
//                }
            }
            // 设置本地路径
//            if (StringUtils.isNotEmpty(mediaVideo.getSuperDefinitionUrl())) {
//                mediaVideo.setSuperDefinitionUrl(systemConfig.getLocalPictureBaseUrl() + mediaVideo.getSuperDefinitionUrl());
//            }
//            if (StringUtils.isNotEmpty(mediaVideo.getHighDefinitionUrl())) {
//                mediaVideo.setHighDefinitionUrl(systemConfig.getLocalPictureBaseUrl() + mediaVideo.getHighDefinitionUrl());
//            }
//            if (StringUtils.isNotEmpty(mediaVideo.getStandardDefinitionUrl())) {
//                mediaVideo.setSuperDefinitionUrl(systemConfig.getLocalPictureBaseUrl() + mediaVideo.getStandardDefinitionUrl());
//            }
//            mediaVideo.setVideoStatus(EVideoStatus.CONVERT_SUCCESS.getCode());
//            mediaVideo.updateById();
        }

        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public MediaInfo getMediaInfo(String uid) {
        MediaInfo mediaInfo = mediaInfoService.getById(uid);
        if (mediaInfo == null) {
            return mediaInfo;
        }

        // 设置用户信息
        if (StringUtils.isNotEmpty(mediaInfo.getUpdateByUid())) {
            User user = userService.getById(mediaInfo.getUpdateByUid());
            user = userService.setUserAvatar(user);
            user = userService.convertUser(user);
            mediaInfo.setUser(user);
        }

        // 查询视频列表
        QueryWrapper<MediaVideo> queryVideoWrapper = new QueryWrapper<>();
        queryVideoWrapper.eq("media_info_uid", uid);
        List<MediaVideo> videoList = mediaVideoService.list(queryVideoWrapper);
        videoList = mediaVideoService.convertMediaVideoList(videoList);
        mediaInfo.setVideoList(videoList);

        //查询演员导演列表
        MediaInfoActor mediaInfoActor = new MediaInfoActor();
        mediaInfoActor.setMediaInfoUid(uid);
        mediaInfoActor.setStatus(EStatus.ENABLE);
        mediaInfoActor.setType(EMediaActorType.ACTOR.getValue());
        List<MediaInfoActor> actorList = mediaInfoActorMapper.selectMediaInfoActorList(mediaInfoActor);
        mediaInfoActor.setType(EMediaActorType.DIRECTOR.getValue());
        List<MediaInfoActor> directorList = mediaInfoActorMapper.selectMediaInfoActorList(mediaInfoActor);
        mediaInfo.setActorList(actorList);
        mediaInfo.setDirectorList(directorList);

        //从Redis取出数据，判断该用户是否点击过
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        String jsonResult = redisUtil.get("BLOG_CLICK:" + ip + "#" + mediaInfo.getUid());
        if (StringUtils.isEmpty(jsonResult)) {
            // 给课程增加点击数
            Integer clickCount = mediaInfo.getClickCount() + 1;
            mediaInfo.setClickCount(clickCount);
            mediaInfo.updateById();

            //将该用户点击记录存储到redis中, 24小时后过期
            redisUtil.setEx(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + mediaInfo.getUid(), mediaInfo.getClickCount().toString(),
                    DateUtils.getNowToNextDaySeconds(), TimeUnit.SECONDS);
        }

        return mediaInfo;
    }

    private void generateAllM3u8(String uploadPath, String sourceFilePath, String targetFilePath, MediaVideo newVideo) {
        File sourceFile = new File(sourceFilePath);
        if (!sourceFile.exists()) {
            log.error("文件不存在！sourceFilePath: " + sourceFilePath);
            return;
        }
        MultimediaInfo multimediaInfo = VideoUtils.getVideoInfoByFile(sourceFilePath);
        VideoInfo video = multimediaInfo.getVideo();
        VideoSize size = video.getSize();
        int width = size.getWidth();
        newVideo.setLength(multimediaInfo.getVideoTime());
        //源文件可能不是mp4格式 首先需要转成mp4格式
        String rootFolder = sourceFile.getParent();
        String sourceFileName = sourceFile.getName();
        String m3u8Name = sourceFileName.substring(0, sourceFileName.lastIndexOf(".")) + ".m3u8";
        String extension = FilenameUtils.getExtension(sourceFilePath);
        String folder1080p = targetFilePath + "/" + "1080";
        String folder720p = targetFilePath + "/" + "720";
        String folder360p = targetFilePath + "/" + "360";
        String m3u8Url1080 = "";
        String m3u8Url720 = "";
        String m3u8Url360 = "";
        // 先将视频转成MP4
        File outPutTarget = null;
        long start = System.currentTimeMillis();
        if (width >= 1920) {
            outPutTarget = VideoUtils.convertMp4(sourceFilePath, folder1080p, FfmpegResolutionEnum.RESOLUTION_1080P);
            VideoUtils.generateM3u8(folder1080p + "/" + sourceFileName, folder1080p + "/" + "hls");
            m3u8Url1080 = folder1080p + "/" + "hls" + "/" + m3u8Name;
        } else if (width >= 720) {
            outPutTarget = VideoUtils.convertMp4(sourceFilePath, folder720p, FfmpegResolutionEnum.RESOLUTION_720P);
            VideoUtils.generateM3u8(folder720p + "/" + sourceFileName, folder720p + "/" + "hls");
            m3u8Url720 = folder720p + "/" + "hls" + "/" + m3u8Name;
        } else {
            outPutTarget = VideoUtils.convertMp4(sourceFilePath, folder360p, FfmpegResolutionEnum.RESOLUTION_360P);
            VideoUtils.generateM3u8(folder360p + "/" + sourceFileName, folder360p + "/" + "hls");
            m3u8Url360 = folder360p + "/" + "hls" + "/" + m3u8Name;
        }
        long end = System.currentTimeMillis();
        log.info("[generateAllM3u8] 生成M3U8文件，耗时: {}", (end - start));
        // 最后在把临时文件都删除
        if (outPutTarget != null && outPutTarget.exists()) {
            outPutTarget.delete();
        }

        // 设置视频高清、超清
        newVideo.setSuperDefinitionUrl(getPath(uploadPath, m3u8Url1080));
        newVideo.setHighDefinitionUrl(getPath(uploadPath, m3u8Url720));
        newVideo.setStandardDefinitionUrl(getPath(uploadPath, m3u8Url360));
    }

    /**
     * 获取文件路径
     *
     * @param path
     * @return
     */
    private String getPath(String uploadPath, String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        int dirLastIndex = uploadPath.length();
        String currentPath = "/" + StringUtils.substring(path, dirLastIndex);
        return currentPath;
    }
}
