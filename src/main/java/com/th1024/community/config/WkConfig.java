package com.th1024.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author izumisakai
 * @create 2022-09-03 15:08
 */
@Configuration
public class WkConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    // 初始化文件目录，只需执行一次
    @PostConstruct
    public void init() {
        // 创建wk图片目录
        File file = new File(wkImageStorage);

        if (!file.exists()) {
            file.mkdir();
            LOGGER.info("创建wk图片目录：" + wkImageStorage);
        }
    }
}
