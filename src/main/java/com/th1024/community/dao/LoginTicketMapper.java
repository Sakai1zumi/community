package com.th1024.community.dao;

import com.th1024.community.bean.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author izumisakai
 * @create 2022-05-06 14:09
 */

@Deprecated
@Mapper
public interface LoginTicketMapper {

    // 使用注解完成sql语句的拼写

    @Insert({
            "insert into login_ticket(`user_id`, `ticket`, `status`, `expired`) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select `id`, `user_id`, `ticket`, `status`, `expired` ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "<script>",
            "update login_ticket set `status` = #{status} where `ticket` = #{ticket} ",
            // if 标签的写法
            "<if test = \"ticket!= null\"> ",
            "and 1 = 1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
