package com.th1024.community.controller.interceptor;

import com.th1024.community.bean.LoginTicket;
import com.th1024.community.bean.User;
import com.th1024.community.service.UserService;
import com.th1024.community.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-05-19 16:26
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取ticket
        String ticket = CookieUtil.getTicket(request, "ticket");

        if (ticket != null) {
            // 获取登录凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 查询凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUser(loginTicket.getUserId());
                // 在本次请求中持有user

            }
        }
        return true;
    }
}
