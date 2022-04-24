package com.th1024.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author izumisakai
 * @create 2022-04-22 18:47
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class loggerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(loggerTest.class);

    @Test
    public void testLogger() {
        LOGGER.debug("log debug");
        LOGGER.info("log info");
        LOGGER.error("log error");
    }

}
