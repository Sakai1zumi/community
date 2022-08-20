package com.th1024.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.th1024.community.bean.Message;
import com.th1024.community.bean.Page;
import com.th1024.community.bean.User;
import com.th1024.community.service.MessageService;
import com.th1024.community.service.UserService;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.CommunityUtil;
import com.th1024.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author izumisakai
 * @create 2022-07-27 23:08
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表页面
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
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
        // 查询未读的通知的数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        // 返回模板位置链接
        return "/site/letter";
    }

    //私信详情页面
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
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

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
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

    // 通知列表请求
    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        // 获取当前用户
        User user = hostHolder.getUser();

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        // 补充额外信息
        Map<String, Object> messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            // 转义HTML字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            // 补充额外信息
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            // 查询通知总数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", count);

            // 查询未读通知数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unreadCount", unreadCount);
        }
        model.addAttribute("commentNotice", messageVO);

        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        // 补充额外信息
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            // 转义HTML字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            // 补充额外信息
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            // 查询通知总数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", count);

            // 查询未读通知数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unreadCount", unreadCount);
        }
        model.addAttribute("likeNotice", messageVO);

        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        // 补充额外信息
        messageVO = new HashMap<>();
        messageVO.put("message", message);
        if (message != null) {

            // 转义HTML字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            // 补充额外信息
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));

            // 查询通知总数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", count);

            // 查询未读通知数量
            int unreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unreadCount", unreadCount);
        }
        model.addAttribute("followNotice", messageVO);

        // 查询未读私信数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 查询未读通知数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    // 通知详情请求
    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setUrl("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());// 获取通知详情数据
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();

                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 触发通知的用户
                map.put("fromUser", userService.findUserById(notice.getFromId()));

                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);

        // 设置已读
        List<Integer> ids = getUnreadIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }
}
