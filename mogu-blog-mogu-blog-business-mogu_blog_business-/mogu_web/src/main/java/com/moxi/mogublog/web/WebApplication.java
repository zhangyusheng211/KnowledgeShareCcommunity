package com.moxi.mogublog.web;

import com.moxi.mogublog.utils.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author 遇见
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients("com.moxi.mogublog.commons.feign")
@ComponentScan(basePackages = {
//        "com.plumelog",
        "com.moxi.mogublog.commons.config",
        "com.moxi.mogublog.commons.fallback",
        "com.moxi.mogublog.commons.annotion",
        "com.moxi.mogublog.utils",
        "com.moxi.mogublog.xo.utils",
        "com.moxi.mogublog.xo.manager",
        "com.moxi.mogublog.xo.executor",
        "com.moxi.mogublog.web",
        "com.moxi.mogublog.xo.event.listener",
        "com.moxi.mogublog.xo.service",
        "com.meetc.mogublog.security.**",
})
public class WebApplication {

    @Bean
    public SpringUtils getSpringUtils() {
        return new SpringUtils();
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(WebApplication.class, args);
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
