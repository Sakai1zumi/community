package com.th1024.community.config;

import com.th1024.community.quartz.TestJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @author izumisakai
 * @create 2022-09-01 21:46
 */
// 配置 -> 初始化到数据库 -> 从数据库中调用
@Configuration
public class QuartzConfig {
    // FactoryBean可简化Bean的实例化过程
    // 1. FactoryBean封装了Bean的实例化过程
    // 2. 将FactoryBean装配到Spring容器中
    // 3. 将FactoryBean注入到其他的Bean中
    // 4. 其他的Bean得到的是FactoryBean所管理的对象实例

    // 配置JobDetail
    // @Bean
    public JobDetailFactoryBean testJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(TestJob.class);
        factoryBean.setName("TestJob");
        factoryBean.setGroup("testJobGroup");
        factoryBean.setDurability(true);// 声明任务是否长久保存
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger()
    // SimpleTriggerFactoryBean：简单任务；CronTriggerFactoryBean：复杂任务
    // @Bean
    public SimpleTriggerFactoryBean testTrigger(JobDetail testJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();

        factoryBean.setJobDetail(testJobDetail);
        factoryBean.setName("testTrigger");
        factoryBean.setGroup("testTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
