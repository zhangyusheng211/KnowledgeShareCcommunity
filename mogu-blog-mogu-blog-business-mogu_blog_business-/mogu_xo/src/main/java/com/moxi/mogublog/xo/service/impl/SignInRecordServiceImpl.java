package com.moxi.mogublog.xo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.SignInRecord;
import com.moxi.mogublog.commons.vo.DayVO;
import com.moxi.mogublog.commons.vo.SignInRecordVO;
import com.moxi.mogublog.commons.vo.UserEquityRecordVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.SignInRecordMapper;
import com.moxi.mogublog.xo.service.SignInRecordService;
import com.moxi.mogublog.xo.service.UserEquityRecordService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mogublog.xo.utils.HttpClientUtil;
import com.moxi.mougblog.base.enums.ECreditType;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.EUseStatus;
import com.moxi.mougblog.base.enums.EquityType;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.BaseSysConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户签到记录实现
 *
 * @author 遇见
 */
@Service
public class SignInRecordServiceImpl extends SuperServiceImpl<SignInRecordMapper, SignInRecord> implements SignInRecordService {

    /**
     * 连续签到阶段阈值
     * todo 阈值加入配置、积分增加值加入配置
     */
    public static final Integer level_1 = 10;
    public static final Integer level_2 = 30;
    public static final Integer level_3 = 50;
    public static final Integer level_4 = 100;
    @Resource
    UserService userService;
    @Resource
    SignInRecordMapper signInRecordMapper;
    @Resource
    UserEquityRecordService userEquityRecordService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private DomainEventUtil domainEventUtil;

    public static void main(String[] args) {

        BigDecimal zero = new BigDecimal(0.4);
        zero = zero.setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println(zero);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf1.format(new Date()));
        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
        System.out.println(sdf2.format(new Date()));

        Calendar calendar2 = Calendar.getInstance();
        //2021-12-25
        calendar2.set(2021, Calendar.DECEMBER, 25);
        Date strDate2 = calendar2.getTime();
        Calendar calendar1 = Calendar.getInstance();
        //2021-12-26
        calendar1.set(2021, Calendar.DECEMBER, 26);
        Date strDate1 = calendar1.getTime();
        Calendar calendar3 = Calendar.getInstance();
        //2021-12-31
        calendar3.set(2021, Calendar.DECEMBER, 31);
        Date strDate3 = calendar3.getTime();

        Calendar calendar4 = Calendar.getInstance();
        //2021-12-31
        calendar4.set(2022, Calendar.JANUARY, 1);
        Date strDate4 = calendar4.getTime();

        System.out.println(sdf2.format(strDate2));
        System.out.println(sdf2.format(strDate1));
        System.out.println(sdf2.format(strDate3));
        System.out.println(sdf2.format(strDate4));
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public String retroactive(SignInRecordVO signInVO) {
        String userUid = RequestHolder.getUserUid();
        this.retroactive(userUid, signInVO.getRetroactiveDate());
        // 清空用户本月的签到信息
        redisUtil.delete(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + userUid);
        return ResultUtil.successWithMessage("补签成功");
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public Integer retroactive(String userUid, List<String> retroactiveDate) {
        HashSet<String> strings = new HashSet(retroactiveDate);
        Set<String> needSignIn = checkSign(userUid, strings);
        /**
         * 需要补签
         */
        Integer needSignInCount = needSignIn.size();
        if (needSignInCount > 0) {
            List<SignInRecord> list = needSignIn.stream().map(s -> {
                SignInRecord signInRecord = new SignInRecord(userUid, s, 1);
                return signInRecord;
            }).collect(Collectors.toList());
            boolean isSave = this.saveBatch(list);
            if (isSave) {
                // 后续如果有补签卡道具 需要在这里进行消耗道具处理
                userEquityRecordService.useSignInCards(userUid, Long.valueOf(needSignInCount));
                // 发送补签事件
                for (int i = list.size() - 1; i >= 0; i--) {
                    domainEventUtil.publishEvent(EventAction.SIGN_ADD, list.get(i));
                }
            }
        } else {
            throw new InsertException("请选择补签日期");
        }

        /**
         * 计算连签天数 返回上级
         */
        Boolean todayIsSign = hasSign(userUid);
        SignInRecordVO signInRecordVO = signInRecordMapper.querySignContinuousDays(userUid, todayIsSign ? today() : yesterday());
        return signInRecordVO == null ? 0 : signInRecordVO.getContinuousDays();
    }

    @Override
    public List<SignInRecordVO> userSignRecordList(String userUid, Boolean hasSignIn, String dateKey) {
        List<SignInRecordVO> list = signInRecordMapper.userSignRecordList(userUid, dateKey);
        return list.size() > 0 ? list.stream().filter(signInRecordVO -> {
            return hasSignIn ? signInRecordVO.getIsSignIn() == 1 : signInRecordVO.getIsSignIn() == 0;
        }).collect(Collectors.toList()) : new ArrayList<>();
    }


    public String today() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        return simpleDateFormat.format(now.getTime());
    }

    public String yesterday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        return simpleDateFormat.format(now.getTime());
    }

    /**
     * 检测是否已签到
     * 过滤已签到过的日期
     * 返回未签到日期
     *
     * @param userUid
     * @param retroactiveDate
     * @return
     */
    public Set<String> checkSign(String userUid, Set<String> retroactiveDate) {

        // 校验所有的补签日期中，是否包含未来的时间
        for (String dateStr : retroactiveDate) {
            Date date = null;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
                date = format.parse(dateStr + " 23:59:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date now = new Date();
            // 签到了一个后面的日期
            if (now.before(date)) {
                throw new InsertException("无法补签未来的日期");
            }
        }

        List<SignInRecord> recordList = list(new QueryWrapper<SignInRecord>()
                .eq("user_uid", userUid)
                .in(" DATE_FORMAT( sign_date ,'%Y-%m-%d') ", retroactiveDate)
        );
        if (recordList.size() > 0) {
            recordList.stream().map(SignInRecord::getSignDate).collect(Collectors.toList()).forEach(e -> {
                retroactiveDate.remove(e);
            });
        }
        return retroactiveDate;
    }

    @Override
    @Transactional(
            rollbackFor = {Exception.class}
    )
    public String userSignIn(String userUid) {
        if (StringUtils.isEmpty(userUid)) {
            return ResultUtil.errorWithMessage("用户未登录，无法获取签到信息");
        }
        if (hasSign(userUid)) {
            return ResultUtil.errorWithMessage("用户今日已签到");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SignInRecord signInRecord = new SignInRecord(userUid, simpleDateFormat.format(new Date()), 0);
        Boolean isSave = save(signInRecord);
        if (isSave) {
            domainEventUtil.publishEvent(EventAction.SIGN_ADD, signInRecord);
            // 签到成功，增加用户积分
            userService.addUserCredits(ECreditType.SignIn.getAction(), figureUp(getSignInCount(userUid)), null, userUid);
            // 清空用户本月的签到信息
            redisUtil.delete(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + userUid);
            return ResultUtil.successWithData(signInRecord);
        } else {
            log.error("签到失败");
            return ResultUtil.errorWithMessage(MessageConf.INSERT_FAIL);
        }
    }


    public Boolean hasSign(String userUid) {
        SignInRecord signInRecord = this.getOne(
                new LambdaQueryWrapper<SignInRecord>()
                        .eq(SignInRecord::getUserUid, userUid)
                        .apply(" sign_date = DATE_FORMAT({0},'%Y-%m-%d')  ", new Date())
        );
        return signInRecord == null ? false : true;
    }

    @Override
    public String signDataByUserUid(Boolean refresh, String dateStr) {

        String calendarList = redisUtil.get(RedisConf.CALENDAR_LIST);
        List<DayVO> dayVOS = null;
        if (StringUtils.isNotEmpty(calendarList)) {
            dayVOS = JsonUtils.jsonToList(calendarList, DayVO.class);
        } else {
            // 获取万历年 信息
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            String param = String.valueOf(year);
            String result = HttpClientUtil.doGet(BaseSysConf.REQUEST_CALENDAR + param + BaseSysConf.CALENDAR_SUFFIX);
            Map<String, Object> dateMap = JSONArray.parseObject(result, Map.class);
            dayVOS = (List<DayVO>) dateMap.get(BaseSysConf.CALENDAR_DAYS);
            String jsonString = JSON.toJSONString(dayVOS);
            redisUtil.setEx(RedisConf.CALENDAR_LIST, jsonString, 30, TimeUnit.DAYS);
        }

        String userUid = RequestHolder.getUserUid();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String dateKey = simpleDateFormat.format(new Date());
        if (StringUtils.isNotEmpty(dateStr)) {
            dateKey = dateStr;
        }
        String userSignInRecordMonthJson = redisUtil.get(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + userUid + Constants.SYMBOL_COLON + dateKey);
        if (StringUtils.isNotEmpty(userSignInRecordMonthJson) && !refresh) {
            SignInRecordVO signInRecordVO = JsonUtils.jsonToPojo(userSignInRecordMonthJson, SignInRecordVO.class);
            signInRecordVO.setCalendarList(dayVOS);
            return ResultUtil.successWithData(signInRecordVO);
        }

        SignInRecordVO signInRecordVO = new SignInRecordVO();
        List<SignInRecordVO> records = this.userSignRecordList(userUid, true, dateKey);
        /**
         * 当月已签到天数
         */
        signInRecordVO.setSignedDataList(records.stream().map(SignInRecordVO::getSignDate).collect(Collectors.toList()));

        Boolean todayIsSign = hasSign(userUid);
        /**
         * 今日是否签到
         */
        signInRecordVO.setTodayIsSignIn(todayIsSign);
        /**
         * 连签天数
         */
        SignInRecordVO count = signInRecordMapper.querySignContinuousDays(userUid, todayIsSign ? today() : yesterday());
        signInRecordVO.setContinuousDays(count == null ? 0 : count.getContinuousDays());
        // 获取补签卡数量
        UserEquityRecordVO userEquityRecordVO = new UserEquityRecordVO();
        userEquityRecordVO.setUserUid(userUid);
        userEquityRecordVO.setEquityType(EquityType.SIGN_IN_CARD.getType());
        userEquityRecordVO.setUseStatus(EUseStatus.UNUSED);
        Integer signInCardCount = userEquityRecordService.getCount(userEquityRecordVO);
        signInRecordVO.setRetroactiveCard(signInCardCount);
        signInRecordVO.setCalendarList(dayVOS);
        // 缓存用户当月签到记录 【10分钟】
        redisUtil.setEx(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + userUid + Constants.SYMBOL_COLON + dateKey, JsonUtils.objectToJson(signInRecordVO), 10, TimeUnit.MINUTES);
        return ResultUtil.successWithData(signInRecordVO);
    }

    @Override
    public Integer getSignInCount(String userUid) {
        Boolean todayIsSign = hasSign(userUid);
        SignInRecordVO signInRecordVO = signInRecordMapper.querySignContinuousDays(userUid, todayIsSign ? today() : yesterday());
        return signInRecordVO == null ? 0 : signInRecordVO.getContinuousDays();
    }

    @Override
    public Integer getSignInSumCount(String userUid) {
        QueryWrapper<SignInRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return signInRecordMapper.selectCount(queryWrapper);
    }

    public Integer figureUp(int signInCount) {
        if (signInCount < level_1) {
            return ECreditType.SignIn.getCredit();
        } else if (signInCount < level_2) {
            return ECreditType.SignIn.getCredit() + 1;
        } else if (signInCount < level_3) {
            return ECreditType.SignIn.getCredit() + 2;
        } else if (signInCount < level_4) {
            return ECreditType.SignIn.getCredit() + 3;
        } else {
            return ECreditType.SignIn.getCredit() + 5;
        }
    }
}
