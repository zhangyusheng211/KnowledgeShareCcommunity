package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.AwardProduct;
import com.moxi.mogublog.commons.vo.AwardProductVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.AwardProductMapper;
import com.moxi.mogublog.xo.service.AwardProductService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 奖品表表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
public class AwardProductServiceImpl extends SuperServiceImpl<AwardProductMapper, AwardProduct> implements AwardProductService {

    @Resource
    AwardProductService awardProductService;
    @Resource
    FileFeignUtil fileFeignUtil;

    @Override
    public IPage<AwardProduct> getPageList(AwardProductVO awardProductVO) {
        QueryWrapper<AwardProduct> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(awardProductVO.getKeyword()) && !StringUtils.isEmpty(awardProductVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, awardProductVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(awardProductVO.getAwardProductUidList()) && awardProductVO.getAwardProductUidList().size() > 0) {
            queryWrapper.in(SQLConf.UID, awardProductVO.getAwardProductUidList());
        }
        Page<AwardProduct> page = new Page<>();
        page.setCurrent(awardProductVO.getCurrentPage());
        page.setSize(awardProductVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(awardProductVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(awardProductVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(awardProductVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(awardProductVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<AwardProduct> pageList = awardProductService.page(page, queryWrapper);

        List<String> fileUidList = new ArrayList<>();
        for (AwardProduct product: pageList.getRecords()) {
            fileUidList.add(product.getFileUid());
        }
        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        for (AwardProduct product: pageList.getRecords()) {
            product.setPhotoUrl(pictureMap.get(product.getFileUid()));
        }
        return pageList;
    }


    @Override
    public String addAwardProduct(AwardProductVO awardProductVO) {
        AwardProduct subjectSort = new AwardProduct();
        BeanUtil.copyProperties(awardProductVO, subjectSort, SysConf.STATUS);
        subjectSort.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editAwardProduct(AwardProductVO awardProductVO) {
        AwardProduct subjectSort = awardProductService.getById(awardProductVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(awardProductVO, subjectSort, SysConf.STATUS, SysConf.UID);
        subjectSort.setUpdateTime(new Date());
        subjectSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchAwardProduct(List<AwardProductVO> awardProductVOList) {
        if (awardProductVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        awardProductVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<AwardProduct> tagList = awardProductService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = awardProductService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
