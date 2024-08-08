package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.MediaActor;
import com.moxi.mogublog.commons.vo.MediaActorVO;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.MediaActorMapper;
import com.moxi.mogublog.xo.service.MediaActorService;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 演员Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
public class MediaActorServiceImpl extends SuperServiceImpl<MediaActorMapper, MediaActor> implements MediaActorService {

    @Autowired
    MediaActorService mediaActorService;

    @Override
    public IPage<MediaActor> getPageList(MediaActorVO mediaActorVO) {
        QueryWrapper<MediaActor> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mediaActorVO.getName())) {
            queryWrapper.like(SQLConf.NAME, mediaActorVO.getName());
        }

        if (StringUtils.isNotEmpty(mediaActorVO.getDescription())) {
            queryWrapper.like("description", mediaActorVO.getDescription());
        }

        if (mediaActorVO.getStatus() != null) {
            queryWrapper.eq(SQLConf.STATUS, mediaActorVO.getStatus());
        }
        Page<MediaActor> page = new Page<>();
        page.setCurrent(mediaActorVO.getCurrentPage());
        page.setSize(mediaActorVO.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<MediaActor> pageList = mediaActorService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public IPage<MediaActor> selectNotSelectedList(MediaActorVO mediaActorVO) {
        QueryWrapper<MediaActor> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mediaActorVO.getName())) {
            queryWrapper.like(SQLConf.NAME, mediaActorVO.getName());
        }

        if (StringUtils.isNotEmpty(mediaActorVO.getDescription())) {
            queryWrapper.like(SQLConf.DESCRIPTION, mediaActorVO.getDescription());
        }

        Page<MediaActor> page = new Page<>();
        page.setCurrent(mediaActorVO.getCurrentPage());
        page.setSize(mediaActorVO.getPageSize());
        List<String> notSelectedIdsList = mediaActorVO.getNotSelectedIdsList();
        if (CollectionUtil.isNotEmpty(notSelectedIdsList)) {
            queryWrapper.notIn(SQLConf.UID, notSelectedIdsList);
        }
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<MediaActor> pageList = mediaActorService.page(page, queryWrapper);
        return pageList;
    }
}
