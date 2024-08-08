package com.moxi.mogublog.xo.utils;

import com.moxi.mogublog.commons.domainEvent.DomainEvent;
import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mougblog.base.holder.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 领域事件相关工具类
 *
 * @author 陌溪
 * @date 2022年12月16日09:01:39
 */
@Slf4j
@RefreshScope
@Component
public class DomainEventUtil {

    public static final String EXCHANGE_DIRECT = "exchange.direct";
    public static final String ROUTING_KEY_EVENT = "domain.event";
    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送领域事件
     *
     * @param eventAction
     * @param entity
     * @param
     */
    public void publishEvent(EventAction eventAction, Object entity) {
        this.publishEvent(eventAction, entity, RequestHolder.checkAdmin(), null);
    }

    /**
     * 发送领域事件，携带额外参数
     *
     * @param eventAction
     * @param entity
     * @param extraMap
     * @param
     */
    public void publishEventWithExtra(EventAction eventAction, Object entity, Map<String, Object> extraMap) {
        this.publishEvent(eventAction, entity, RequestHolder.checkAdmin(), extraMap);
    }


    /**
     * 发送领域事件，完全自定义
     *
     * @param eventAction
     * @param entity
     * @param extraMap
     */
    public void publishEvent(EventAction eventAction, Object entity, boolean isAdminOperate, Map<String, Object> extraMap) {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setUid(StringUtils.getUUID());
        domainEvent.setEventAction(eventAction);
        domainEvent.setEntityType(eventAction.getEntityType());
        domainEvent.setEntity(entity);
        domainEvent.setEntityUid(getEntityUid(eventAction.getEntityType(), entity));
        domainEvent.setCreateTime(new Date());
        domainEvent.setAdminOperate(isAdminOperate);
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        // 设置用户信息
        if (StringUtils.isNotEmpty(RequestHolder.getUserUid())) {
            extraMap.put(SysConf.USER_UID, RequestHolder.getUserUid());
        }
        if (StringUtils.isNotEmpty(RequestHolder.getAdminUid())) {
            extraMap.put(SysConf.ADMIN_UID, RequestHolder.getAdminUid());
        }
        // 设置IP地址
        if (StringUtils.isNotEmpty(RequestHolder.getIp())) {
            extraMap.put(SysConf.IP, RequestHolder.getIp());
        }

        domainEvent.setExtra(extraMap);

        log.info("[publishEvent] entityType: {}, actionName: {}", eventAction.getEntityType(), eventAction.getActionName());
        log.info("[publishEvent] EventAction: {}", JsonUtils.objectToJson(eventAction));
        try {
            //发送到RabbitMq
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY_EVENT, domainEvent);
        } catch (Exception e) {
            log.error("[publishEvent] 领域消息推送失败， entityType: {}, actionName: {}, error: {}", eventAction.getEntityType(), eventAction.getActionName(), e.getMessage());
        }
    }


    private String getEntityUid(EntityType entityType, Object entity) {
        switch (entityType) {
            case BLOG: {
                Blog blog = (Blog) entity;
                return blog != null ? blog.getUid() : null;
            }
            case QUESTION: {
                Question question = (Question) entity;
                return question != null ? question.getUid() : null;
            }
            case MEDIA: {
                MediaInfo mediaInfo = (MediaInfo) entity;
                return mediaInfo != null ? mediaInfo.getUid() : null;
            }
            case MOMENT: {
                UserMoment userMoment = (UserMoment) entity;
                return userMoment != null ? userMoment.getUid() : null;
            }
            case COMMENT: {
                Comment comment = (Comment) entity;
                return comment != null ? comment.getUid() : null;
            }
            case PROBLEM: {
                Problem problem = (Problem) entity;
                return problem != null ? problem.getUid() : null;
            }
            case FILE: {
                File file = (File) entity;
                return file != null ? file.getUid() : null;
            }
            default:
                return null;

        }
    }
}
