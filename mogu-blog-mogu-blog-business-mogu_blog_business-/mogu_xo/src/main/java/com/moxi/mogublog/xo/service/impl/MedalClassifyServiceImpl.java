package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.MedalClassify;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.MedalClassifyMapper;
import com.moxi.mogublog.xo.service.MedalClassifyService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 勋章表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class MedalClassifyServiceImpl extends SuperServiceImpl<MedalClassifyMapper, MedalClassify> implements MedalClassifyService {

    @Resource
    private MedalClassifyService medalClassifyService;

    @Override
    public IPage<MedalClassify> getPageList(MedalClassifyVO medalClassifyVO) {
        QueryWrapper<MedalClassify> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(medalClassifyVO.getKeyword()) && !StringUtils.isEmpty(medalClassifyVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, medalClassifyVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(medalClassifyVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, medalClassifyVO.getIsPublish());
        }
        Page<MedalClassify> page = new Page<>();
        page.setCurrent(medalClassifyVO.getCurrentPage());
        page.setSize(medalClassifyVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(medalClassifyVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(medalClassifyVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(medalClassifyVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(medalClassifyVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<MedalClassify> pageList = medalClassifyService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addMedalClassify(MedalClassifyVO medalClassifyVO) {
        MedalClassify medalClassify = new MedalClassify();
        BeanUtil.copyProperties(medalClassifyVO, medalClassify, SysConf.STATUS);
        medalClassify.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editMedalClassify(MedalClassifyVO medalClassifyVO) {
        MedalClassify medalClassify = medalClassifyService.getById(medalClassifyVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(medalClassifyVO, medalClassify, SysConf.STATUS, SysConf.UID);
        medalClassify.setUpdateTime(new Date());
        medalClassify.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchMedalClassify(List<MedalClassifyVO> medalClassifyVOList) {
        if (medalClassifyVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        medalClassifyVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<MedalClassify> tagList = medalClassifyService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = medalClassifyService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
