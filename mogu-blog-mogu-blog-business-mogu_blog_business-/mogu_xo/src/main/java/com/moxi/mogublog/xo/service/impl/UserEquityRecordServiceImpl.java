package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.commons.vo.UserEquityRecordVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.UserEquityRecordMapper;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.utils.RabbitMqUtil;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户权益表 服务实现类
 *
 * @author 陌溪
 * @date 2021年12月18日23:22:20
 */
@Service
@Slf4j
public class UserEquityRecordServiceImpl extends SuperServiceImpl<UserEquityRecordMapper, UserEquityRecord> implements UserEquityRecordService {

    @Resource
    SystemConfigService systemConfigService;
    @Resource
    RabbitMqUtil rabbitMqUtil;
    @Resource
    NoticeService noticeService;
    @Resource
    SysParamsService sysParamsService;
    @Resource
    UserService userService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private UserEquityRecordService userEquityRecordService;

    @Override
    public IPage<UserEquityRecord> getPageList(UserEquityRecordVO userEquityRecordVO) {
        Page<UserEquityRecord> page = new Page<>();
        page.setCurrent(userEquityRecordVO.getCurrentPage());
        page.setSize(userEquityRecordVO.getPageSize());
        return userEquityRecordService.page(page, fill(userEquityRecordVO));
    }

    @Override
    public List<UserEquityRecord> getList(UserEquityRecordVO userEquityRecordVO) {
        return userEquityRecordService.list(fill(userEquityRecordVO));
    }

    @Override
    public Integer getCount(UserEquityRecordVO userEquityRecordVO) {
        return userEquityRecordService.count(fill(userEquityRecordVO));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Override
    public Boolean useSignInCards(String userUid, Long cardCount) {
        UserEquityRecordVO userEquityRecordVO = new UserEquityRecordVO();
        userEquityRecordVO.setUserUid(userUid);
        userEquityRecordVO.setEquityType(EquityType.SIGN_IN_CARD.getType());
        userEquityRecordVO.setUseStatus(Constants.STR_ZERO);
        userEquityRecordVO.setEndTime(new Date());
        userEquityRecordVO.setIsAsc(Constants.ASC);
        userEquityRecordVO.setCurrentPage(1L);
        userEquityRecordVO.setPageSize(cardCount);
        IPage<UserEquityRecord> userEquityRecordPage = getPageList(userEquityRecordVO);
        List<UserEquityRecord> userEquityRecordList = userEquityRecordPage.getRecords();
        if (userEquityRecordList.size() < cardCount) {
            throw new QueryException("您的补签卡不足");
        }
        List<UserEquityRecord> useUserEquityRecords = userEquityRecordList.stream().map(s -> {
            s.setUseStatus(EUseStatus.USED);
            return s;
        }).collect(Collectors.toList());
        // 批量消耗签到卡
        Boolean isSave = userEquityRecordService.updateBatchById(useUserEquityRecords);
        return isSave;
    }

    @Override
    public Boolean sendSignInCards(String userUid, int cardCount) {
        if (cardCount <= 0) {
            throw new InsertException(MessageConf.PARAM_INCORRECT);
        }
        List<UserEquityRecord> userEquityRecordList = new ArrayList<>();
        for (int a = 0; a < cardCount; a++) {
            userEquityRecordList.add(getSignInCard(userUid));
        }
        boolean isSave = userEquityRecordService.saveBatch(userEquityRecordList);
        // 清空用户签到卡记录
        if (isSave) {
            Set<String> keys = redisUtil.keys(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + userUid + Constants.SYMBOL_COLON + "*");
            redisUtil.delete(keys);
        }
        return isSave;
    }

    @Override
    public void addUserEquityRecord(UserEquityRecordVO userEquityRecordVO) {
        int count = userEquityRecordVO.getCount() == 0 ? 1: userEquityRecordVO.getCount();
        // 发放用户权益
        for (int a=0; a< count; a++) {
            UserEquityRecord userEquityRecord = new UserEquityRecord();
            BeanUtil.copyProperties(userEquityRecordVO, userEquityRecord, SysConf.STATUS);
            userEquityRecord.insert();
        }
    }

    @Override
    public Boolean batchSendSignInCard() {
        String topN = sysParamsService.getSysParamsValueByKey(SysConf.USER_TOP_N);
        // 获取topN的用户
        Page<User> page = new Page<>();
        page.setCurrent(Constants.NUM_ONE);
        page.setSize(Integer.parseInt(topN));
        IPage<User> userIPage = userService.page(page, new LambdaQueryWrapper<User>()
                .eq(User::getStatus, EStatus.ENABLE)
                .orderByDesc(User::getCredits)
        );
        List<String> userUidList = userIPage.getRecords().stream().map(user -> user.getUid()).collect(Collectors.toList());
        // 获取到目标用户
        int index = 0;
        List<UserEquityRecord> userEquityRecordList = new ArrayList<>();
        for (String userUid : userUidList) {
            // 前20名，可以获取两张
            if (index < 20) {
                userEquityRecordList.add(getSignInCard(userUid));
                userEquityRecordList.add(getSignInCard(userUid));
            } else {
                userEquityRecordList.add(getSignInCard(userUid));
            }
            index++;
        }
        boolean isSave = userEquityRecordService.saveBatch(userEquityRecordList);

        if (isSave) {
            // TODO签到卡方法成功，通知用户
            List<Notice> noticeList = new ArrayList<>();
            for (UserEquityRecord userEquityRecord : userEquityRecordList) {
                Notice notice = new Notice();
                notice.setUserUid(userEquityRecord.getUserUid());
                notice.setNoticeStatus(0);
                notice.setContent("恭喜你！通过每周排行榜" + EBusinessType.SEND_SIGN_IN_CARD.getName());
                notice.setNoticeType(ENoticeType.SYSTEM.getCode());
                notice.setBusinessUid(userEquityRecord.getUid());
                notice.setBusinessType(EBusinessType.SEND_SIGN_IN_CARD.getCode());
                noticeList.add(notice);

                // 用户收到系统通知
                String redisKey = RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + userEquityRecord.getUserUid();
                String count = redisUtil.get(redisKey);
                if (StringUtils.isNotEmpty(count)) {
                    redisUtil.incrBy(redisKey, Constants.NUM_ONE);
                } else {
                    redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
                }
            }

            // 批量发送站内信
            log.info("批量发送签到卡发放的站内信");
            noticeService.saveBatch(noticeList);

            // 签到卡发放成功，通知运营人员
            SystemConfig systemConfig = systemConfigService.getConfig();
            if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
                if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
                    log.info("签到卡发放通知");
                    String emailContent = "";
                    String dateTimeStr = DateUtils.dateTimeToStr(new Date());
                    emailContent = dateTimeStr + " 签到卡发放成功~";
                    rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), emailContent);
                } else {
                    log.error("网站没有配置通知接收的邮箱地址！");
                }
            }
        }
        return isSave;
    }

    /**
     * 获取用户签到卡
     *
     * @param userUid
     * @return
     */
    private UserEquityRecord getSignInCard(String userUid) {
        UserEquityRecord userEquityRecord = new UserEquityRecord();
        userEquityRecord.setUseStatus(EUseStatus.UNUSED);
        userEquityRecord.setUserUid(userUid);
        userEquityRecord.setEquityType(EquityType.SIGN_IN_CARD.getType());
        userEquityRecord.setIsPermanent(Constants.STR_ZERO);
        // 发放的签到卡，只有一个月的有效期
        userEquityRecord.setStartTime(new Date());
        userEquityRecord.setEndTime(DateUtils.getDate(DateUtils.getNowTime(), 31));
        return userEquityRecord;
    }

    /**
     * 填充数据
     *
     * @param userEquityRecordVO
     * @return
     */
    private QueryWrapper fill(UserEquityRecordVO userEquityRecordVO) {

        QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(userEquityRecordVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, userEquityRecordVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(userEquityRecordVO.getEquityUid())) {
            queryWrapper.eq(SQLConf.EQUITY_UID, userEquityRecordVO.getEquityUid());
        }
        if (StringUtils.isNotEmpty(userEquityRecordVO.getEquityType())) {
            queryWrapper.eq(SQLConf.EQUITY_TYPE, userEquityRecordVO.getEquityType());
        }
        if (StringUtils.isNotEmpty(userEquityRecordVO.getIsPermanent())) {
            queryWrapper.eq(SQLConf.IS_PERMANENT, userEquityRecordVO.getIsPermanent());
        }
        if (StringUtils.isNotEmpty(userEquityRecordVO.getUseStatus())) {
            queryWrapper.eq(SQLConf.USE_STATUS, userEquityRecordVO.getUseStatus());
        }
        if (userEquityRecordVO.getStartTime() != null) {
            queryWrapper.ge(SQLConf.START_TIME, userEquityRecordVO.getStartTime());
        }
        if (userEquityRecordVO.getStartTime() != null) {
            queryWrapper.eq(SQLConf.END_TIME, userEquityRecordVO.getEndTime());
        }
        if (userEquityRecordVO.isAsc()) {
            queryWrapper.orderByAsc(SQLConf.CREATE_TIME);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
//
//         LambdaQueryWrapper<UserEquityRecord> queryWrapper = new LambdaQueryWrapper<UserEquityRecord>()
//                .eq(UserEquityRecord::getStatus, EStatus.ENABLE)
//                .eq(UserEquityRecord::getUserUid, userEquityRecordVO.getUserUid())
//                .eq(UserEquityRecord::getEquityUid, userEquityRecordVO.getEquityUid())
//                .eq(UserEquityRecord::getEquityType, userEquityRecordVO.getEquityType())
//                .eq(UserEquityRecord::getIsPermanent, userEquityRecordVO.getIsPermanent())
//                .eq(UserEquityRecord::getUseStatus, userEquityRecordVO.getUseStatus())
//                .ge(UserEquityRecord::getStartTime, userEquityRecordVO.getStartTime())
//                .le(UserEquityRecord::getEndTime, userEquityRecordVO.getEndTime());
//        if(userEquityRecordVO.isAsc()) {
//            queryWrapper.orderByAsc(UserEquityRecord::getCreateTime);
//        } else {
//            queryWrapper.orderByDesc(UserEquityRecord::getCreateTime);
//        }
        return queryWrapper;
    }
}
