package com.th1024.community;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.User;
import com.th1024.community.dao.DiscussPostMapper;
import com.th1024.community.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author izumisakai
 * @create 2022-04-10 17:25
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//设置配置类
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelect() {
        User user = userMapper.selectUserById(101);
        System.out.println(user);

        user = userMapper.selectUserByName("guanyu");
        System.out.println(user);

        user = userMapper.selectUserByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        user.setEmail("zhangsan@123.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/zhangsan.png");
        int i = userMapper.insertUser(user);
        System.out.println(i);
    }

    @Test
    public void testUpdate() {
        userMapper.updateStatus(150, 1);
        userMapper.updateHeader(150, "http://images.nowcoder.com/head/zhangsan111.png");
        userMapper.updatePassword(150, "zhangsan123");
    }

    @Test
    public void testDelete() {
        userMapper.deleteUser(150);
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int i = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(i);
    }
}
