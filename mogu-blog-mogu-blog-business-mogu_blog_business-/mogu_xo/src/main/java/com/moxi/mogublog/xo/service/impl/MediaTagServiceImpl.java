package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.MediaTag;
import com.moxi.mogublog.commons.vo.MediaTagVO;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.MediaTagMapper;
import com.moxi.mogublog.xo.service.MediaTagService;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 媒资标签Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
public class MediaTagServiceImpl extends SuperServiceImpl<MediaTagMapper, MediaTag> implements MediaTagService {
    @Autowired
    MediaTagService mediaTagService;

    @Override
    public IPage<MediaTag> getPageList(MediaTagVO mediaTagVO) {
        QueryWrapper<MediaTag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mediaTagVO.getContent())) {
            queryWrapper.like(SQLConf.CONTENT, mediaTagVO.getContent());
        }

        if (mediaTagVO.getStatus() != null) {
            queryWrapper.eq(SQLConf.STATUS, mediaTagVO.getStatus());
        }

        Page<MediaTag> page = new Page<>();
        page.setCurrent(mediaTagVO.getCurrentPage());
        page.setSize(mediaTagVO.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<MediaTag> pageList = mediaTagService.page(page, queryWrapper);
        return pageList;
    }
}
