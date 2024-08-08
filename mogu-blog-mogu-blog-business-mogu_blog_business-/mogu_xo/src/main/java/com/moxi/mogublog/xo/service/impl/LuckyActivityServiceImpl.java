package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.feign.PayFeignClient;
import com.moxi.mogublog.commons.vo.*;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.LuckyActivityMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.entity.SuperEntity;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.BusinessException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 抽奖规则表 服务实现类
 *
 * @author 陌溪
 * @date 2023年7月16日14:56:27
 */
@Service
@Slf4j
public class LuckyActivityServiceImpl extends SuperServiceImpl<LuckyActivityMapper, LuckyActivity> implements LuckyActivityService {

    @Resource
    LuckyActivityService luckyActivityService;
    @Resource
    LuckyAwardItemService luckyAwardItemService;
    @Resource
    AwardProductService awardProductService;
    @Resource
    FileFeignUtil fileFeignUtil;
    @Resource
    UserService userService;
    @Resource
    LuckyRecordService luckyRecordService;
    @Resource
    LuckyRuleService luckyRuleService;
    @Resource
    private AsyncService asyncService;
    @Resource
    private UserEquityRecordService userEquityRecordService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PayFeignClient payFeignClient;

    @Override
    public IPage<LuckyActivity> getPageList(LuckyActivityVO luckyActivityVO) {
        QueryWrapper<LuckyActivity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(luckyActivityVO.getKeyword()) && !StringUtils.isEmpty(luckyActivityVO.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, luckyActivityVO.getKeyword().trim());
        }

        if (luckyActivityVO.getLuckyActivityUidList() != null && luckyActivityVO.getLuckyActivityUidList().size() > 0) {
            queryWrapper.in(SQLConf.UID, luckyActivityVO.getLuckyActivityUidList());
        }

        Page<LuckyActivity> page = new Page<>();
        page.setCurrent(luckyActivityVO.getCurrentPage());
        page.setSize(luckyActivityVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(luckyActivityVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyActivityVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(luckyActivityVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(luckyActivityVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        IPage<LuckyActivity> pageList = luckyActivityService.page(page, queryWrapper);

        List<String> fileUidList = new ArrayList<>();
        List<String> luckyAwardItemUidList = new ArrayList<>();
        List<String> luckyRuleUidList = new ArrayList<>();
        for (LuckyActivity luckyActivity: pageList.getRecords()) {
            fileUidList.add(luckyActivity.getFileUid());
            luckyAwardItemUidList.add(luckyActivity.getUid());
            luckyRuleUidList.add(luckyActivity.getLuckyRuleUid());
        }
        // 为空提前退出
        if (pageList.getRecords().size() == 0) {
            return pageList;
        }
        LuckyAwardItemVO luckyAwardItemVO = new LuckyAwardItemVO();
        luckyAwardItemVO.setLuckyActivityUidList(luckyAwardItemUidList);
        luckyAwardItemVO.setPageSize(1000L);
        luckyAwardItemVO.setCurrentPage(1L);
        luckyAwardItemVO.setOrderByDescColumn(SQLConf.SORT);
        IPage<LuckyAwardItem> luckyAwardItemPage = luckyAwardItemService.getPageList(luckyAwardItemVO);
        List<LuckyAwardItem> luckyAwardItems = luckyAwardItemPage.getRecords();

        List<String> awardProductUidList = new ArrayList<>();
        for (LuckyAwardItem luckyAwardItem: luckyAwardItems) {
            awardProductUidList.add(luckyAwardItem.getAwardProductUid());
        }

        // 获取奖品信息
        AwardProductVO awardProductVO = new AwardProductVO();
        awardProductVO.setAwardProductUidList(awardProductUidList);
        awardProductVO.setPageSize(1000L);
        awardProductVO.setCurrentPage(1L);
        IPage<AwardProduct> awardProductPage = awardProductService.getPageList(awardProductVO);
        List<AwardProduct> awardProductItems = awardProductPage.getRecords();
        Map<String, AwardProduct> awardProductMap = new HashMap<>();
        for (AwardProduct awardProduct: awardProductItems) {
            awardProductMap.put(awardProduct.getUid(), awardProduct);
        }

        List<LuckyRule> luckyRuleList = luckyRuleService.listByIds(luckyRuleUidList);
        Map<String, LuckyRule> luckyRuleMap = luckyRuleList.stream().collect(Collectors.toMap(SuperEntity::getUid, item -> item));

        Map<String, List<LuckyAwardItem>> luckyAwardItemListMap = new HashMap<>();
        // 构建活动项
        for (LuckyAwardItem luckyAwardItem: luckyAwardItems) {
            if (StringUtils.isEmpty(luckyAwardItem.getLuckyActivityUid())) {
                continue;
            }
            // 设置活动项的商品信息
            luckyAwardItem.setAwardProduct(awardProductMap.get(luckyAwardItem.getAwardProductUid()));

            List<LuckyAwardItem> luckyAwardItemList = luckyAwardItemListMap.get(luckyAwardItem.getLuckyActivityUid());
            if (luckyAwardItemList == null) {
                luckyAwardItemList = new ArrayList<>();
            }
            luckyAwardItemList.add(luckyAwardItem);
            luckyAwardItemListMap.put(luckyAwardItem.getLuckyActivityUid(), luckyAwardItemList);
        }

        Map<String, String> pictureMap = fileFeignUtil.fileUidToFileUrlMap(fileUidList);
        for (LuckyActivity luckyActivity: pageList.getRecords()) {
            luckyActivity.setPhotoUrl(pictureMap.get(luckyActivity.getFileUid()));
            List<LuckyAwardItem> luckyAwardItemList = luckyAwardItemListMap.get(luckyActivity.getUid());
            luckyActivity.setLuckyAwardItemList(luckyAwardItemList);
            luckyActivity.setLuckyRule(luckyRuleMap.get(luckyActivity.getLuckyRuleUid()));
        }
        return pageList;
    }

    @Override
    public String  addLuckyActivity(LuckyActivityVO luckyActivityVO) {
        LuckyActivity luckyActivity = new LuckyActivity();
        BeanUtil.copyProperties(luckyActivityVO, luckyActivity, SysConf.STATUS);
        luckyActivity.insert();
        // 设置奖品项
        List<LuckyAwardItem> luckyAwardItemList = luckyActivityVO.getLuckyAwardItemList();
        for (LuckyAwardItem luckyAwardItem : luckyAwardItemList) {
            if (StringUtils.isEmpty(luckyAwardItem.getAwardProductUid()) || luckyAwardItem.getTotal() == 0) {
                throw new InsertException("存在未配置奖品信息的项");
            }
            luckyAwardItem.setLuckyActivityUid(luckyActivity.getUid());
            luckyAwardItem.insert();
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editLuckyActivity(LuckyActivityVO luckyActivityVO) {
        LuckyActivity luckyActivity = luckyActivityService.getById(luckyActivityVO.getUid());
        Assert.notNull(luckyActivity);
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(luckyActivityVO, luckyActivity, SysConf.STATUS, SysConf.UID);
        luckyActivity.setUpdateTime(new Date());
        luckyActivity.updateById();
        // 更新奖品项后，需要查询出所有的奖品信息，然后进行更新
        List<LuckyAwardItem> luckyAwardItemList =  luckyActivityVO.getLuckyAwardItemList();
        if (luckyAwardItemList == null) {
            throw new UpdateException(MessageConf.PARAM_INCORRECT);
        }
        Map<String, LuckyAwardItem> updateOrDeleteMap = new HashMap<>();
        List<LuckyAwardItem> insertLuckyAwardList = new ArrayList<>();
        int index = luckyAwardItemList.size();
        for (LuckyAwardItem luckyAwardItem: luckyAwardItemList) {
            if (StringUtils.isEmpty(luckyAwardItem.getAwardProductUid()) || luckyAwardItem.getTotal() == 0) {
                throw new InsertException("存在未配置奖品信息的项");
            }
            luckyAwardItem.setLuckyActivityUid(luckyActivity.getUid());
            // 将获取数据插入
            luckyAwardItem.setLuckyActivityUid(luckyActivity.getUid());
            index = index - 1;
            luckyAwardItem.setSort(index);
            // 不存在的数据，直接插入
            if (StringUtils.isNotEmpty(luckyAwardItem.getUid())) {
                updateOrDeleteMap.put(luckyAwardItem.getUid(), luckyAwardItem);
            } else {
                insertLuckyAwardList.add(luckyAwardItem);
            }
        }

        List<LuckyAwardItem> luckyAwardItems = getLuckyAwardItems(luckyActivity.getUid());
        // 当前所有的奖励项目，需要判断哪些需要更新，哪些需要删除
        for (LuckyAwardItem luckyAwardItem: luckyAwardItems) {
            // 命中了map，说明需要更新
            LuckyAwardItem luckyAwardItemUpdate = updateOrDeleteMap.get(luckyAwardItem.getUid());
            if (luckyAwardItemUpdate != null) {
                luckyAwardItemUpdate.updateById();
            } else {
                // 直接硬删除
                luckyAwardItem.deleteById();
            }
        }

        // 在操作新增
        for (LuckyAwardItem luckyAwardItem: insertLuckyAwardList) {
            luckyAwardItem.insert();
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    private List<LuckyAwardItem> getLuckyAwardItems(String luckyActivityUid) {
        // 查询出活动下所有的奖品项
        LuckyAwardItemVO luckyAwardItemVO = new LuckyAwardItemVO();
        luckyAwardItemVO.setLuckyActivityUid(luckyActivityUid);
        luckyAwardItemVO.setPageSize(100L);
        luckyAwardItemVO.setCurrentPage(1L);
        IPage<LuckyAwardItem> luckyAwardItemPage = luckyAwardItemService.getPageList(luckyAwardItemVO);
        List<LuckyAwardItem> luckyAwardItems = luckyAwardItemPage.getRecords();
        return luckyAwardItems;
    }

    @Override
    public String deleteBatchLuckyActivity(List<LuckyActivityVO> luckyActivityVOList) {
        if (luckyActivityVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        luckyActivityVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<LuckyActivity> tagList = luckyActivityService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = luckyActivityService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public String lucky(LuckyActivityVO luckyActivityVO) {

        // 获取抽奖的用户ID
        String userUid = RequestHolder.getUserUid();
        // 加锁，限制单个用户的操作频率
        String lockKey = RedisConf.LUCKY_LOCK + Constants.SYMBOL_COLON + userUid;
        String lock = redisUtil.get(lockKey);
        if (StringUtils.isNotEmpty(lock)) {
            throw new BusinessException("操作过于频繁，请稍后再试");
        }
        // 加锁
        redisUtil.setEx(lockKey, "1", 5, TimeUnit.SECONDS);
        User user = userService.getById(userUid);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 获取抽奖活动信息
        LuckyActivity luckyActivity = luckyActivityService.getById(luckyActivityVO.getUid());
        // 判断抽奖是否处于有效期
        if (EPublish.NO_PUBLISH.equals(luckyActivity.getIsPublish())) {
            throw new BusinessException("活动未上架");
        }
        // 判断抽奖是否开启
        boolean luckyStart = DateUtils.checkNowBetweenIn(luckyActivity.getStartTime(), luckyActivity.getEndTime());
        if (!luckyStart) {
            throw new BusinessException("活动暂未开启");
        }

        // 获取抽奖规则，判断是否命中了规则
        LuckyRule luckyRule = luckyRuleService.getById(luckyActivity.getLuckyRuleUid());
        if (luckyRule == null) {
            throw new BusinessException("抽奖活动暂未配置抽奖规则，请联系管理员");
        }

        // 如果设置了最大可抽奖次数
        LuckyRecordVO luckyRecordVO = new LuckyRecordVO();
        luckyRecordVO.setLuckyActivityUid(luckyActivity.getUid());
        luckyRecordVO.setUserUid(userUid);
        if (luckyRule.getMaxLuckyCount() > -1) {
            int count = luckyRecordService.getLuckyRecordCount(luckyRecordVO);
            if (count >= luckyRule.getMaxLuckyCount()) {
                throw new QueryException("已超过系统设置的最大可抽奖次数："+ luckyRule.getMaxLuckyCount() +"次，请下次再参与~");
            }
        }

        // 如果设置了最大可中奖次数
        if (luckyRule.getMaxAwardCount() > -1) {
            luckyRecordVO.setLuckyStatus(1);
            int count = luckyRecordService.getLuckyRecordCount(luckyRecordVO);
            if (count >= luckyRule.getMaxAwardCount()) {
                throw new QueryException("已超过系统设置的最大可中奖次数："+ luckyRule.getMaxAwardCount() +"次，请下次再参与~");
            }
        }

        // 判断是否设置了每日抽取上限
        if (luckyRule.getDayLuckyCount() > -1) {
            luckyRecordVO.setLuckyStatus(null);
            // 获取今天的日期
            luckyRecordVO.setStartTime(DateUtils.getToDayStartDate());
            luckyRecordVO.setEndTime(DateUtils.getToDayEndDate());
            int count = luckyRecordService.getLuckyRecordCount(luckyRecordVO);
            if (count >= luckyRule.getDayLuckyCount()) {
                throw new QueryException("已超过系统设置的每日可抽奖次数："+ luckyRule.getDayLuckyCount() +"次，请明天再参与~");
            }
        }

        // 获取所有的商品，判断是否为空
        String luckyProductForThanks = redisUtil.get(RedisConf.LUCKY_PRODUCT_FOR_THANKS);
        if (StringUtils.isEmpty(luckyProductForThanks)) {
            // 获取所有的商品
            AwardProductVO awardProductVO = new AwardProductVO();
            awardProductVO.setCurrentPage(1L);
            awardProductVO.setPageSize(100L);
            IPage<AwardProduct> awardProductIPage = awardProductService.getPageList(awardProductVO);
            List<AwardProduct> awardProductList = awardProductIPage.getRecords();
            for(AwardProduct item: awardProductList) {
                int type = Integer.parseInt(item.getAwardType());
                if (EAwardType.Thanks.getType() == type) {
                    luckyProductForThanks = item.getUid();
                    break;
                }
            }
            redisUtil.setEx(RedisConf.LUCKY_PRODUCT_FOR_THANKS, luckyProductForThanks, 10, TimeUnit.MINUTES);
        }

        // 获取所有的抽奖项
        List<LuckyAwardItem>  luckyAwardItemList =  getLuckyAwardItems(luckyActivity.getUid());
        // 判断奖品是否都抽取完毕
        int residueTotal = 0;
        List<LuckyAwardItem> enableLuckyAwardItemList = new ArrayList<>();
        for(LuckyAwardItem luckyAwardItem: luckyAwardItemList) {
            residueTotal += luckyAwardItem.getResidueTotal();
            // 只有大于0的奖励，才会放进去抽奖
            if (luckyAwardItem.getResidueTotal() <= 0) {
                continue;
            }
            // 如果是股东，并且是今天是疯狂星期四，奖池不投放谢谢惠顾
//            if (user.IsShareholder() && DateUtils.crazyKFC() && luckyProductForThanks.equals(luckyAwardItem.getAwardProductUid())) {
//                continue;
//            }
            // 是否允许重复抽取
            if (luckyAwardItem.getAllowRepetition() > 0) {
                QueryWrapper<LuckyRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(SQLConf.USER_UID, userUid);
                queryWrapper.eq(SQLConf.LUCKY_ACTIVITY_UID, luckyActivity.getUid());
                queryWrapper.eq(SQLConf.LUCKY_AWARD_ITEM_UID, luckyAwardItem.getUid());
                queryWrapper.last(SysConf.LIMIT_ONE);
                int count = luckyRecordService.count(queryWrapper);
                // 查询出来说明用户曾经抽取过，将不投放该奖励
                if (count > 0 ) {
                    continue;
                }
            }
            enableLuckyAwardItemList.add(luckyAwardItem);
        }
        if (residueTotal == 0) {
            throw new BusinessException("该期抽奖奖品已发放完毕，请下期再来");
        }

        // 进行抽奖
        LuckyAwardItem luckyAwardItem = drawLottery(enableLuckyAwardItemList);
        // 抽中奖品后，还需要将对应的数量的库存减1
        if (luckyAwardItem.getResidueTotal() > 0) {
            // 如果是不放回，才需要扣库存
            if (luckyAwardItem.getPutBack() == 0) {
                luckyAwardItem.setResidueTotal(luckyAwardItem.getResidueTotal() - 1);
                luckyAwardItem.updateById();
            }
        } else {
            // 抽中剩余数量=0的奖品，直接抛出异常
            log.error("[lucky] 抽奖服务出现异常，luckyAwardItem: " + JsonUtils.objectToJson(luckyAwardItem));
            throw new BusinessException("抽奖服务出现异常，请联系管理员");
        }
        AwardProduct awardProduct = awardProductService.getById(luckyAwardItem.getAwardProductUid());
        if (awardProduct == null) {
            log.error("[lucky] 抽奖服务出现异常，awardProduct is null awardProductID: " + luckyAwardItem.getAwardProductUid());
            throw new QueryException("未查询到奖品信息，请联系管理员");
        }

        // 然后扣库存, 判断库存数
        if (awardProduct.getTotal() > 0) {
            // 如果是放回抽奖，不扣除库存
            if (luckyAwardItem.getPutBack() == 0) {
                awardProduct.setTotal(awardProduct.getTotal() - 1);
                awardProduct.updateById();
            }
        } else {
            log.error("[lucky] 抽奖服务出现异常，库存容量不足 awardProduct: " + luckyAwardItem.getAwardProductUid());
            throw new QueryException("抽奖服务出现异常，请联系管理员");
        }

        // 进行抽奖积分服务所需的积分消耗
        userService.addUserCredits(ECreditType.CREDITS_LUCKY.getAction(), -luckyRule.getCostCredits(), luckyActivity.getUid(), RequestHolder.getUserUid());

        // 判断是否需要立即颁发奖励，积分类可以立即颁发
        EquityType equityType = EquityType.getEquityTypeByType(awardProduct.getAwardType());
        if (equityType == null) {
            log.error("[lucky] 抽奖服务出现异常，EAwardType.getEAwardTypeByType error, awardType: " + awardProduct.getAwardType());
            throw new QueryException("奖品配置有误，请联系管理员");
        }

        // 中奖标识，默认未中奖
        int luckyStatus = 1;
        switch (equityType) {
            case Thanks: {
                luckyStatus = 0;
                log.info("[lucky] 用户抽中谢谢参与");
            } break;

            case VIP: {
                log.info("[lucky] 用户抽中VIP体验卡");
                UserEquityRecordVO userEquityRecordVO = new UserEquityRecordVO();
                userEquityRecordVO.setUserUid(userUid);
                userEquityRecordVO.setEquityType(EquityType.VIP.getType());
                userEquityRecordVO.setIsPermanent(Constants.STR_ZERO); // 默认非永久使用
                userEquityRecordVO.setUseStatus(Constants.STR_ZERO);
                userEquityRecordVO.setStartTime(new Date());
                userEquityRecordVO.setEndTime(DateUtils.getNextDate(new Date(), 31));
                userEquityRecordVO.setCount(luckyAwardItem.getCount());
                userEquityRecordService.addUserEquityRecord(userEquityRecordVO);
                asyncService.executeAsyncBusinessNotice(userUid, String.format("恭喜您通过积分抽奖活动，获得%s %d 张", awardProduct.getName(), luckyAwardItem.getCount()));
            } break;

            case SIGN_IN_CARD: {
                log.info("[lucky] 用户抽中签到卡");
                // 调用发放签到卡接口
                boolean sendSuccess = userEquityRecordService.sendSignInCards(userUid, luckyAwardItem.getCount());
                // 发完签到卡，发个站内信
                if (sendSuccess) {
//                    asyncService.executeAsyncBusinessNotice(userUid, String.format("恭喜您通过积分抽奖活动，获得%s %d 张", awardProduct.getName(), luckyAwardItem.getCount()));
                }
                log.info("[Award] 完成任务，获得签到卡奖励; userUid: {}, count: {}", userUid, luckyAwardItem.getCount());
            } break;

            case RedPacket: {
                log.info("[lucky] 用户抽中现金红包");
                Double money = Double.valueOf(luckyAwardItem.getCount());
                // 记录金额变更的日志
                OrderAmountLogVO orderAmountLogVO = new OrderAmountLogVO();
                orderAmountLogVO.setUserAmount(money.longValue());
                orderAmountLogVO.setPlatformAmount(0L);
                orderAmountLogVO.setOldAmount(user.getAmount());
                orderAmountLogVO.setNewAmount(user.getAmount() + money.intValue());
                orderAmountLogVO.setUserUid(userUid);
                orderAmountLogVO.setBusinessType(EBusinessType.LUCKY.getCode());
                orderAmountLogVO.setStatus(EStatus.ENABLE);
                orderAmountLogVO.setResourceType(EResourceType.Lucky.getType());
                orderAmountLogVO.setResourceUid(luckyAwardItem.getUid());
                payFeignClient.addOrderAmount(orderAmountLogVO);

                // 更新用户金额
                userService.updateAmountByUserUid(userUid, money.longValue());
                asyncService.executeAsyncBusinessNotice(userUid, String.format("恭喜您通过积分抽奖活动，获得%s 价值¥ %.2f", awardProduct.getName(), money / 100));

            } break;

            case Credits: {
                log.info("[lucky] 用户抽中积分奖励");
                // 增加用户积分
                userService.addUserCredits(ECreditType.LUCKY_AWARD.getAction(), luckyAwardItem.getCount(), luckyAwardItem.getUid(), userUid);
                // 发完积分奖励，发个站内信
//                asyncService.executeAsyncBusinessNotice(userUid, String.format("恭喜您通过积分抽奖活动，获得 %d 积分", luckyAwardItem.getCount()));
                log.info("[Award] 完成任务，获得积分奖励; userUid: {}, credits: {}", userUid, luckyAwardItem.getCount());
            } break;

            case Entity: {
                log.info("[lucky] 用户抽中实物");
                UserEquityRecordVO userEquityRecordVO = new UserEquityRecordVO();
                userEquityRecordVO.setUserUid(userUid);
                userEquityRecordVO.setEquityType(EquityType.Entity.getType());
                userEquityRecordVO.setEquityUid(awardProduct.getUid());
                userEquityRecordVO.setIsPermanent(Constants.STR_ONE);
                userEquityRecordVO.setUseStatus(Constants.STR_ZERO);
                userEquityRecordVO.setCount(luckyAwardItem.getCount());
                userEquityRecordService.addUserEquityRecord(userEquityRecordVO);
                asyncService.executeAsyncBusinessNotice(userUid, String.format("恭喜您通过积分抽奖活动，获得" + awardProduct.getName() + " %d 个", luckyAwardItem.getCount()));

            } break;
        }

        // 输出抽奖记录
        LuckyRecordVO insertLuckyRecordVO = new LuckyRecordVO();
        insertLuckyRecordVO.setLuckyAwardItemUid(luckyAwardItem.getUid());
        insertLuckyRecordVO.setLuckyActivityUid(luckyActivity.getUid());
        insertLuckyRecordVO.setAwardProductUid(awardProduct.getUid());
        insertLuckyRecordVO.setUserUid(userUid);
        insertLuckyRecordVO.setLuckyStatus(luckyStatus);
        luckyRecordService.addLuckyRecord(insertLuckyRecordVO);

        return luckyAwardItem.getUid();
    }

    /**
     * 根据权重区间实现抽奖算法
     * @param prizes
     * @return
     */
    public LuckyAwardItem drawLottery(List<LuckyAwardItem> prizes) {
        int totalWeight = prizes.stream().mapToInt(LuckyAwardItem::getTotal).sum();
        int randomNumber = new Random(System.currentTimeMillis()).nextInt(totalWeight) + 1;
        int cumulativeWeight = 0;
        for (LuckyAwardItem prize : prizes) {
            cumulativeWeight += prize.getTotal();
            if (randomNumber <= cumulativeWeight) {
                return prize;
            }
        }
        throw new RuntimeException("未抽中任何奖品");
    }

    public static void main(String[] args) {
        LuckyActivityServiceImpl impl = new LuckyActivityServiceImpl();
        List<LuckyAwardItem> luckyAwardItemList = new ArrayList<>();
        LuckyAwardItem awardItem1 = new LuckyAwardItem();
        awardItem1.setUid("AAA");
        awardItem1.setResidueTotal(70);

        LuckyAwardItem awardItem2 = new LuckyAwardItem();
        awardItem2.setUid("BBB");
        awardItem2.setResidueTotal(10);

        LuckyAwardItem awardItem3 = new LuckyAwardItem();
        awardItem3.setUid("CCC");
        awardItem3.setResidueTotal(20);

        luckyAwardItemList.add(awardItem1);
        luckyAwardItemList.add(awardItem2);
        luckyAwardItemList.add(awardItem3);
        Map<String, Integer> countMap = new HashMap<String, Integer>();
        for (int i = 0; i < 10000; i++) {
            String uid = impl.drawLottery(luckyAwardItemList).getUid();
            Integer count = countMap.get(uid);
            countMap.put(uid, count == null ? 1 : count + 1);
        }
        countMap.forEach( (index, count) -> {
            System.out.println("giftId = " + index + ", count=" + count);
        });

    }

}
