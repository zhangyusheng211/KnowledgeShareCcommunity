package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.LuckyAwardItem;
import com.moxi.mogublog.commons.vo.LuckyAwardItemVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.LuckyAwardItemMapper;
import com.moxi.mogublog.xo.service.LuckyAwardItemService;
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
 * 奖项表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
public class LuckyAwardItemServiceImpl extends SuperServiceImpl<LuckyAwardItemMapper, LuckyAwardItem> implements LuckyAwardItemService {

    @Resource
    LuckyAwardItemService luckyAwardItemService;

    @Override
    public IPage<LuckyAwardItem> getPageList(LuckyAwardItemVO luckyAwardItemVO) {
        QueryWrapper<LuckyAwardItem> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(luckyAwardItemVO.getKeyword()) && !StringUtils.isEmpty(luckyAwardItemVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, luckyAwardItemVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(luckyAwardItemVO.getLuckyActivityUid())) {
            queryWrapper.like(SQLConf.LUCKY_ACTIVITY_UID, luckyAwardItemVO.getLuckyActivityUid());
        }

        if (StringUtils.isNotEmpty(luckyAwardItemVO.getLuckyActivityUidList()) && luckyAwardItemVO.getLuckyActivityUidList().size() > 0) {
            queryWrapper.in(SQLConf.LUCKY_ACTIVITY_UID, luckyAwardItemVO.getLuckyActivityUidList());
        }

        Page<LuckyAwardItem> page = new Page<>();
        page.setCurrent(luckyAwardItemVO.getCurrentPage());
        page.setSize(luckyAwardItemVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(luckyAwardItemVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyAwardItemVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(luckyAwardItemVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyAwardItemVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<LuckyAwardItem> pageList = luckyAwardItemService.page(page, queryWrapper);
        return pageList;
    }


    @Override
    public String addLuckyAwardItem(LuckyAwardItemVO luckyAwardItemVO) {
        LuckyAwardItem subjectSort = new LuckyAwardItem();
        BeanUtil.copyProperties(luckyAwardItemVO, subjectSort, SysConf.STATUS);
        subjectSort.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editLuckyAwardItem(LuckyAwardItemVO luckyAwardItemVO) {
        LuckyAwardItem subjectSort = luckyAwardItemService.getById(luckyAwardItemVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(luckyAwardItemVO, subjectSort, SysConf.STATUS, SysConf.UID);
        subjectSort.setUpdateTime(new Date());
        subjectSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchLuckyAwardItem(List<LuckyAwardItemVO> luckyAwardItemVOList) {
        if (luckyAwardItemVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        luckyAwardItemVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<LuckyAwardItem> tagList = luckyAwardItemService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = luckyAwardItemService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
