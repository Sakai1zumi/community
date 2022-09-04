package com.th1024.community;

import java.io.IOException;

/**
 * @author izumisakai
 * @create 2022-09-03 14:24
 */
public class WkTests {
    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75 https://www.baidu.com /Users/izumisakai/java/wk-data/wk-images/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            Thread.sleep(5000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
