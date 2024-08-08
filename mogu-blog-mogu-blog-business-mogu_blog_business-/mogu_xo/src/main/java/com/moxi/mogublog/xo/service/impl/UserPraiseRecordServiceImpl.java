package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.UserPraiseRecord;
import com.moxi.mogublog.commons.vo.UserPraiseRecordVO;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.mapper.UserPraiseRecordMapper;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.xo.service.UserPraiseRecordService;
import com.moxi.mougblog.base.enums.EPraiseType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户签到模块
 *
 * @author 遇见
 */
@Service
@Slf4j
public class UserPraiseRecordServiceImpl extends SuperServiceImpl<UserPraiseRecordMapper, UserPraiseRecord> implements UserPraiseRecordService {

    @Resource
    UserPraiseRecordMapper userPraiseRecordMapper;
    @Resource
    RedisUtil redisUtil;
    @Autowired
    NoticeService noticeService;
    @Resource
    private CommonManager commonManager;
    @Resource
    private BlogService blogService;

    @Override
    public Map<String, Object> praisedCount(UserPraiseRecordVO userPraiseRecordVO) {
        String userUid = RequestHolder.getUserUid();

        Map<String, Object> map = new HashMap<>(Constants.NUM_FOUR);
        String praiseCountJson = redisUtil.get(RedisConf.PRAISE_COUNT + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid());
        String treadCountJson = redisUtil.get(RedisConf.TREAD_COUNT + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid());
        String isPraiseJson = "";
        String isTreadJson = "";
        if (StringUtils.isNotEmpty(userUid)) {
            isPraiseJson = redisUtil.get(RedisConf.IS_USER_PRAISE + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid());
            isTreadJson = redisUtil.get(RedisConf.IS_USER_TREAD + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid());
            if (StringUtils.isNotEmpty(isPraiseJson)) {
                Boolean isPraise = Boolean.valueOf(isPraiseJson);
                map.put("isPraise", isPraise);
            }
            if (StringUtils.isNotEmpty(isTreadJson)) {
                Boolean isTread = Boolean.valueOf(isTreadJson);
                map.put("isTread", isTread);
            }
        } else {
            map.put("isPraise", false);
            map.put("isTread", false);
        }

        if (StringUtils.isNotEmpty(praiseCountJson)) {
            Integer praiseCount = Integer.valueOf(praiseCountJson);
            map.put("praiseCount", praiseCount);
        } else {
            map.put("praiseCount", 0);
        }
        if (StringUtils.isNotEmpty(treadCountJson)) {
            Integer treadCount = Integer.valueOf(treadCountJson);
            map.put("treadCount", treadCount);
        } else {
            map.put("treadCount", 0);
        }

        // 未登录直接返回false
        if (StringUtils.isNotEmpty(praiseCountJson) &&
                StringUtils.isNotEmpty(treadCountJson) &&
                ((StringUtils.isNotEmpty(isPraiseJson) &&
                        StringUtils.isNotEmpty(isTreadJson)) || StringUtils.isEmpty(userUid))) {
            return map;
        }

        // 类型校验
        Integer praiseCount = 0;
        Integer treadCount = 0;
        Boolean isPraise = false;
        Boolean isTread = false;
        EResourceType resourceType = EResourceType.getType(userPraiseRecordVO.getResourceType());
        List<UserPraiseRecord> userPraiseRecordList = userPraiseRecordMapper.selectList(new LambdaQueryWrapper<UserPraiseRecord>()
                .eq(UserPraiseRecord::getResourceUid, userPraiseRecordVO.getResourceUid())
                .eq(UserPraiseRecord::getResourceType, resourceType.getType())
                .eq(UserPraiseRecord::getStatus, EStatus.ENABLE)
        );

        for (UserPraiseRecord item : userPraiseRecordList) {
            if (EPraiseType.PRAISE.getType().equals(item.getPraiseType())) {
                if (StringUtils.isNotEmpty(userUid) && userUid.equals(item.getUserUid())) {
                    isPraise = true;
                }
                praiseCount++;
            } else if (EPraiseType.TREAD.getType().equals(item.getPraiseType())) {
                if (StringUtils.isNotEmpty(userUid) && userUid.equals(item.getUserUid())) {
                    isTread = true;
                }
                treadCount++;
            }
        }
        map.put("praiseCount", praiseCount);
        map.put("isPraise", isPraise);
        map.put("treadCount", treadCount);
        map.put("isTread", isTread);

        // 缓存资源id的点赞数、点踩数
        redisUtil.setEx(RedisConf.PRAISE_COUNT + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid(), JsonUtils.objectToJson(praiseCount), 10, TimeUnit.MINUTES);
        redisUtil.setEx(RedisConf.TREAD_COUNT + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid(), JsonUtils.objectToJson(treadCount), 10, TimeUnit.MINUTES);
        if (StringUtils.isNotEmpty(userUid)) {
            redisUtil.setEx(RedisConf.IS_USER_PRAISE + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid(), JsonUtils.objectToJson(isPraise), 10, TimeUnit.MINUTES);
            redisUtil.setEx(RedisConf.IS_USER_TREAD + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + userPraiseRecordVO.getResourceUid(), JsonUtils.objectToJson(isTread), 10, TimeUnit.MINUTES);
        }
        return map;
    }

    @Override
    public Integer getPraiseCountByWeek(String userUid) {
        QueryWrapper<UserPraiseRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.PRAISE_TYPE, EPraiseType.PRAISE.getType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Date last7Date = DateUtils.getDate(DateUtils.getNowTime(), -7);
        queryWrapper.ge(SQLConf.CREATE_TIME, last7Date);
        return userPraiseRecordMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer getPraiseCount(UserPraiseRecordVO userPraiseRecordVO) {
        QueryWrapper<UserPraiseRecord> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(userPraiseRecordVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, userPraiseRecordVO.getUserUid());
        }
        if (StringUtils.isNotEmpty(userPraiseRecordVO.getToUserUid())) {
            queryWrapper.eq(SQLConf.TO_USER_UID, userPraiseRecordVO.getToUserUid());
        }
        if (StringUtils.isNotEmpty(userPraiseRecordVO.getPraiseType())) {
            queryWrapper.eq(SQLConf.PRAISE_TYPE, userPraiseRecordVO.getPraiseType());
        }
        if (StringUtils.isNotEmpty(userPraiseRecordVO.getResourceUid())) {
            queryWrapper.eq(SQLConf.RESOURCE_UID, userPraiseRecordVO.getResourceUid());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return userPraiseRecordMapper.selectCount(queryWrapper);
    }

    @Override
    public String refreshPraise() {
        QueryWrapper<UserPraiseRecord> queryWrapper = new QueryWrapper<>();
        Page<UserPraiseRecord> page = new Page<>();
        queryWrapper.isNull("to_user_uid");
        page.setCurrent(1);
        page.setPages(10);
        int count = 0;
        while (true) {
            IPage<UserPraiseRecord> userPraiseRecordPage = userPraiseRecordMapper.selectPage(page, queryWrapper);
            if (userPraiseRecordPage.getRecords().size() == 0) {
                break;
            }
            for (UserPraiseRecord userPraiseRecord : userPraiseRecordPage.getRecords()) {
                EResourceType resourceType = EResourceType.getType(userPraiseRecord.getResourceType());
                String toUserUid = commonManager.getUserUidByResource(resourceType, userPraiseRecord.getResourceUid());
                userPraiseRecord.setToUserUid(toUserUid);
                userPraiseRecord.updateById();
            }
            count += 1;
            page.setCurrent(count);
            log.info("[refreshPraise] refreshPraise count, count: {}", count);
        }
        return ResultUtil.successWithMessage("刷数成功");
    }
}
