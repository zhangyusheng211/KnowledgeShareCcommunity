package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.Notice;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mogublog.sms.task.enums.Action;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.enums.ENoticeType;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 关注事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.WATCH})
public class   WatchEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserWatch userWatch = objectMapper.convertValue(domainEvent.getEntity(), UserWatch.class);
        switch (domainEvent.getEventAction()) {
            case WATCH_ADD: {
                this.add(userWatch);
            }
            break;
            case WATCH_CANCEL: {
                this.cancel(userWatch);
            }
            break;
        }
    }

    /**
     * 添加关注
     *
     * @param userWatch
     */
    public void add(UserWatch userWatch) {
        // 向被关注的用户推送消息通知
        Notice notice = new Notice();
        notice.setUserUid(userWatch.getToUserUid());
        notice.setNoticeType(ENoticeType.WATCH.getCode());
        notice.setBusinessType(EBusinessType.USER_WATCH.getCode());
        notice.setBusinessUid(userWatch.getUid());
        notice.insert();

        // 同时向被关注的Redis发送通知
        String redisKey = RedisConf.USER_RECEIVE_WATCH_COUNT + Constants.SYMBOL_COLON + userWatch.getToUserUid();
        String watchCount = redisUtil.get(redisKey);
        if (StringUtils.isNotEmpty(watchCount)) {
            redisUtil.incrBy(redisKey, Constants.NUM_ONE);
        } else {
            redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
        }

        // 新增关注，判断任务进行情况
        userTaskService.action(Action.WATCH, null, null);
        // 被关注方任务是否执行
        userTaskService.action(Action.TO_WATCH, null, null, userWatch.getToUserUid());
    }

    /**
     * 取消关注
     *
     * @param userWatch
     */
    public void cancel(UserWatch userWatch) {
        log.info("[WatchEventHandler] 取消关注事件暂未实现");
    }


}
