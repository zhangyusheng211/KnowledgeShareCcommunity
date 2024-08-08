package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.HotSearch;
import com.moxi.mogublog.commons.entity.SecretConfig;
import com.moxi.mogublog.commons.schema.OutsideHotSearch;
import com.moxi.mogublog.commons.schema.OutsideHotSearchItem;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.HotSearchMapper;
import com.moxi.mogublog.xo.service.HotSerchService;
import com.moxi.mogublog.xo.service.SecretConfigService;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mogublog.xo.utils.SensitiveUtils;
import com.moxi.mougblog.base.enums.*;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class HotSearchServiceImpl extends SuperServiceImpl<HotSearchMapper, HotSearch> implements HotSerchService {

    @Resource
    HotSerchService hotSerchService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private SensitiveUtils sensitiveUtils;
    @Autowired
    private SysParamsService sysParamsService;
    @Resource
    private SecretConfigService secretConfigService;

    public List<HotSearch> getHotSearchList() {
        String jsonResult = redisUtil.get(RedisConf.HOT_SEARCH_LIST);
        List<HotSearch> keywordList = new ArrayList<>();

        // 判断Redis中是否有数据
        if (StringUtils.isNotEmpty(jsonResult)) {
            keywordList = JsonUtils.jsonToList(jsonResult, HotSearch.class);
        } else {
            String sysWhiteListWords = sysParamsService.getSysParamsValueByKey(SysConf.SYS_WHITELIST_WORD);
            String sysBlackListWords = sysParamsService.getSysParamsValueByKey(SysConf.SYS_HOTSEARCH_BLACKLIST);
            //热搜显示条数
            String sysHotSearchCount = sysParamsService.getSysParamsValueByKey(SysConf.SYS_HOTSEARCH_COUNT);
            String[] whiteKeywords = sysWhiteListWords.split(Constants.SYMBOL_COMMA);
            String[] blackKeywords = sysBlackListWords.split(Constants.SYMBOL_COMMA);
            // 构建白名单表
            List<HotSearch> blackWordList = new ArrayList<>();
            for (String keyword : whiteKeywords) {
                if (StringUtils.isNotEmpty(keyword)) {
                    HotSearch hotSearch = new HotSearch();
                    hotSearch.setContent(keyword);
                    keywordList.add(hotSearch);
                }
            }

            // 构建黑名单表
            for (String keyword : blackKeywords) {
                if (StringUtils.isNotEmpty(keyword)) {
                    HotSearch hotSearch = new HotSearch();
                    hotSearch.setContent(keyword);
                    blackWordList.add(hotSearch);
                }
            }

            int limitCount = Integer.parseInt(sysHotSearchCount) - whiteKeywords.length + blackKeywords.length;
            if (limitCount < 0) {
                limitCount = 0;
            }

            Calendar calendar = Calendar.getInstance();
            Date today = new Date();
            calendar.setTime(today);
            int day = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, day - 7);
            Date dateBefore = calendar.getTime();
            QueryWrapper<HotSearch> wrapper = new QueryWrapper<>();
            wrapper.select("content, count(*) as hotCount");
            wrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            wrapper.ge(SQLConf.CREATE_TIME, dateBefore);
            wrapper.groupBy(SQLConf.CONTENT);
            wrapper.orderByDesc(SQLConf.HOT_COUNT);
            wrapper.last("limit " + limitCount);
            List<HotSearch> searchList = hotSerchService.list(wrapper);

            for (HotSearch hotSearch : searchList) {
                boolean isHit = false;
                for (String blackKeyword : blackKeywords) {
                    if (hotSearch.getContent().contains(blackKeyword)) {
                        isHit = true;
                        break;
                    }
                }

                for (String whiteKeyword : whiteKeywords) {
                    if (hotSearch.getContent().contains(whiteKeyword)) {
                        isHit = true;
                        break;
                    }
                }

                if (!isHit) {
                    keywordList.add(hotSearch);
                }
                if (keywordList.size() > Integer.parseInt(sysHotSearchCount)) {
                    break;
                }
            }

            if (keywordList.size() > 0) {
                // 将热搜插入到搜索表中
                redisUtil.setEx(RedisConf.HOT_SEARCH_LIST, JsonUtils.objectToJson(keywordList), 10, TimeUnit.MINUTES);
            }
        }

        return keywordList;
    }

    @Override
    public List<OutsideHotSearchItem> getOutsideHotSearch(String type) {

        EHotSearchType hotSearchType = EHotSearchType.getType(type);
        if (hotSearchType == null) {
            throw new QueryException("传入类型有误");
        }
        SecretConfig secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_SECRET.getType(), EThirdSecretType.CODER_UTIL.getType());
        String key = String.format("%s:%s", RedisConf.OUTSIDE_HOT_SEARCH_LIST, hotSearchType.getType());
        String webResult = redisUtil.get(key);
        Boolean isHit = false;
        if (StringUtils.isEmpty(webResult)) {
            String params = String.format("access-key=%s&secret-key=%s", secretConfig.getBizKey(), secretConfig.getBizSecret());
            webResult = HttpRequestUtil.sendGet(hotSearchType.getUrl(), params);
            isHit = true;
        }

        // 解析热搜
        OutsideHotSearch resp = JsonUtils.jsonToPojo(webResult, OutsideHotSearch.class);
        List<OutsideHotSearchItem> outsideHotSearchItems = new ArrayList<>();
        if (resp != null && resp.getSuccess()) {
            outsideHotSearchItems = resp.getData();
            // 缓存redis
            if (isHit) {
                redisUtil.setEx(key, webResult, 5, TimeUnit.MINUTES);
            }
        }
        return outsideHotSearchItems;
    }

    @Override
    public String addHotSearch(String keyword) {

        //敏感词过滤
        Map<String, String> filter = sensitiveUtils.filter(keyword, false, SysConf.SYS_SENSITIVE_WORD);
        if (filter.containsKey(SysConf.COUNT)) {
            if (StringUtils.isNotEmpty(filter.get(SysConf.COUNT))) {
                if (Integer.valueOf(filter.get(SysConf.COUNT)) > 0) {
                    //搜索内容存在敏感词 ， 不进行记录
                    return ResultUtil.errorWithMessage("存在敏感词，不进行记录数据");
                } else {
                    HttpServletRequest request = RequestHolder.getRequest();
                    String userUid = RequestHolder.getUserUid();
                    LambdaQueryWrapper<HotSearch> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(HotSearch::getUserUid, userUid);
                    //设置时区
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    Date today = calendar.getTime();
                    queryWrapper.eq(HotSearch::getContent, keyword);
                    queryWrapper.ge(HotSearch::getCreateTime, today);
                    List<HotSearch> hotSearches = hotSerchService.list(queryWrapper);
                    // 用户当日搜索 重复数据 仅记录一次
                    if (hotSearches.size() > 0) {
                        //不记录
                        return ResultUtil.errorWithMessage("重复搜索数据,当前搜索未记录");
                    } else {
                        //同一ip  搜索数据每天只记录前五次
                        String ip = IpUtils.getIpAddr(request);
                        Object ipLimit = redisTemplate.opsForValue().get(RedisConf.HOT_SEARCH_LIMIT + ip);

                        if (ipLimit != null) {
                            if ((Integer) ipLimit <= 5) {
                                HotSearch hotSearch = new HotSearch();
                                hotSearch.setUserIp(ip);
                                hotSearch.setContent(keyword);
                                hotSearch.setStatus(EStatus.ENABLE);
                                hotSearch.setUserUid(RequestHolder.getUserUid());
                                hotSerchService.save(hotSearch);
                            }
                            redisTemplate.opsForValue().increment(RedisConf.HOT_SEARCH_LIMIT + ip);
                        } else {
                            HotSearch hotSearch = new HotSearch();
                            hotSearch.setUserIp(ip);
                            hotSearch.setContent(keyword);
                            hotSearch.setStatus(EStatus.ENABLE);
                            hotSearch.setUserUid(RequestHolder.getUserUid());
                            hotSerchService.save(hotSearch);
                            redisTemplate.opsForValue().set(RedisConf.HOT_SEARCH_LIMIT + Constants.SYMBOL_COLON + ip, 1, 24, TimeUnit.HOURS);
                        }
                    }
                }
            }
        }
        return ResultUtil.errorWithMessage("用户搜索数据记录成功");
    }
}
