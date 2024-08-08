package com.moxi.mogublog.xo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.Medal;
import com.moxi.mogublog.commons.entity.MedalClassify;
import com.moxi.mogublog.commons.entity.UserEquityRecord;
import com.moxi.mogublog.commons.vo.MedalClassifyVO;
import com.moxi.mogublog.commons.vo.MedalVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.MedalMapper;
import com.moxi.mogublog.xo.service.MedalClassifyService;
import com.moxi.mogublog.xo.service.MedalService;
import com.moxi.mogublog.xo.service.UserEquityRecordService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.enums.EquityType;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 勋章表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
@Slf4j
public class MedalServiceImpl extends SuperServiceImpl<MedalMapper, Medal> implements MedalService {

    @Autowired
    private MedalService medalService;
    @Resource
    private MedalClassifyService medalClassifyService;
    @Resource
    private UserEquityRecordService userEquityRecordService;
    @Resource
    private AsyncService asyncService;
    @Value(value = "${data.webSite.url}")
    private String webSiteUrl;

    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public IPage<Medal> getPageList(MedalVO medalVo) {
        QueryWrapper<Medal> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(medalVo.getKeyword()) && !StringUtils.isEmpty(medalVo.getKeyword())) {
            queryWrapper.like(SQLConf.NAME, medalVo.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(medalVo.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, medalVo.getIsPublish());
        }
        if (StringUtils.isNotEmpty(medalVo.getMedalClassifyUid())) {
            queryWrapper.eq(SQLConf.MEDAL_CLASSIFY_UID, medalVo.getMedalClassifyUid());
        }
        Page<Medal> page = new Page<>();
        page.setCurrent(medalVo.getCurrentPage());
        page.setSize(medalVo.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(medalVo.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(medalVo.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(medalVo.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(medalVo.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<Medal> pageList = medalService.page(page, queryWrapper);

        List<String> medalClassifyUidList = pageList.getRecords().stream().map(Medal::getMedalClassifyUid).collect(Collectors.toList());
        Map<String, MedalClassify> medalClassifyMap = new HashMap<>();
        if (medalClassifyUidList.size() > 0) {
            List<MedalClassify> medalClassifyList = medalClassifyService.listByIds(medalClassifyUidList);
            medalClassifyMap = medalClassifyList.stream().collect(Collectors.toMap(MedalClassify::getUid, Function.identity(), (oldValue, newValue) -> newValue));
        }
        for (Medal item : pageList.getRecords()) {
            item.setMedalClassify(medalClassifyMap.get(item.getMedalClassifyUid()));
        }
        return pageList;
    }

    @Override
    public List<Medal> getList() {
        QueryWrapper<Medal> medalQueryWrapper = new QueryWrapper<>();
        medalQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return medalService.list(medalQueryWrapper);
    }

    @Override
    public String addMedal(MedalVO medalVO) {
        Medal medal = new Medal();
        BeanUtil.copyProperties(medalVO, medal, SysConf.STATUS);
        medal.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editMedal(MedalVO medalVO) {
        Medal medal = medalService.getById(medalVO.getUid());
        // 插入数据【使用Spring工具类提供的深拷贝】
        BeanUtils.copyProperties(medalVO, medal, SysConf.STATUS, SysConf.UID);
        medal.setUpdateTime(new Date());
        medal.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchMedal(List<MedalVO> medalVOList) {
        if (medalVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        medalVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Medal> tagList = medalService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = medalService.updateBatchById(tagList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public void awardMedal(String userUid, String medalUid) {
        Medal medal = medalService.getById(medalUid);
        if (medal == null) {
            log.error("[awardMedal] 勋章已被删除, medalUid: " + medalUid);
            return;
        }
        if (medal.getStatus() != EStatus.ENABLE) {
            log.error("[awardMedal] 勋章已被删除, medalUid: " + medalUid);
            return;
        }
        if (EPublish.NO_PUBLISH.equals(medal.getIsPublish())) {
            log.error("[awardMedal] 勋章已被下架, medalUid: " + medalUid);
            return;
        }
        if (StringUtils.isEmpty(userUid)) {
            log.error("[awardMedal] 用户uid不能为空");
            return;
        }

        QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.EQUITY_UID, medalUid);
        queryWrapper.eq(SQLConf.EQUITY_TYPE, EquityType.MEDAL.getType());
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.last(SysConf.LIMIT_ONE);
        UserEquityRecord existEquityRecord = userEquityRecordService.getOne(queryWrapper);
        if (existEquityRecord != null) {
            log.info("[awardMedal] 该用户已拥有该勋章, userEquityUid:" + existEquityRecord.getUid());
            return;
        }

        // 设置用户权益
        UserEquityRecord userEquityRecord = new UserEquityRecord();
        userEquityRecord.setUserUid(userUid);
        userEquityRecord.setEquityUid(medalUid);
        userEquityRecord.setEquityType(EquityType.MEDAL.getType());
        userEquityRecord.setIsPermanent(Constants.STR_ONE);
        userEquityRecord.setUseStatus(Constants.STR_ZERO);
        boolean isSave = userEquityRecord.insert();
        // 保存成功，发送消息
        if (isSave) {
            String mediaUrl = "<a _blank='target' href='" + webSiteUrl + "?medalUid=" + medalUid + "'>" + medal.getName() + "</a>";
            asyncService.executeAsyncBusinessNotice(userUid, "恭喜你完成成就任务，获取勋章：" + mediaUrl);
        }
    }

    @Override
    public Medal getMedalByRecent(MedalVO medalVO) {
        String userUid = RequestHolder.getUserUid();
        QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(medalVO.getUid())) {
            queryWrapper.eq(SQLConf.EQUITY_UID, medalVO.getUid());
        } else {
            // 查询最近未查看的勋章
            queryWrapper.eq("use_status", Constants.STR_ZERO);
        }
        queryWrapper.eq(SQLConf.EQUITY_TYPE, EquityType.MEDAL.getType());
        queryWrapper.eq(SQLConf.USER_UID, userUid);
        queryWrapper.last(SysConf.LIMIT_ONE);
        queryWrapper.orderByAsc(SQLConf.CREATE_TIME);
        UserEquityRecord existEquityRecord = userEquityRecordService.getOne(queryWrapper);
        if (existEquityRecord == null) {
            log.info("[awardMedal] 您暂未获取到任何勋章");
            return null;
        }

        // 将该勋章设置为已使用【只要加载过，说明用户已经查阅了】
        existEquityRecord.setUseStatus(Constants.STR_ONE);
        existEquityRecord.updateById();

        // 通过权益，获取到该勋章
        Medal medal = medalService.getById(existEquityRecord.getEquityUid());
        if (medal == null) {
            log.info("[awardMedal] 未查询到勋章信息");
            throw new QueryException("未查询到勋章信息");
        }
        return medal;
    }

    @Override
    public List<MedalClassify> getUserMedalList(MedalClassifyVO medalClassifyVO) {

        // 如果没有传递用户id，那么直接取当前登录的
        if (StringUtils.isEmpty(medalClassifyVO.getUserUid())) {
            String userUid = RequestHolder.getUserUid();
            if (StringUtils.isEmpty(userUid)) {
                throw new QueryException("传入的参数异常");
            }
            medalClassifyVO.setUserUid(userUid);
        }

        QueryWrapper<MedalClassify> medalClassifyQueryWrapper = new QueryWrapper<>();
        if (medalClassifyVO.getType() != null) {
            medalClassifyQueryWrapper.eq(SQLConf.TYPE, medalClassifyVO.getType());
        }
        medalClassifyQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        medalClassifyQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        medalClassifyQueryWrapper.orderByDesc(SQLConf.SORT);
        List<MedalClassify> medalClassifyList = medalClassifyService.list(medalClassifyQueryWrapper);

        // 获取勋章uid
        List<String> medalClassifyUidList = medalClassifyList.stream().map(MedalClassify::getUid).collect(Collectors.toList());
        // 获取勋章获取概率
        Map<String, String> medalFrequencyMap = getAllMedalFrequency();
        // 通过分类，查询所有的勋章
        Map<String, List<Medal>> medalListMap = new HashMap<>();
        if (medalClassifyUidList.size() > 0) {
            QueryWrapper<Medal> medalQueryWrapper = new QueryWrapper<>();
            medalQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            medalQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
            medalQueryWrapper.in(SQLConf.MEDAL_CLASSIFY_UID, medalClassifyUidList);
            medalQueryWrapper.orderByAsc(SQLConf.SORT);
            List<Medal> medalList = medalService.list(medalQueryWrapper);
            List<String> medalUidList = medalList.stream().map(Medal::getUid).collect(Collectors.toList());

            Map<String, UserEquityRecord> userEquityRecordMap = new HashMap<>();
            if (medalUidList.size() > 0) {

                QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(SQLConf.EQUITY_TYPE, EquityType.MEDAL.getType());
                queryWrapper.eq(SQLConf.USER_UID, medalClassifyVO.getUserUid());
                queryWrapper.in(SQLConf.EQUITY_UID, medalUidList);
                List<UserEquityRecord> userEquityRecordList = userEquityRecordService.list(queryWrapper);
                // 获取到list后
                userEquityRecordMap = userEquityRecordList.stream().collect(Collectors.toMap(UserEquityRecord::getEquityUid, Function.identity(), (oldValue, newValue) -> newValue));
            }

            for (Medal medal : medalList) {
                // 设置勋章获取时间
                UserEquityRecord userEquityRecord = userEquityRecordMap.get(medal.getUid());
                if (userEquityRecord != null) {
                    medal.setGainTime(userEquityRecord.getCreateTime());
                }

                if (medalFrequencyMap.get(medal.getUid()) != null) {
                    medal.setGainProbability(medalFrequencyMap.get(medal.getUid()));
                }

                if (StringUtils.isNotEmpty(medal.getMedalClassifyUid())) {
                    List<Medal> medals = medalListMap.get(medal.getMedalClassifyUid());
                    if (medals == null) {
                        medals = new ArrayList<>();
                    }
                    medals.add(medal);
                    medalListMap.put(medal.getMedalClassifyUid(), medals);
                }
            }
        }

        medalClassifyList.forEach(item -> {
            item.setMedalList(medalListMap.get(item.getUid()));
        });

        return medalClassifyList;
    }

    @Override
    public List<Medal> getUserShowMedalList(MedalVO medalVO) {
        if (StringUtils.isEmpty(medalVO.getUserUid())) {
            throw new QueryException(MessageConf.PARAM_INCORRECT);
        }
        // 查询用户获取过那些勋章
        QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.EQUITY_TYPE, EquityType.MEDAL.getType());
        queryWrapper.eq(SQLConf.USER_UID, medalVO.getUserUid());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<UserEquityRecord> userEquityRecordList = userEquityRecordService.list(queryWrapper);
        List<String> medalUidList = userEquityRecordList.stream().map(UserEquityRecord::getEquityUid).collect(Collectors.toList());
        Map<String, UserEquityRecord> userEquityRecordMap = userEquityRecordList.stream().collect(Collectors.toMap(UserEquityRecord::getEquityUid, Function.identity(), (oldValue, newValue) -> newValue));

        Map<String, String> medalFrequencyMap = getAllMedalFrequency();
        // 按勋章重要程度排序
        List<Medal> medalList = new ArrayList<>();
        if (medalUidList.size() > 0) {
            QueryWrapper<Medal> medalQueryWrapper = new QueryWrapper<>();
            medalQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            medalQueryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
            medalQueryWrapper.in(SQLConf.UID, medalUidList);
            medalQueryWrapper.orderByDesc(SQLConf.SORT);
            medalList = medalService.list(medalQueryWrapper);

            // 设置获取时间
            for (Medal medal : medalList) {
                // 设置勋章获取时间
                UserEquityRecord userEquityRecord = userEquityRecordMap.get(medal.getUid());
                if (userEquityRecord != null) {
                    medal.setGainTime(userEquityRecord.getCreateTime());
                }

                if (medalFrequencyMap.get(medal.getUid()) != null) {
                    medal.setGainProbability(medalFrequencyMap.get(medal.getUid()));
                }
            }
        }
        return medalList;
    }

    @Override
    public Map<String, String> getAllMedalFrequency() {

        String medalFrequencyJson = redisUtil.get(RedisConf.ALL_MEDAL_FREQUENCY);
        if (StringUtils.isNotEmpty(medalFrequencyJson)) {
             Map<String, String> map = (Map<String, String>) JsonUtils.jsonToMap(medalFrequencyJson, String.class);
             return map;
        }

        // 获取所有用户数
        int count = userService.getUserCount(EStatus.ENABLE);
        // 获取所有用户勋章ID
        List<Medal> medalList = medalService.getList();
        List<String> medalUidList = new ArrayList<>();
        medalList.forEach(item -> {
            medalUidList.add(item.getUid());
        });

        Map<String, String> medalFrequencyMap = new HashMap<>();
        if (medalUidList.size() == 0) {
            return medalFrequencyMap;
        }

        QueryWrapper<UserEquityRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.EQUITY_TYPE, EquityType.MEDAL.getType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.in(SQLConf.EQUITY_UID, medalUidList);
        queryWrapper.groupBy(SQLConf.EQUITY_UID);
        queryWrapper.select(SQLConf.EQUITY_UID, SQLConf.SUM_ONE);
        List<Map<String, Object>> userEquityMapList = userEquityRecordService.listMaps(queryWrapper);

        for (Map<String, Object> map : userEquityMapList) {
            if (map.get(SQLConf.EQUITY_UID) == null || map.get(SQLConf.SUM) == null) {
                continue;
            }
            String equityUid = map.get(SQLConf.EQUITY_UID).toString();
            Double dd = Double.valueOf(map.get(SQLConf.SUM).toString());
            Double value = dd / count * 100;
            DecimalFormat df = new DecimalFormat("0.00");
            String valueStr = df.format(value);
            medalFrequencyMap.put(equityUid, valueStr + "%");
        }

        // 缓存结果
        redisUtil.setEx(RedisConf.ALL_MEDAL_FREQUENCY, JsonUtils.objectToJson(medalFrequencyMap), 10, TimeUnit.MINUTES);

        return medalFrequencyMap;
    }
}
