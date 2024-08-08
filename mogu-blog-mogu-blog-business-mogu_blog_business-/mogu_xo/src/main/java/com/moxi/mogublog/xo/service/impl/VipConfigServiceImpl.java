package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.VipConfig;
import com.moxi.mogublog.commons.schema.VipExtraConfig;
import com.moxi.mogublog.commons.vo.VipConfigVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.VipConfigMapper;
import com.moxi.mogublog.xo.service.VipConfigService;
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
 * 会员配置
 *
 * @author 陌溪
 * @date 2024年5月26日08:28:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VipConfigServiceImpl extends SuperServiceImpl<VipConfigMapper, VipConfig> implements VipConfigService {

    @Resource
    private VipConfigService resourceService;


    @Override
    public IPage<VipConfig> getPageList(VipConfigVO resourceVO) {
        QueryWrapper<VipConfig> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(resourceVO.getKeyword()) && !StringUtils.isEmpty(resourceVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, resourceVO.getKeyword().trim()).or().like(SQLConf.SUMMARY, resourceVO.getKeyword().trim());
        }

        if (resourceVO.getPayType() != null) {
            queryWrapper.eq(SQLConf.PAY_TYPE, resourceVO.getPayType());
        }
        if (resourceVO.getChargeType() != null) {
            queryWrapper.eq(SQLConf.CHARGE_TYPE, resourceVO.getChargeType());
        }

        if (resourceVO.getIsPublish() != null) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, resourceVO.getIsPublish());
        }

        if (StringUtils.isNotEmpty(resourceVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(resourceVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(resourceVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(resourceVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        }

        Page<VipConfig> page = new Page<>();
        page.setCurrent(resourceVO.getCurrentPage());
        page.setSize(resourceVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<VipConfig> pageList = resourceService.page(page, queryWrapper);
        List<VipConfig> vipConfigList = pageList.getRecords();
        vipConfigList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getExtra())) {
                VipExtraConfig vipExtraConfig = JsonUtils.jsonToPojo(item.getExtra(), VipExtraConfig.class);
                item.setVipExtraConfig(vipExtraConfig);
            }
        });
        pageList.setRecords(vipConfigList);
        return pageList;
    }

    @Override
    public String addVipConfig(VipConfigVO resourceVO) {
        VipConfig resource = new VipConfig();
        BeanUtils.copyProperties(resourceVO, resource, SysConf.STATUS);
        resource.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editVipConfig(VipConfigVO resourceVO) {
        VipConfig resource = resourceService.getById(resourceVO.getUid());
        BeanUtils.copyProperties(resourceVO, resource, SysConf.STATUS, SysConf.UID);
        resource.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchVipConfig(List<VipConfigVO> resourceVOList) {
        if (resourceVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        resourceVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        Collection<VipConfig> blogSortList = resourceService.listByIds(uids);
        blogSortList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        boolean save = resourceService.updateBatchById(blogSortList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
