package com.th1024.community;

import com.th1024.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author izumisakai
 * @create 2022-04-24 13:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMailSend() {
        mailClient.sendMail("1248394192@qq.com", "TEST", "Test");
    }

    @Test
    public void testHTMLMail() {
        Context context = new Context();
        context.setVariable("username", "WH");

        String content = templateEngine.process("/mail/demomail", context);

        mailClient.sendMail("1248394192@qq.com", "HTML", content);
    }

}
