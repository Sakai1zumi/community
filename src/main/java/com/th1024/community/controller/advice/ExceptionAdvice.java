package com.th1024.community.controller.advice;

import com.th1024.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author izumisakai
 * @create 2022-07-29 13:19
 */
@ControllerAdvice(annotations = Controller.class) // 只扫描带有Controller注解的类
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})// 表明需处理Exception类的异常
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器异常：" + e.getMessage());

        for (StackTraceElement element : e.getStackTrace()) {
            LOGGER.error(element.toString());
        }

        // 判断是否是异步请求的固定技巧
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) { // 表示请求为异步请求
            response.setContentType("application/plain;charset=utf-8");// 发送普通文本，需手动转换为字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器出现错误！"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
