package com.moxi.mogublog.commons.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

// z为了使 @TableField(fill = FieldFill.INSERT) 注解生效，你需要配置 MyBatis-Plus 的元数据处理器（MetaObjectHandler）
// 创建一个类实现 MetaObjectHandler 接口，并覆盖其 insertFill 方法：
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // log.info("插入方法填充");
        // 插入时，创建更新时间填充
        setFieldValByName("createTime", new Date(), metaObject);
        setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // log.info("更新方法填充");
        // 更新时，更新时间填充
        setFieldValByName("updateTime", new Date(), metaObject);
    }
}
