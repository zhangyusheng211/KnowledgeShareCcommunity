package com.moxi.mogublog.sms.event.factory;

import com.moxi.mogublog.commons.domainEvent.EntityType;
import com.moxi.mogublog.sms.event.annotation.EventType;
import com.moxi.mogublog.sms.event.service.IEventService;
import com.moxi.mogublog.utils.SpringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 领域事件工厂类
 *
 * @Author 陌溪
 * @Date 2022年12月25日11:22:11
 * @Version 1.0
 **/
@Component
public class EventFactory {


    private static Map<String, IEventService> eventServiceMap = new ConcurrentHashMap<>();

    /**
     * 获取事件服务类
     *
     * @param entityTypeEnum
     * @return
     */
    public static IEventService getEventService(EntityType entityTypeEnum) {
        // 判断策略是否存在
        if (!eventServiceMap.containsKey(entityTypeEnum.getType())) {
            throw new IllegalArgumentException("IUploadService uploadType not found: " + entityTypeEnum);
        }
        // 获得策略实例
        IEventService strategy = eventServiceMap.get(entityTypeEnum.getType());
        // 执行策略
        return strategy;
    }

    @PostConstruct
    private synchronized void init() {
        // 根据实现类上的MarketName注解来自动注入
        Map<String, IEventService> abstractZjOpenApiStrategyMap = SpringUtils.getBeansOfType(IEventService.class);

        abstractZjOpenApiStrategyMap.forEach(
                (name, strategy) -> {
                    Class<? extends IEventService> clazz = strategy.getClass();
                    String clazzName = clazz.getSimpleName();
                    if (clazzName.contains("$$EnhancerByCGLIB$$") || clazzName.contains("$$EnhancerBySpringCGLIB$$")) { // 如果是CGLIB动态生成的类
                        clazzName = clazzName.substring(0, clazzName.indexOf("$$"));
                    }
                    String finalClazzName = clazzName;
                    EventType eventType = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, EventType.class))
                            .orElseThrow(() -> new IllegalStateException("Error creating bean with name '" + finalClazzName + "'"));
                    EntityType[] entityTypeEnums = eventType.value();
                    if (entityTypeEnums != null && entityTypeEnums.length > 0) {
                        for (EntityType entityType : entityTypeEnums) {
                            eventServiceMap.putIfAbsent(entityType.getType(), strategy);
                        }
                    }
                });
    }
}
