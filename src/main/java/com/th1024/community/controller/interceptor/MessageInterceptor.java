package com.th1024.community.controller.interceptor;

import com.th1024.community.bean.User;
import com.th1024.community.service.MessageService;
import com.th1024.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author izumisakai
 * @create 2022-08-17 00:29
 */
// 拦截请求，查询当前用户的未读消息数量
@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    // 调用Controller之后，模板之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
