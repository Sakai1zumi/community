package com.th1024.community.controller;

import com.th1024.community.bean.Message;
import com.th1024.community.bean.Page;
import com.th1024.community.bean.User;
import com.th1024.community.service.MessageService;
import com.th1024.community.service.UserService;
import com.th1024.community.util.CommunityUtil;
import com.th1024.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.expression.Ids;

import java.util.*;

/**
 * @author izumisakai
 * @create 2022-07-27 23:08
 */
@Controller
@RequestMapping(path = "/letter")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表页面
    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        // 获取当前用户
        User user = hostHolder.getUser();

        // 设置分页信息
        page.setLimit(5);
        page.setUrl("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 查询会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        // 对获得的数据进行重构，补充额外的数据，如总未读消息数量，会话未读消息数量，会话私信数量，以及会话的用户信息等
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);// 加入会话列表信息
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));// 加入会话私信数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));// 加入会话未读消息数量
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));// 加入会话的对方用户的信息

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询总未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 返回模板位置链接
        return "/site/letter";
    }

    //私信详情页面
    @RequestMapping(path = "/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setUrl("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 查询私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));// 页面需要显示发信人的相关信息
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //页面需要显示，来自"某某用户"的私信，因此需要获取当前私信对话的用户的信息
        model.addAttribute("target", getTargetUser(conversationId));

        // 设置所有未读的私信为已读
        List<Integer> ids = getUnreadIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getTargetUser(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getUnreadIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                // 只有在当前用户是接收者，并且消息确实是未读时，才加入结果集
                if (message.getStatus() == 0 && message.getToId() == hostHolder.getUser().getId()) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/send", method = RequestMethod.POST)
    @ResponseBody
    public String addLetter(String toName, String content) {
        User toUser = userService.findUserByName(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(1, "用户不存在，发送失败");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(toUser.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }
}
