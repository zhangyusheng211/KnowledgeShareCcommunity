package com.moxi.mogublog.sms.event.factory;

import com.moxi.mogublog.sms.event.annotation.MessagePushType;
import com.moxi.mogublog.sms.event.service.MessagePushEventService;
import com.moxi.mogublog.utils.SpringUtils;
import com.moxi.mougblog.base.enums.EPushMethod;
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
public class MessagePushFactory {


    private static Map<Integer, MessagePushEventService> messagePushServiceMap = new ConcurrentHashMap<>();

    /**
     * 获取事件服务类
     *
     * @param pushMethod
     * @return
     */
    public static MessagePushEventService getEventService(EPushMethod pushMethod) {
        // 判断策略是否存在
        if (!messagePushServiceMap.containsKey(pushMethod.getType())) {
            throw new IllegalArgumentException("MessagePushService pushMethod not found: " + pushMethod);
        }
        // 获得策略实例
        MessagePushEventService strategy = messagePushServiceMap.get(pushMethod.getType());
        // 执行策略
        return strategy;
    }

    @PostConstruct
    private synchronized void init() {
        // 根据实现类上的MarketName注解来自动注入
        Map<String, MessagePushEventService> abstractZjOpenApiStrategyMap = SpringUtils.getBeansOfType(MessagePushEventService.class);
        abstractZjOpenApiStrategyMap.forEach(
                (name, strategy) -> {
                    Class<? extends MessagePushEventService> clazz = strategy.getClass();
                    String clazzName = clazz.getSimpleName();
                    if (clazzName.contains("$$EnhancerByCGLIB$$") || clazzName.contains("$$EnhancerBySpringCGLIB$$")) { // 如果是CGLIB动态生成的类
                        clazzName = clazzName.substring(0, clazzName.indexOf("$$"));
                    }
                    String finalClazzName = clazzName;
                    MessagePushType messagePushType = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, MessagePushType.class))
                            .orElseThrow(() -> new IllegalStateException("Error creating bean with name '" + finalClazzName + "'"));
                    EPushMethod[] pushMethodEnums = messagePushType.value();
                    if (pushMethodEnums != null && pushMethodEnums.length > 0) {
                        for (EPushMethod pushMethod : pushMethodEnums) {
                            messagePushServiceMap.putIfAbsent(pushMethod.getType(), strategy);
                        }
                    }
                });
    }
}
