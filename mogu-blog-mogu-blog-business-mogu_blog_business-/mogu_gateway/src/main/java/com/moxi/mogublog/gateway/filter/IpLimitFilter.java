package com.moxi.mogublog.gateway.filter;

import com.alibaba.nacos.common.utils.StringUtils;
import com.moxi.mogublog.gateway.config.IpBloomFilterConfig;
import com.moxi.mogublog.gateway.config.IpLimiter;
import com.moxi.mogublog.gateway.global.RedisConf;
import com.moxi.mogublog.gateway.global.SysConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * IP限流全局拦截器
 *
 * @author 陌溪
 * @date 2022年3月28日08:40:37
 */
@Slf4j
@RefreshScope
@Component
public class IpLimitFilter implements GlobalFilter, Ordered {

    @Resource
    RedisTemplate redisTemplate;
    /**
     * IP限制配置
     */
    @Resource
    IpLimiter ipLimiter;
    /**
     * IP限制管理
     */
    @Resource
    IpFilterManager ipFilterManager;


    /**
     * getRedisScript 读取脚本工具类
     * 这里设置为Long,是因为ipLimiter.lua 脚本返回的是数字类型
     */
    private DefaultRedisScript<Long> getRedisScript;


    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("ipLimiter.lua")));
        log.info("IpLimitHandler[分布式限流处理器]脚本加载完成");
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String limitIp = IpUtil.getIpAddr(request);
        //获取请求路径
        String url = request.getPath().toString();
        boolean limitVisit = ipFilterManager.checkIp(limitIp);
        log.info("[IpLimitFilter] 用户IP:{}, 请求URL:{}", limitIp, url);
        if (limitVisit) {
            log.error("[IpLimitFilter] IP: {}, 命中黑名单策略，请求疑似机器人，请稍后再试", limitIp);
            String errMessage = "{\"code\":\"error\",\"message\":\"你的请求疑似机器人，请稍后再试\"}";
            byte[] bits = errMessage.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }

        /**
         * 执行Lua脚本
         */
        List<String> ipList = new ArrayList();
        // 设置key值为注解中的值
        String key = IpBloomFilterConfig.IP_LIMIT_FILTER.concat(url).concat(IpBloomFilterConfig.SIGN).concat(limitIp);
        ipList.add(key);

        /**
         * 调用脚本并执行
         */
        Long result = (Long) redisTemplate.execute(getRedisScript, ipList, ipLimiter.getExpireTime(), ipLimiter.getLimitTimes());
        if (result != null && result == 0) {
            // 检测该ip是否要放入全局黑名单
            ipFilterManager.checkAndAddBlackIp(limitIp);
//            // TODO 放进全局黑名单【用于后续观察】
//            if (blackList != null) {
//                redisTemplate.opsForValue().increment(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST + limitIp);
//            } else {
//                // 拉黑30分钟
//                redisTemplate.opsForValue().set(IpBloomFilterConfig.IP_LIMIT_BLACK_LIST + limitIp, 1, 30, TimeUnit.MINUTES);
//            }

            String msg = "由于超过单位时间=" + ipLimiter.getExpireTime() + "-允许的请求次数=" + ipLimiter.getLimitTimes() + "[触发限流]";
            log.info("[IpLimitFilter] IP: {}, {}", limitIp, msg);
            // 达到限流返回给前端信息
            String errMessage = "{\"code\":\"error\",\"message\":\"请求过于频繁，请稍后再试\"}";
            byte[] bits = errMessage.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }

        // FeignSecurity擦除特殊的header【将特殊标识的信息给清空，防止非法请求】
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("feignRequest", "").build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();

        // 判断是否设置需要附身的用户
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst(SysConf.Authorization);
        String userUid = headers.getFirst(SysConf.X_USER_UID);
//        log.info("[filter] 判断是否携带用户附身请求头，userUid: " + userUid);
        if (StringUtils.isNotEmpty(userUid)) {
            // 判断是否是门户用户【目前附身只支持门户用户附身】
            String onlineUser = (String) redisTemplate.opsForValue().get(String.format("%s:%s:%s", "LOGIN_TOKEN_KEY", "USER", authHeader));
            // 如果不是门户用户直接放行
            if (StringUtils.isEmpty(onlineUser)) {
                log.info("[filter] 附身的token不是门户用户skip，userUid: " + userUid);
                return chain.filter(mutableExchange);
            }
            // 判断当前登录的用户能否直接附身操作【从参数Key中获取附身的key】
            String redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + SysConf.SYS_AUTH_USER;
            String authUserUidListJson = (String) redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isEmpty(authUserUidListJson)) {
                log.info("[filter] 未配置用户权限列表skip");
                return chain.filter(mutableExchange);
            }
            // 判断当前用户能否附身
            boolean authSuccess = false;
            String[] authUserUidList = authUserUidListJson.split(",");
            for (String authUserUid : authUserUidList) {
                if (onlineUser.contains(authUserUid)) {
                    authSuccess = true;
                }
            }
            // 不能附身，直接跳过
            if (!authSuccess) {
                log.info("[filter]不在附身的白名单内，skip");
                return chain.filter(mutableExchange);
            }
            String token = (String) redisTemplate.opsForValue().get(RedisConf.USER_UID_TO_TOKEN + SysConf.SYMBOL_COLON + userUid);
            if (StringUtils.isNotEmpty(token)) {
                log.info("[filter]附身成功");
                mutableReq = exchange.getRequest().mutate().header(SysConf.Authorization, token).build();
                mutableExchange = exchange.mutate().request(mutableReq).build();
            }
        }

        // 直接放行
        return chain.filter(mutableExchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
