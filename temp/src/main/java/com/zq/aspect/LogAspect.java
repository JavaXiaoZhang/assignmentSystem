package com.zq.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(com.zq.annotation.LogPoint)")
    public void logPointCut(){
        log.info("Pointcut...");
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("around...");
        joinPoint.proceed();
        log.info("around...");
        return "";
    }

    @Before("logPointCut()")
    public void before(){
        log.info("before...");
        System.out.println("before...");
    }

}
