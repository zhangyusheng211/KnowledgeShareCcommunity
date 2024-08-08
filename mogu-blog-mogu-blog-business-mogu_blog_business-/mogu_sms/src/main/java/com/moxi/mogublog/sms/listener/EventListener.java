package com.moxi.mogublog.sms.listener;

import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.sms.event.factory.EventFactory;
import com.moxi.mogublog.sms.global.SysConf;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mougblog.base.holder.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 事件监听器【用于处理所有发送过来的领域事件】
 *
 * @author 陌溪
 * @date 2022年12月15日23:56:54
 */
@Slf4j
@Component
public class EventListener {

    @RabbitListener(queues = "domain.event")
    public void eventHandler(DomainEvent domainEvent) {
        // 不同事件，对应的处理逻辑？
        log.info("[EventListener] eventHandler come in, entityType: {}", domainEvent.getEntityType());
        log.info("[EventListener] eventAction: {}", domainEvent.getEventAction());
        log.info("[EventListener] domainEvent info: {}", JsonUtils.objectToJson(domainEvent));

        // 设置用户信息
        setUserInfo(domainEvent);

        try {
            // 获取实例类，处理领域事件
            EventFactory.getEventService(domainEvent.getEntityType()).doEventHandler(domainEvent);
        } catch (Exception e) {
            log.error("[EventListener] doEventHandler error：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            // 删除用户信息
            clearUserInfo();
        }
    }

    /**
     * 设置用户信息
     *
     * @param domainEvent
     */
    private void setUserInfo(DomainEvent domainEvent) {
        if (domainEvent.getExtra() != null) {
            Map<String, Object> map = domainEvent.getExtra();
            ThreadLocalUtil.set(SysConf.USER_UID, map.get(SysConf.USER_UID));
            ThreadLocalUtil.set(SysConf.ADMIN_UID, map.get(SysConf.ADMIN_UID));
            ThreadLocalUtil.set(SysConf.IP, map.get(SysConf.IP));
        }
    }

    /**
     * 删除用户信息
     */
    private void clearUserInfo() {
        ThreadLocalUtil.remove(SysConf.USER_UID);
        ThreadLocalUtil.remove(SysConf.ADMIN_UID);
        ThreadLocalUtil.remove(SysConf.IP);
    }
}
