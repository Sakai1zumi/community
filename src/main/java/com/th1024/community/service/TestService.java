package com.th1024.community.service;

import com.th1024.community.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author izumisakai
 * @create 2022-04-04 23:51
 */

@Service
@Scope("prototype") //设置该Bean为多实例
public class TestService {

    @Autowired
    private TestDao dao;

    public String find() {
        return dao.select();
    }

    public TestService(){
        //System.out.println("实例化testService");
    }

    @PostConstruct
    public void init() { //初始化方法
        //System.out.println("初始化testService");
    }

    @PreDestroy
    public void destroy() { //销毁方法
        //System.out.println("销毁testService");
    }
}
