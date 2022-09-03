package com.th1024.community.controller;

import com.th1024.community.bean.Comment;
import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.Event;
import com.th1024.community.event.EventProducer;
import com.th1024.community.service.CommentService;
import com.th1024.community.service.DiscussPostService;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.HostHolder;
import com.th1024.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-07-15 13:00
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        // 根据评论的对象的不同，查询对象的作者
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发给帖子评论事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId)
                    .setUserId(comment.getUserId());
            eventProducer.fireEvent(event);

            // 计算帖子的分数
            String postScoreKey = RedisKeyUtil.getPostScoreKey();
            // 应将数据存储在set中，无序的不可重复的数据类型
            redisTemplate.opsForSet().add(postScoreKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
