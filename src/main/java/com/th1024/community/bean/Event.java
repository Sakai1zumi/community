package com.th1024.community.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author izumisakai
 * @create 2022-08-15 11:01
 */
// 事件对象
public class Event {
    private String topic;// 事件的主题
    private int userId;// 触发事件的用户id
    private int entityType;// 触发事件的实体类型
    private int entityId;// 触发事件的实体id
    private int entityUserId;// 触发事件的实体的作者的id
    private Map<String, Object> data = new HashMap<>();// 补充的额外数据

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {// 便于灵活赋值
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
