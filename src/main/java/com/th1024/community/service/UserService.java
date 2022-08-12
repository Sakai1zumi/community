package com.th1024.community.service;

import com.th1024.community.bean.LoginTicket;
import com.th1024.community.bean.User;
import com.th1024.community.dao.LoginTicketMapper;
import com.th1024.community.dao.UserMapper;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.CommunityUtil;
import com.th1024.community.util.MailClient;
import com.th1024.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author izumisakai
 * @create 2022-04-21 15:28
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        //return userMapper.selectUserById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }

        return user;
    }

    public User findUserByName(String username) {
        return userMapper.selectUserByName(username);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 判断参数有效性
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 判断参数是否为空
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证账号数据
        User u = userMapper.selectUserByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已被注册");
            return map;
        }
        u = userMapper.selectUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/userId/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "账号激活", content);

        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectUserById(userId);

        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);// 状态发生改变，清除缓存
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 验证用户名
        User user = userMapper.selectUserByName(username);
        if (user == null) {
            map.put("usernameMsg", "用户名不存在");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        // 使用Redis存储登录凭证
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        //loginTicketMapper.updateStatus(ticket, 1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        // return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);// 先进行修改操作
        // 清除缓存
        clearCache(userId);
        return rows;
    }

    // 1. 优先从缓存中取值
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 2. 取不到值则初始化缓存
    private User initCache(int userId) {
        User user = userMapper.selectUserById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);// 注意设置过期时间
        return user;
    }

    // 3. 数据发生变化时清除缓存
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        // 将key删除即可
        redisTemplate.delete(userKey);
    }
}
