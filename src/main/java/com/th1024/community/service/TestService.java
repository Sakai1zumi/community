package com.th1024.community.service;

import com.th1024.community.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

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

    @Autowired
    private TransactionTemplate transactionTemplate;

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

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save() {

        return "ok";
    }

    public Object save1() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                return "ok";
            }
        });
    }
}
