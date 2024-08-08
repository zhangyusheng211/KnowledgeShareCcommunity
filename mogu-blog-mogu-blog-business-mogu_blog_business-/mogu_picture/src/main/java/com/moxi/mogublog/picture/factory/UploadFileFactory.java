package com.moxi.mogublog.picture.factory;

import com.moxi.mogublog.picture.annotation.UploadType;
import com.moxi.mogublog.picture.enums.UploadTypeEnum;
import com.moxi.mogublog.picture.service.IUploadService;
import com.moxi.mogublog.picture.service.impl.LocalFileServiceImpl;
import com.moxi.mogublog.utils.SpringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tanhuihuang
 * @Date 2022/5/27 19:36
 * @Version 1.0
 **/
@Component
public class UploadFileFactory {


    private static Map<String, IUploadService> uploadServiceMap = new ConcurrentHashMap<>();

    public static IUploadService getUploadService(UploadTypeEnum uploadTypeEnum) {
        // 判断策略是否存在
        if (!uploadServiceMap.containsKey(uploadTypeEnum.getCode())) {
            throw new IllegalArgumentException("IUploadService uploadType not found " + uploadTypeEnum);
        }
        // 获得策略实例
        IUploadService strategy = uploadServiceMap.get(uploadTypeEnum.getCode());
        // 执行策略
        return strategy;
    }

    @PostConstruct
    private synchronized void init() {
        // 根据实现类上的MarketName注解来自动注入
        Map<String, IUploadService> abstractZjOpenApiStrategyMap = SpringUtils.getBeansOfType(IUploadService.class);

        abstractZjOpenApiStrategyMap.forEach(
                (name, strategy) -> {
                    Class<? extends IUploadService> clazz = strategy.getClass();
                    String clazzName = clazz.getSimpleName();
                    if (clazzName.contains("$$EnhancerByCGLIB$$") || clazzName.contains("$$EnhancerBySpringCGLIB$$")) { // 如果是CGLIB动态生成的类
                        clazzName = clazzName.substring(0, clazzName.indexOf("$$"));
                    }
                    String finalClazzName = clazzName;
                    UploadType uploadType = Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, UploadType.class))
                            .orElseThrow(() -> new IllegalStateException("Error creating bean with name '" + finalClazzName + "'"));
                    UploadTypeEnum[] uploadTypeEnums = uploadType.value();
                    if (uploadTypeEnums != null && uploadTypeEnums.length > 0) {
                        for (UploadTypeEnum uploadTypeEnum : uploadTypeEnums) {
                            uploadServiceMap.putIfAbsent(uploadTypeEnum.getCode(), strategy);
                        }
                    }
                });
    }
}
