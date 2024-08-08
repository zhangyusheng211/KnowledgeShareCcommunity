package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Subject;
import com.moxi.mogublog.commons.entity.LuckyRule;
import com.moxi.mogublog.commons.vo.LuckyRuleVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.LuckyRuleMapper;
import com.moxi.mogublog.xo.service.SubjectService;
import com.moxi.mogublog.xo.service.LuckyRuleService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 抽奖规则表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
public class LuckyRuleServiceImpl extends SuperServiceImpl<LuckyRuleMapper, LuckyRule> implements LuckyRuleService {

    @Resource
    LuckyRuleService luckyRuleService;

    @Override
    public IPage<LuckyRule> getPageList(LuckyRuleVO luckyRuleVO) {
        QueryWrapper<LuckyRule> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(luckyRuleVO.getKeyword()) && !StringUtils.isEmpty(luckyRuleVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, luckyRuleVO.getKeyword().trim());
        }

        Page<LuckyRule> page = new Page<>();
        page.setCurrent(luckyRuleVO.getCurrentPage());
        page.setSize(luckyRuleVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(luckyRuleVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyRuleVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(luckyRuleVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyRuleVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<LuckyRule> pageList = luckyRuleService.page(page, queryWrapper);
        return pageList;
    }


    @Override
    public String addLuckyRule(LuckyRuleVO luckyRuleVO) {
        LuckyRule luckyRule = new LuckyRule();
        BeanUtil.copyProperties(luckyRuleVO, luckyRule, SysConf.STATUS);
        luckyRule.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editLuckyRule(LuckyRuleVO luckyRuleVO) {
        LuckyRule luckyRule = luckyRuleService.getById(luckyRuleVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(luckyRuleVO, luckyRule, SysConf.STATUS, SysConf.UID);
        luckyRule.setUpdateTime(new Date());
        luckyRule.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchLuckyRule(List<LuckyRuleVO> luckyRuleVOList) {
        if (luckyRuleVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        luckyRuleVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<LuckyRule> tagList = luckyRuleService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = luckyRuleService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
