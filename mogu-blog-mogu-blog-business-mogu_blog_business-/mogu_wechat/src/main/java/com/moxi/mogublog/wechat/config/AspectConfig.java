//package com.moxi.mogublog.wechat.config;
//
//import com.plumelog.trace.aspect.AbstractAspect;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Aspect
//@Component
//public class AspectConfig extends AbstractAspect {
//
//    @Around("within(com.moxi.mogublog.wechat.controller.*) || within(com.moxi.mogublog.wechat.handler.*)|| within(com.moxi.mogublog.wechat.service.*))")
//    public Object around(JoinPoint joinPoint) throws Throwable {
//        return aroundExecute(joinPoint);
//    }
//}