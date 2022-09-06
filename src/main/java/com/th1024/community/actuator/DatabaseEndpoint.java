package com.th1024.community.actuator;

import com.th1024.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author izumisakai
 * @create 2022-09-05 21:07
 */
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEndpoint.class);

    // 尝试获取与数据库的连接
    @Autowired
    private DataSource dataSource;

    @ReadOperation // 只能通过get请求访问
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection();
                ) {
            return CommunityUtil.getJSONString(0, "获取连接成功！");
        } catch (SQLException throwables) {
            LOGGER.error("获取连接失败：" + throwables.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }
}
