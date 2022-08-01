package com.th1024.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author izumisakai
 * @create 2022-07-31 00:24
 */
//@Component
//@Aspect
public class TestAspect {

    @Pointcut("execution(* com.th1024.community.service.*.*(..))")// 切入点表达式
    public void pointcut() {
    }

    @Before("pointcut()")// 表示以方法pointcut定义的切点为切入点，在切入点方法执行之前执行
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")// 表示以方法pointcut定义的切点为切入点，在切入点方法执行之后执行
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")// 表示以方法pointcut定义的切点为切入点，在切入点方法返回之后执行
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")// 表示以方法pointcut定义的切点为切入点，在切入点方法发生异常之后执行
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")// 在切入点方法前后都执行逻辑
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around-before");// 执行前的逻辑
        Object obj = joinPoint.proceed();// 调用目标组件的方法
        System.out.println("around-after");// 执行后的逻辑
        return obj;
    }

    /*
    输出顺序
    around-before
    before

    afterReturning
    after
    around-after
     */
}
