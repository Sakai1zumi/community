package com.th1024.community;

import com.th1024.community.dao.TestDao;
import com.th1024.community.service.TestService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//设置配置类
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    //传入ioc容器
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplication() {
        TestDao testDao = applicationContext.getBean(TestDao.class);
        System.out.println(testDao);
        testDao = applicationContext.getBean("hibernateDao", TestDao.class);//以名字和类型获取bean
        System.out.println(testDao);
    }

    @Test
    public void testBeanManagement() {
        TestService testService = applicationContext.getBean("testService", TestService.class);
        System.out.println(testService);
        testService = applicationContext.getBean("testService", TestService.class);
        System.out.println(testService);
    }

    @Test
    public void testConfig() {
        SimpleDateFormat dateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(dateFormat.format(new Date()));
    }

    //DI：依赖注入
    @Autowired
    @Qualifier(value = "hibernateDao") //根据指定的名称进行注入
    public TestDao dao;

    @Autowired
    public TestService service;

    @Autowired
    public SimpleDateFormat format;

    @Test
    public void testDI() {
        System.out.println(dao);
        System.out.println(service);
        System.out.println(format.format(new Date()));
    }
}
