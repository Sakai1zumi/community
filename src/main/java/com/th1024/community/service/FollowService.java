package com.th1024.community.service;

import com.th1024.community.bean.User;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author izumisakai
 * @create 2022-08-06 20:19
 */
@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    // 关注
    public void follow(int userId, int entityType, int entityId) {
        // 由于关注与增加粉丝为相关的两个业务行为，因此需要进行事务管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    // 取消关注
    public void unfollow(int userId, int entityType, int entityId) {
        // 由于关注与增加粉丝为相关的两个业务行为，因此需要进行事务管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                redisTemplate.opsForZSet().remove(followeeKey, entityId);
                redisTemplate.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    // 查询某个用户关注的实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询某个实体的粉丝数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已关注当前实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询某个用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);

        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);// 查询从开始到结尾的索引的数据

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> followeesList = new ArrayList<>();
        for (int id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User u = userService.findUserById(id);
            map.put("user", u);
            Double followTime = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(followTime.longValue()));
            followeesList.add(map);
        }

        return followeesList;
    }

    // 查询某个用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);

        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);// 查询从开始到结尾的索引的数据

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> followersList = new ArrayList<>();
        for (int id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User u = userService.findUserById(id);
            map.put("user", u);
            Double followTime = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(followTime.longValue()));
            followersList.add(map);
        }

        return followersList;
    }
}
