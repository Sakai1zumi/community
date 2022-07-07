package com.th1024.community.controller;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.User;
import com.th1024.community.service.DiscussPostService;
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

import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-06-14 21:24
 */
@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) { // 判断用户是否登录
            return CommunityUtil.getJSONString(403, "您还未登录");
        }

        // 创建DiscussPost对象并设置参数
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 报错的情况之后统一处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.findUser(post.getUserId());
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
