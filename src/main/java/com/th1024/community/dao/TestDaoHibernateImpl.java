package com.th1024.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author izumisakai
 * @create 2022-04-04 18:21
 */

@Repository("hibernateDao")
public class TestDaoHibernateImpl implements TestDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
