package com.th1024.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author izumisakai
 * @create 2022-04-04 18:42
 */

@Repository
@Primary //当以类型从容器中获取bean时，设置优先获取该bean
public class TestDaoMybatisImpl implements TestDao {
    @Override
    public String select() {
        return "Mybatis";
    }
}
