package com.th1024.community.controller;

import com.th1024.community.bean.User;
import com.th1024.community.service.LikeService;
import com.th1024.community.util.CommunityUtil;
import com.th1024.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId);
        // 点赞数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 点赞状态
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", entityLikeStatus);

        return CommunityUtil.getJSONString(0, null, map);
    }
}
