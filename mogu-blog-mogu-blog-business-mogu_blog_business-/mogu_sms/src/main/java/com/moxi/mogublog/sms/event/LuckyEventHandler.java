package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.LuckyRecord;
import com.moxi.mogublog.commons.entity.Notice;
import com.moxi.mogublog.commons.entity.UserWatch;
import com.moxi.mogublog.commons.vo.LuckyRecordVO;
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
 * 抽奖事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.LUCKY})
public class LuckyEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        LuckyRecord luckyRecord = objectMapper.convertValue(domainEvent.getEntity(), LuckyRecord.class);
        switch (domainEvent.getEventAction()) {
            case LUCKY: {
                this.add(luckyRecord);
            }
            break;
        }
    }

    /**
     * 走抽奖逻辑
     *
     * @param LuckyRecord
     */
    public void add(LuckyRecord LuckyRecord) {
        // 新增关注，判断任务进行情况
        userTaskService.action(Action.LUCKY, null, null);
    }

}
