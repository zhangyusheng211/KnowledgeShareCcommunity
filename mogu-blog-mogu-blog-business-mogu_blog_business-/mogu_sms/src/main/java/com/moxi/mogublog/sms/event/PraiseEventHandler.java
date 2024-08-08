package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.UserPraiseRecord;
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
 * 点赞事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.PRAISE})
public class PraiseEventHandler extends AbstractEventHandler {
    @Resource
    private WebFeignClient webFeignClient;
    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserPraiseRecord userPraiseRecord = objectMapper.convertValue(domainEvent.getEntity(), UserPraiseRecord.class);
        switch (domainEvent.getEventAction()) {
            case PRAISE_ADD: {
                this.addPraise(userPraiseRecord);
            }
            break;
            case PRAISE_CANCEL: {
                this.cancelPraise(userPraiseRecord);
            }
            break;
            case TREAD_ADD: {
                this.addTread(userPraiseRecord);
            }
            break;
            case TREAD_CANCEL: {
                this.cancelTrade(userPraiseRecord);
            }
            break;
        }
    }

    /**
     * 添加点赞
     *
     * @param userPraiseRecord
     */
    public void addPraise(UserPraiseRecord userPraiseRecord) {
        /**
         * 站内信通知
         */
        // 类型校验
        EResourceType resourceType = EResourceType.getType(userPraiseRecord.getResourceType());
        String toUserUid = commonManager.getUserUidByResource(resourceType, userPraiseRecord.getResourceUid());
        if (StringUtils.isNotEmpty(toUserUid)) {
            // 给对方收件箱发消息
            // 给该资源的用户发送点赞通知
            asyncService.executeAsyncNotice(toUserUid, ENoticeType.PRAISE, EResourceType.resourceType2BusinessType(resourceType, 1), userPraiseRecord.getUid());
            // 同时向作者插入一条小红点
            String redisKey = RedisConf.USER_RECEIVE_PRAISE_COUNT + Constants.SYMBOL_COLON + toUserUid;
            String watchCount = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(watchCount)) {
                redisUtil.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
            // 刷新通知
            webFeignClient.sendDomainEventMessage(EWebDomainEvent.NOTICE_FLUSH, toUserUid);
        }

        // 更新被点赞/点踩的用户uid
        userPraiseRecord.setToUserUid(toUserUid);
        userPraiseRecord.updateById();

        // 更新用户点赞缓存
        this.deleteUserPraiseCache(userPraiseRecord);

        // 任务执行
        userTaskService.action(Action.PRAISE, null, null);

        // 执行被点赞方的任务
        userTaskService.action(Action.TO_PRAISE, null, null, userPraiseRecord.getToUserUid());
    }

    /**
     * 添加点踩
     *
     * @param userPraiseRecord
     */
    public void addTread(UserPraiseRecord userPraiseRecord) {
        this.deleteUserPraiseCache(userPraiseRecord);
    }

    /**
     * 取消点赞
     *
     * @param userPraiseRecord
     */
    public void cancelPraise(UserPraiseRecord userPraiseRecord) {
        this.deleteUserPraiseCache(userPraiseRecord);
    }

    /**
     * 取消点踩
     *
     * @param userPraiseRecord
     */
    public void cancelTrade(UserPraiseRecord userPraiseRecord) {
        this.deleteUserPraiseCache(userPraiseRecord);
    }

    /**
     * 清空缓存
     *
     * @param userPraiseRecord
     */
    private void deleteUserPraiseCache(UserPraiseRecord userPraiseRecord) {
        // 缓存资源id的点赞数、点踩数
        redisUtil.delete(RedisConf.TREAD_COUNT + RedisConf.SEGMENTATION + userPraiseRecord.getResourceUid());
        redisUtil.delete(RedisConf.IS_USER_TREAD + RedisConf.SEGMENTATION + userPraiseRecord.getUserUid() + RedisConf.SEGMENTATION + userPraiseRecord.getResourceUid());
    }
}
