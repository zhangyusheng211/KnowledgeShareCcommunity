package com.moxi.mogublog.pay.factory;

import com.moxi.mogublog.pay.annotation.PayMethod;
import com.moxi.mogublog.pay.service.PayService;
import com.moxi.mogublog.utils.SpringUtils;
import com.moxi.mougblog.base.enums.EPayMethod;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付服务工厂类
 *
 * @Author 陌溪
 * @Date 2023年4月22日22:56:23
 * @Version 1.0
 **/
@Component
public class PayServiceFactory {
    private static Map<Integer, PayService> payMethodServiceMap = new ConcurrentHashMap<>();

    /**
     * 根据支付方式，获取服务类
     *
     * @param payMethod
     * @return
     */
    public static PayService getPayService(EPayMethod payMethod) {
        // 判断策略是否存在
        if (!payMethodServiceMap.containsKey(payMethod.getType())) {
            throw new IllegalArgumentException("getPayService not found, payMethod:" + payMethod);
        }
        // 获得策略实例
        PayService strategy = payMethodServiceMap.get(payMethod.getType());
        // 执行策略
        return strategy;
    }

    @PostConstruct
    private synchronized void init() {
        // 根据实现类上的MarketName注解来自动注入
        Map<String, PayService> abstractZjOpenApiStrategyMap = SpringUtils.getBeansOfType(PayService.class);
        abstractZjOpenApiStrategyMap.forEach(
                (name, strategy) -> {
                    Class<? extends PayService> clazz = strategy.getClass();
                    String clazzName = clazz.getSimpleName();
                    if (clazzName.contains("$$EnhancerByCGLIB$$") || clazzName.contains("$$EnhancerBySpringCGLIB$$")) { // 如果是CGLIB动态生成的类
                        clazzName = clazzName.substring(0, clazzName.indexOf("$$"));
                    }
                    String finalClazzName = clazzName;
                    PayMethod messagePushType = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, PayMethod.class))
                            .orElseThrow(() -> new IllegalStateException("Error creating bean with name '" + finalClazzName + "'"));
                    EPayMethod[] payMethodEnums = messagePushType.value();
                    if (payMethodEnums != null && payMethodEnums.length > 0) {
                        for (EPayMethod payMethod : payMethodEnums) {
                            payMethodServiceMap.putIfAbsent(payMethod.getType(), strategy);
                        }
                    }
                });
    }
}
