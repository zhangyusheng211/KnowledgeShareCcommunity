//package com.moxi.mogublog.search.config;
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
//    @Around("within(com.moxi.mogublog.search.restapi.*) || within(com.moxi.mogublog.search.service.*))")
//    public Object around(JoinPoint joinPoint) throws Throwable {
//        return aroundExecute(joinPoint);
//    }
//}