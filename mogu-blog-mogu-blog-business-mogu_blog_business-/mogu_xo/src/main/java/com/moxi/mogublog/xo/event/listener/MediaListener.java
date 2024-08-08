package com.moxi.mogublog.xo.event.listener;

import com.moxi.mogublog.commons.convert.AggEsDocConvert;
import com.moxi.mogublog.commons.entity.MediaInfo;
import com.moxi.mogublog.xo.event.mediaEvent.MediaAddEvent;
import com.moxi.mogublog.xo.event.mediaEvent.MediaEditEvent;
import com.moxi.mogublog.xo.event.mediaEvent.MediaEvent;
import com.moxi.mogublog.xo.executor.AsyncService;
import com.moxi.mougblog.base.enums.EOperate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class MediaListener {
    /**
     * 异步执行服务
     */
    @Resource
    private AsyncService asyncService;

    @EventListener(value = MediaAddEvent.class)
    public void add(MediaEvent event) {
        MediaInfo mediaInfo = (MediaInfo) event.getSource();
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.ADD);
    }

    @EventListener(value = MediaEditEvent.class)
    public void edit(MediaEvent event) {
        MediaInfo mediaInfo = (MediaInfo) event.getSource();
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.EDIT);
    }

    @EventListener(value = MediaAddEvent.class)
    public void delete(MediaEvent event) {
        MediaInfo mediaInfo = (MediaInfo) event.getSource();
        asyncService.executeAsyncUpdateEsAndRedis(AggEsDocConvert.convertMedia(mediaInfo), EOperate.DELETE);
    }

}
