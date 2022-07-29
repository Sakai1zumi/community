package com.th1024.community.controller.interceptor;

import com.th1024.community.bean.LoginTicket;
import com.th1024.community.bean.User;
import com.th1024.community.service.UserService;
import com.th1024.community.util.CookieUtil;
import com.th1024.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    private HostHolder hostHolder;

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
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有user(考虑多线程的情况)
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    //在模版引擎之前调用，存入user对象
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
