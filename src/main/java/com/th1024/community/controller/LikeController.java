package com.th1024.community.controller;

import com.th1024.community.bean.Event;
import com.th1024.community.bean.User;
import com.th1024.community.event.EventProducer;
import com.th1024.community.service.LikeService;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.CommunityUtil;
import com.th1024.community.util.HostHolder;
import com.th1024.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author izumisakai
 * @create 2022-08-02 14:36
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 点赞数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 点赞状态
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", entityLikeStatus);

        // 触发点赞事件
        if (entityLikeStatus == 1) {// 只在点赞时触发事件
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        if (entityType == ENTITY_TYPE_POST) {
            // 计算帖子的分数
            String postScoreKey = RedisKeyUtil.getPostScoreKey();
            // 应将数据存储在set中，无序的不可重复的数据类型
            redisTemplate.opsForSet().add(postScoreKey, postId);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
