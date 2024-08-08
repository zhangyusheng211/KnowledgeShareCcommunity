package com.moxi.mogublog.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;


@EnableTransactionManagement
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableFeignClients("com.moxi.mogublog.commons.feign")
@ComponentScan(basePackages = {
//        "com.plumelog",
        "com.moxi.mogublog.commons.config.feign",
        "com.moxi.mogublog.commons.handler",
        "com.moxi.mogublog.commons.config.redis",
        "com.moxi.mogublog.utils",
        "com.meetc.mogublog.security.**",
        "com.moxi.mogublog.wechat"})
public class WechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatApplication.class, args);
    }
}
