package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.MediaCategory;
import com.moxi.mogublog.commons.vo.MediaCategoryVO;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.MediaCategoryMapper;
import com.moxi.mogublog.xo.service.MediaCategoryService;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 媒资分类Service业务层处理
 *
 * @author thh
 * @date 2021-12-14
 */
@Service
public class MediaCategoryServiceImpl extends SuperServiceImpl<MediaCategoryMapper, MediaCategory> implements MediaCategoryService {
    @Autowired
    MediaCategoryService mediaCategoryService;

    @Override
    public IPage<MediaCategory> getPageList(MediaCategoryVO mediaCategory) {
        QueryWrapper<MediaCategory> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mediaCategory.getName())) {
            queryWrapper.like(SQLConf.NAME, mediaCategory.getName());
        }

        if (StringUtils.isNotEmpty(mediaCategory.getContent())) {
            queryWrapper.like(SQLConf.CONTENT, mediaCategory.getContent());
        }

        if (mediaCategory.getStatus() != null) {
            queryWrapper.eq(SQLConf.STATUS, mediaCategory.getStatus());
        }

        Page<MediaCategory> page = new Page<>();
        page.setCurrent(mediaCategory.getCurrentPage());
        page.setSize(mediaCategory.getPageSize());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<MediaCategory> pageList = mediaCategoryService.page(page, queryWrapper);
        return pageList;
    }
}
