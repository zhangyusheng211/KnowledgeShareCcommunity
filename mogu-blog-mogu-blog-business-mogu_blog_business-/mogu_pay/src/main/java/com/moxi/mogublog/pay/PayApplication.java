package com.moxi.mogublog.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * mogu_pay 启动类
 *
 * @author 遇见
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableScheduling
@EnableTransactionManagement
@EnableFeignClients("com.moxi.mogublog.commons.feign")
@ComponentScan(basePackages = {
//        "com.plumelog",
        "com.moxi.mogublog.commons.annotion",
        "com.moxi.mogublog.commons.config.feign",
        "com.moxi.mogublog.commons.handler",
        "com.moxi.mogublog.commons.config.redis",
        "com.moxi.mogublog.utils",
        "com.meetc.mogublog.security.**",
        "com.moxi.mogublog.pay"})
public class PayApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(PayApplication.class, args);
    }

    /**
     * AliPayService
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
