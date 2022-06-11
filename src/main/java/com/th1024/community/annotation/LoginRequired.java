package com.th1024.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author izumisakai
 * @create 2022-06-11 00:19
 */

// 表明该注解可标注在方法上
@Target(ElementType.METHOD)
// 表明该注解在程序运行时有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
