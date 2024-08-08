package com.moxi.mogublog.commons.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.incrementer.H2KeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collections;


/**
 * Mybatis-plus全局配置
 */
@EnableTransactionManagement
@Configuration
@MapperScan("com.moxi.mogublog.xo.mapper")
@MapperScan("com.moxi.mogublog.sms.task.mapper")
public class MybatisPlusConfig {
    /**
     * 分页插件 3.5.X
     * @author zhengkai.blog.csdn.net
     */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(-1L);
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.setInterceptors(Collections.singletonList(paginationInnerInterceptor()));
        return mybatisPlusInterceptor;
    }
}
