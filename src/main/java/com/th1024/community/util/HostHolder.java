package com.th1024.community.util;

import com.th1024.community.bean.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替session对象
 *
 * @author izumisakai
 * @create 2022-06-06 14:45
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    /*
    Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            map.set(this, value);
        } else {
            createMap(t, value);
        }
        将对象存入每个线程对应的map中
     */


    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
