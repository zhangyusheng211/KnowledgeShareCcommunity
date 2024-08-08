package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Resource;
import com.moxi.mogublog.commons.vo.ResourceVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 学习视频表 服务类
 *
 * @author 陌溪
 * @date 2018年10月19日21:26:25
 */
public interface ResourceService extends SuperService<Resource> {
    /**
     * 获取学习视频列表
     *
     * @param resourceVO
     * @return
     */
    IPage<Resource> getPageList(ResourceVO resourceVO);

    /**
     * 新增学习视频
     *
     * @param resourceVO
     */
    String addResource(ResourceVO resourceVO);

    /**
     * 编辑学习视频
     *
     * @param resourceVO
     */
    String editResource(ResourceVO resourceVO);

    /**
     * 批量删除学习视频
     *
     * @param resourceVOList
     */
    String deleteBatchResource(List<ResourceVO> resourceVOList);

    /**
     * 获取资源详情
     *
     * @param resourceVO
     * @return
     */
    Resource getDetail(ResourceVO resourceVO);

    /**
     * 下载资源
     *
     * @param resourceVO
     * @return
     */
    Resource download(ResourceVO resourceVO);

    /**
     * 通过VO获取资源数量
     *
     * @param resourceVO
     * @return
     */
    public Integer getResourceCount(ResourceVO resourceVO);
}
