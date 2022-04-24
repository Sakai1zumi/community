package com.th1024.community.dao;

import com.th1024.community.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author izumisakai
 * @create 2022-04-10 16:34
 */

@Mapper //添加@Mapper注解，将该类添加到容器中
public interface UserMapper {

    User selectUserById(int id);

    User selectUserByName(String username);

    User selectUserByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

    int deleteUser(int id);
}
