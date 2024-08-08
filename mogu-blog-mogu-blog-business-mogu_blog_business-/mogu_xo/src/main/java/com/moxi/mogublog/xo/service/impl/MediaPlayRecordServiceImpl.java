package com.moxi.mogublog.xo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.moxi.mogublog.commons.entity.MediaPlayRecord;
import com.moxi.mogublog.commons.vo.MediaPlayRecordVO;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.ServletUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.MediaPlayRecordMapper;
import com.moxi.mogublog.xo.service.MediaPlayRecordService;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 播放记录Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
public class MediaPlayRecordServiceImpl extends SuperServiceImpl<MediaPlayRecordMapper, MediaPlayRecord> implements MediaPlayRecordService {

    @Resource
    private MediaPlayRecordService mediaPlayRecordService;

    @Override
    public IPage<MediaPlayRecord> getPageList(MediaPlayRecordVO mediaPlayRecordVO) {
        return null;
    }

    @Override
    public String insertOrUpdateMediaPlayRecord(MediaPlayRecordVO mediaPlayRecordVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            throw new QueryException("请先登录");
        }
        String ip = IpUtils.getIpAddr(request);
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        LambdaQueryWrapper<MediaPlayRecord> lambdaQueryWrapper = new LambdaQueryWrapper<MediaPlayRecord>()
                .eq(MediaPlayRecord::getVideoUid, mediaPlayRecordVO.getVideoUid());
        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(userUid)) {
            lambdaQueryWrapper.eq(MediaPlayRecord::getUserUid, userUid);
        } else {
            lambdaQueryWrapper.eq(MediaPlayRecord::getIp, ip);
        }

        // 查询出最近的一个
        MediaPlayRecord mediaPlayRecord = this.selectPlayRecordsByVideoId(mediaPlayRecordVO.getVideoUid());
        if (mediaPlayRecord != null) {
            mediaPlayRecord.setUserUid(userUid);
            mediaPlayRecord.setIp(ip);
            mediaPlayRecord.setOperatingSystem(os);
            mediaPlayRecord.setPlayPosition(mediaPlayRecordVO.getPlayPosition());
            mediaPlayRecord.setPlayDuration(mediaPlayRecordVO.getPlayDuration());
            mediaPlayRecord.setUpdateTime(new Date());
            mediaPlayRecord.updateById();
            return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
        }
        // 新增一个播放记录
        mediaPlayRecord = new MediaPlayRecord();
        mediaPlayRecord.setVideoUid(mediaPlayRecordVO.getVideoUid());
        mediaPlayRecord.setUserUid(userUid);
        mediaPlayRecord.setIp(ip);
        mediaPlayRecord.setOperatingSystem(os);
        mediaPlayRecord.setPlayPosition(mediaPlayRecordVO.getPlayPosition());
        mediaPlayRecord.setPlayDuration(mediaPlayRecordVO.getPlayDuration());
        mediaPlayRecord.setUpdateTime(new Date());
        mediaPlayRecord.insert();

        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public MediaPlayRecord selectPlayRecordsByVideoId(String videoId) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (request == null) {
            return null;
        }
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        String ip = IpUtils.getIpAddr(request);
        LambdaQueryWrapper<MediaPlayRecord> lambdaQueryWrapper = new LambdaQueryWrapper<MediaPlayRecord>()
                .eq(MediaPlayRecord::getVideoUid, videoId);

        String userUid = RequestHolder.getUserUid();
        if (StringUtils.isNotEmpty(userUid)) {
            lambdaQueryWrapper.eq(MediaPlayRecord::getUserUid, userUid);
        } else if (StringUtils.isNotEmpty(ip)) {
            lambdaQueryWrapper.eq(MediaPlayRecord::getIp, ip);
        } else {
            throw new QueryException("请先登录");
        }
        if (StringUtils.isNotEmpty(os)) {
            lambdaQueryWrapper.eq(MediaPlayRecord::getOperatingSystem, os);
        }
        // 查询出当前播放记录
        return mediaPlayRecordService.getOne(lambdaQueryWrapper);
    }
}
