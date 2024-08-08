package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.LuckyRecordMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽奖规则表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
public class LuckyRecordServiceImpl extends SuperServiceImpl<LuckyRecordMapper, LuckyRecord> implements LuckyRecordService {

    @Resource
    LuckyRecordService luckyRecordService;
    @Resource
    LuckyActivityService luckyActivityService;
    @Resource
    AwardProductService awardProductService;
    @Resource
    UserService userService;
    @Resource
    LuckyAwardItemService luckyAwardItemService;
    @Resource
    FileFeignUtil fileFeignUtil;
    /**
     * 领域事件工具类
     */
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<LuckyRecord> getPageList(LuckyRecordVO luckyRecordVO) {
        QueryWrapper<LuckyRecord> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(luckyRecordVO.getKeyword()) && !StringUtils.isEmpty(luckyRecordVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, luckyRecordVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(luckyRecordVO.getUserUid())) {
            queryWrapper.like(SQLConf.USER_UID, luckyRecordVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(luckyRecordVO.getLuckyActivityUid())) {
            queryWrapper.like(SQLConf.LUCKY_ACTIVITY_UID, luckyRecordVO.getLuckyActivityUid());
        }
        if (StringUtils.isNotEmpty(luckyRecordVO.getAwardProductUid())) {
            queryWrapper.like(SQLConf.AWARD_PRODUCT_UID, luckyRecordVO.getAwardProductUid());
        }
        if (luckyRecordVO.getLuckyStatus() != null) {
            queryWrapper.like(SQLConf.LUCKY_STATUS, luckyRecordVO.getLuckyStatus());
        }


        Page<LuckyRecord> page = new Page<>();
        page.setCurrent(luckyRecordVO.getCurrentPage());
        page.setSize(luckyRecordVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(luckyRecordVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyRecordVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(luckyRecordVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyRecordVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        List<String> userUidList = new ArrayList<>();
        List<String> luckyActivityUidList = new ArrayList<>();
        List<String> awardProductUidList = new ArrayList<>();
        List<String> luckyAwardItemUidList = new ArrayList<>();

        IPage<LuckyRecord> pageList = luckyRecordService.page(page, queryWrapper);
        List<LuckyRecord> luckyRecordList = pageList.getRecords();

        for (LuckyRecord luckyRecord : luckyRecordList) {
            userUidList.add(luckyRecord.getUserUid());
            luckyActivityUidList.add(luckyRecord.getLuckyActivityUid());
            awardProductUidList.add(luckyRecord.getAwardProductUid());
            luckyAwardItemUidList.add(luckyRecord.getLuckyAwardItemUid());
        }

        Map<String, User> userMap = new HashMap<>();
        Map<String, LuckyActivity> luckyActivityMap = new HashMap<>();
        Map<String, AwardProduct> awardProductMap = new HashMap<>();
        Map<String, LuckyAwardItem> luckyAwardItemMap = new HashMap<>();

        if (userUidList.size() > 0) {
            userMap = userService.getUserListAndAvatarByIds(userUidList).stream().collect(Collectors.toMap(User::getUid, Function.identity()));
        }
        if (luckyActivityUidList.size() > 0) {
            luckyActivityMap = luckyActivityService.listByIds(luckyActivityUidList).stream().collect(Collectors.toMap(LuckyActivity::getUid, Function.identity()));
        }
        if (awardProductUidList.size() > 0) {
            List<AwardProduct> awardProductList = awardProductService.listByIds(awardProductUidList);
            // 获取商品图ID
            List<String> fileUidList = awardProductList.stream().map(AwardProduct::getFileUid).collect(Collectors.toList());
            Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
            for (AwardProduct product: awardProductList) {
                product.setPhotoUrl(pictureMap.get(product.getFileUid()));
            }
            awardProductMap = awardProductList.stream().collect(Collectors.toMap(AwardProduct::getUid, Function.identity()));
        }
        if (luckyAwardItemUidList.size() > 0) {
            luckyAwardItemMap = luckyAwardItemService.listByIds(luckyAwardItemUidList).stream().collect(Collectors.toMap(LuckyAwardItem::getUid, Function.identity()));
        }
        // 组装到结果后，查询对应的数据
        for (LuckyRecord luckyRecord : luckyRecordList) {
            luckyRecord.setUser(userMap.get(luckyRecord.getUserUid()));
            luckyRecord.setLuckyActivity(luckyActivityMap.get(luckyRecord.getLuckyActivityUid()));
            luckyRecord.setAwardProduct(awardProductMap.get(luckyRecord.getAwardProductUid()));
            luckyRecord.setLuckyAwardItem(luckyAwardItemMap.get(luckyRecord.getLuckyAwardItemUid()));
        }
        pageList.setRecords(luckyRecordList);
        return pageList;
    }


    @Override
    public String addLuckyRecord(LuckyRecordVO luckyRecordVO) {
        LuckyRecord luckyRecord = new LuckyRecord();
        BeanUtil.copyProperties(luckyRecordVO, luckyRecord, SysConf.STATUS);
        luckyRecord.insert();
        // 发布抽奖领域事件
        domainEventUtil.publishEvent(EventAction.LUCKY, luckyRecord);
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editLuckyRecord(LuckyRecordVO luckyRecordVO) {
        LuckyRecord subjectSort = luckyRecordService.getById(luckyRecordVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(luckyRecordVO, subjectSort, SysConf.STATUS, SysConf.UID);
        subjectSort.setUpdateTime(new Date());
        subjectSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchLuckyRecord(List<LuckyRecordVO> luckyRecordVOList) {
        if (luckyRecordVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        luckyRecordVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<LuckyRecord> tagList = luckyRecordService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = luckyRecordService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public int getLuckyRecordCount(LuckyRecordVO luckyRecordVO) {
        QueryWrapper<LuckyRecord> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(luckyRecordVO.getLuckyActivityUid())) {
            queryWrapper.eq(SQLConf.LUCKY_ACTIVITY_UID, luckyRecordVO.getLuckyActivityUid());
        }
        if (StringUtils.isNotEmpty(luckyRecordVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, luckyRecordVO.getUserUid());
        }
        if (luckyRecordVO.getLuckyStatus() != null) {
            queryWrapper.eq(SQLConf.LUCKY_STATUS, luckyRecordVO.getLuckyStatus());
        }
        if (luckyRecordVO.getStartTime() != null) {
            queryWrapper.gt(SQLConf.CREATE_TIME, luckyRecordVO.getStartTime());
        }
        if (luckyRecordVO.getEndTime() != null) {
            queryWrapper.lt(SQLConf.CREATE_TIME, luckyRecordVO.getEndTime());
        }
        return luckyRecordService.count(queryWrapper);
    }

}
