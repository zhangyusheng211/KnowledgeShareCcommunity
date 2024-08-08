package com.moxi.mogublog.xo.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.Collect;
import com.moxi.mogublog.commons.vo.CollectVO;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.manager.CollectManager;
import com.moxi.mogublog.xo.mapper.CollectMapper;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.holder.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 收藏模块
 *
 * @author 陌溪
 * @date 2022年3月27日23:24:06
 */
@Service
public class CollectManagerImpl implements CollectManager {

    @Resource
    RedisUtil redisUtil;
    @Autowired
    NoticeService noticeService;
    @Resource
    CollectMapper collectMapper;

    @Override
    public Map<String, Object> getCollectCount(CollectVO collectVo) {
        String userUid = RequestHolder.getUserUid();
        Map<String, Object> map = new HashMap<>(Constants.NUM_TWO);
        String collectCountJson = redisUtil.get(RedisConf.COLLECT_COUNT + RedisConf.SEGMENTATION + collectVo.getBizUid());
        String isCollectJson = "";
        if (StringUtils.isNotEmpty(userUid)) {
            isCollectJson = redisUtil.get(RedisConf.IS_USER_COLLECT + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + collectVo.getBizUid());
            if (StringUtils.isNotEmpty(isCollectJson)) {
                Boolean isCollect = Boolean.valueOf(isCollectJson);
                map.put("isCollect", isCollect);
            }
        } else {
            map.put("isCollect", false);
        }

        if (StringUtils.isNotEmpty(collectCountJson)) {
            Integer collectCount = Integer.valueOf(collectCountJson);
            map.put("collectCount", collectCount);
        } else {
            map.put("collectCount", 0);
        }

        // 未登录直接返回false
        if (StringUtils.isNotEmpty(collectCountJson) && (StringUtils.isNotEmpty(isCollectJson) || StringUtils.isEmpty(userUid))) {
            return map;
        }
        QueryWrapper<Collect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.BIZ_UID, collectVo.getBizUid());
        queryWrapper.eq(SQLConf.COLLECT_TYPE, collectVo.getCollectType());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Collect> collectList = collectMapper.selectList(queryWrapper);
        boolean isCollect = false;
        if (StringUtils.isNotEmpty(userUid)) {
            for (Collect collect : collectList) {
                if (collect.getUserUid().equals(userUid)) {
                    isCollect = true;
                    break;
                }
            }
        }
        map.put("collectCount", collectList.size());
        map.put("isCollect", isCollect);

        // 缓存资源id的收藏数 和 是否收藏
        redisUtil.setEx(RedisConf.COLLECT_COUNT + RedisConf.SEGMENTATION + collectVo.getBizUid(), JsonUtils.objectToJson(collectList.size()), 10, TimeUnit.MINUTES);
        if (StringUtils.isNotEmpty(userUid)) {
            redisUtil.setEx(RedisConf.IS_USER_COLLECT + RedisConf.SEGMENTATION + userUid + RedisConf.SEGMENTATION + collectVo.getBizUid(), JsonUtils.objectToJson(isCollect), 10, TimeUnit.MINUTES);
        }
        return map;
    }
}
