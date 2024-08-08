package com.moxi.mogublog.gateway.handle;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局Sentinel异常处理器
 *
 * @author: 陌溪
 * @date: 2022年8月14日18:15:32
 */
@Configuration
@Slf4j
public class FeignBlockHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        StringBuffer requestURL = httpServletRequest.getRequestURL();
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
        log.error("{}, 请求路径为:{}", message, requestURL);
        String result = "{\"code\":\"401\",\"message\":\"" + message + "\"}";
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(result);
    }
}