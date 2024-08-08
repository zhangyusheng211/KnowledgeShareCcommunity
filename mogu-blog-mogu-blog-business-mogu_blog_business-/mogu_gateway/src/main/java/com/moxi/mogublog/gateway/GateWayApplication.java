package com.moxi.mogublog.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {
//        "com.plumelog",
        "com.moxi.mogublog.gateway",
})
public class GateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        // 配置跨域的信息
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        // SpringBoot升级到2.4.0 之后需要使用该配置
        //允许来源
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }

    @PostConstruct
    private void initBlockHandler() {

        // 当触发限流后页面显示的是Blocked by Sentinel: FlowException。
        // 这个原理是DefaultBlockRequestHandler；实现了BlockRequesthandler接口
        // setBlockHandler ：注册函数用于实现自定义的逻辑处理被限流的请求，对应接口为 BlockRequestHandler 。
        // 默认实现为 DefaultBlockRequestHandler ，当被限流时会返回类似 于下面的错误信息：Blocked by Sentinel: FlowException 。
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 401);
                BlockException e = (BlockException) throwable;
                String message = null;
                if (e instanceof FlowException) {
                    message = "请求接口被限流";
                } else if (e instanceof DegradeException) {
                    message = "请求接口被降级";
                } else if (e instanceof ParamFlowException) {
                    message = "请求接口被热点参数限流";
                } else if (e instanceof AuthorityException) {
                    message = "请求接口被授权规则限制访问";
                } else if (e instanceof SystemBlockException) {
                    message = "请求接口被系统规则限制";
                } else {
                    message = "请求接口被流控";
                }
                map.put("message", message);
                return ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(map));
            }
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
