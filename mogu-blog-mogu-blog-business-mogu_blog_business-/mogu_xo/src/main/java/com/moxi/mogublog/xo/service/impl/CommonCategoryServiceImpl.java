package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.CommonCategory;
import com.moxi.mogublog.commons.vo.CommonCategoryVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.CommonCategoryMapper;
import com.moxi.mogublog.xo.service.CommonCategoryService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 商品类别信息
 *
 * @author 陌溪
 * @date 2024年6月1日22:48:00
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CommonCategoryServiceImpl extends SuperServiceImpl<CommonCategoryMapper, CommonCategory> implements CommonCategoryService {

    @Resource
    private CommonCategoryService commonCategoryService;


    @Override
    public IPage<CommonCategory> getPageList(CommonCategoryVO commonCategoryVO) {
        QueryWrapper<CommonCategory> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commonCategoryVO.getKeyword()) && !StringUtils.isEmpty(commonCategoryVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, commonCategoryVO.getKeyword().trim()).or().like(SQLConf.SUMMARY, commonCategoryVO.getKeyword().trim());
        }

        if (commonCategoryVO.getIsPublish() != null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, commonCategoryVO.getIsPublish());
        }

        if (StringUtils.isNotEmpty(commonCategoryVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(commonCategoryVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(commonCategoryVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(commonCategoryVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        }

        Page<CommonCategory> page = new Page<>();
        page.setCurrent(commonCategoryVO.getCurrentPage());
        page.setSize(commonCategoryVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<CommonCategory> pageList = commonCategoryService.page(page, queryWrapper);
        List<CommonCategory> vipConfigList = pageList.getRecords();
        vipConfigList.forEach(item -> {
        });
        pageList.setRecords(vipConfigList);
        return pageList;
    }

    @Override
    public String addCommonCategory(CommonCategoryVO commonCategoryVO) {
        CommonCategory commonCategory = new CommonCategory();
        BeanUtils.copyProperties(commonCategoryVO, commonCategory, SysConf.STATUS);
        commonCategory.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editCommonCategory(CommonCategoryVO commonCategoryVO) {
        CommonCategory commonCategory = commonCategoryService.getById(commonCategoryVO.getUid());
        BeanUtils.copyProperties(commonCategoryVO, commonCategory, SysConf.STATUS, SysConf.UID);
        commonCategory.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchCommonCategory(List<CommonCategoryVO> commonCategoryVOList) {
        if (commonCategoryVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        commonCategoryVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<CommonCategory> blogSortList = commonCategoryService.listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        boolean save = commonCategoryService.updateBatchById(blogSortList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
