package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Collect;
import com.moxi.mogublog.commons.feign.WebFeignClient;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mougblog.base.enums.ENoticeType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.EWebDomainEvent;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 收藏事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.COLLECT})
public class CollectEventHandler extends AbstractEventHandler {
    @Resource
    private WebFeignClient webFeignClient;
    /**
     * 事件处理器
     *
     * @param domainEvent
     */
    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        Collect collect = objectMapper.convertValue(domainEvent.getEntity(), Collect.class);
        switch (domainEvent.getEventAction()) {
            case COLLECT_ADD: {
                this.add(collect);
            }
            break;
            case COLLECT_CANCEL: {
                this.deleteCollectCache(collect);
            }
            break;
        }
    }


    /**
     * 添加收藏
     *
     * @param collect
     */
    public void add(Collect collect) {
        /**
         * 站内信通知
         */
        EResourceType resourceType = EResourceType.getType(collect.getCollectType());
        // 获取资源所属的用户
        String toUserUid = commonManager.getUserUidByResource(resourceType, collect.getBizUid());
        if (StringUtils.isNotEmpty(toUserUid)) {
            // 给对方收件箱发消息
            // 给该资源的用户发送点赞通知
            asyncService.executeAsyncNotice(toUserUid, ENoticeType.COLLECT, EResourceType.resourceType2BusinessType(resourceType, 2), collect.getUid());
            // 同时向作者插入一条通知
            String redisKey = RedisConf.USER_RECEIVE_COLLECT_COUNT + Constants.SYMBOL_COLON + toUserUid;
            String watchCount = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(watchCount)) {
                redisUtil.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
            // 刷新通知
            webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, toUserUid);
        }

        // 更新被关注的用户uid
        collect.setToUserUid(toUserUid);
        collect.updateById();

        // 缓存资源id的收藏数 和 是否收藏
        this.deleteCollectCache(collect);

        // 执行用户任务
        userTaskService.action(Action.COLLECT, null, null);

        // 被收藏用户的任务【是否有收藏的成就任务完成？】
        userTaskService.action(Action.TO_COLLECT, null, null, toUserUid);
    }


    /**
     * 取消收藏
     *
     * @param collect
     */
    public void deleteCollectCache(Collect collect) {
        // 缓存资源id的收藏数 和 是否收藏
        redisUtil.delete(RedisConf.COLLECT_COUNT + RedisConf.SEGMENTATION + collect.getBizUid());
        redisUtil.delete(RedisConf.IS_USER_COLLECT + RedisConf.SEGMENTATION + collect.getUserUid() + RedisConf.SEGMENTATION + collect.getBizUid());
    }
}
