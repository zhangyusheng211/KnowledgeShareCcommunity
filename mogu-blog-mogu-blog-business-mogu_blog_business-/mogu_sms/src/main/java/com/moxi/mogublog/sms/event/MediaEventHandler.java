package com.moxi.mogublog.sms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.AbstractEventHandler;
import com.moxi.mougblog.base.enums.EOperate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 博客事件处理器
 */
@Slf4j
@Component
@EventType({EntityType.MEDIA})
public class MediaEventHandler extends AbstractEventHandler {

    @Override
    public void doEventHandler(DomainEvent domainEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        MediaInfo mediaInfo = objectMapper.convertValue(domainEvent.getEntity(), MediaInfo.class);
        switch (domainEvent.getEventAction()) {
            case MEDIA_ADD: {
                this.add(mediaInfo, domainEvent.isAdminOperate());
            }
            break;
            case MEDIA_EDIT: {
                this.edit(mediaInfo, domainEvent.isAdminOperate());
            }
            break;
            case MEDIA_DELETE: {
                this.delete(mediaInfo, domainEvent.isAdminOperate());
            }
            break;
        }
    }

    /**
     * 添加课程
     *
     * @param mediaInfo
     * @param isAdminOperate
     */
    public void add(MediaInfo mediaInfo, boolean isAdminOperate) {
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.ADD);
    }


    /**
     * 编辑课程
     *
     * @param mediaInfo
     * @param isAdminOperate
     */
    public void edit(MediaInfo mediaInfo, boolean isAdminOperate) {
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.EDIT);
    }

    /**
     * 删除课程
     *
     * @param mediaInfo
     * @param isAdminOperate
     */
    public void delete(MediaInfo mediaInfo, boolean isAdminOperate) {
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.DELETE);
    }
}
