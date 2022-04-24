package com.th1024.community.service;

import com.th1024.community.bean.User;
import com.th1024.community.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author izumisakai
 * @create 2022-04-21 15:28
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUser(int id) {
        return userMapper.selectUserById(id);
    }

}
