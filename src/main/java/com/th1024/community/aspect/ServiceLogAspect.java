package com.th1024.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-07-31 00:56
 */
@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.th1024.community.service.*.*(..))")// 切入点表达式
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 记录日志格式：用户[1.2.3.4（ip）]在[xxx（时间）]访问了[com.th1024.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); // 可获取请求相关信息
        HttpServletRequest request = attributes.getRequest(); //获取request对象
        String ip = request.getRemoteHost();// 获取当前访问的请求的ip地址
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());// 获取当前时间
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();// 获取当前执行的方法的包名及方法名
        LOGGER.info(String.format("用户[%s]，在[%s]，访问了[%s]。", ip, now, target));// 记录日志
    }
}
