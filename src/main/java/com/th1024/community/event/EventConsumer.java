package com.th1024.community.event;

import com.alibaba.fastjson.JSONObject;
import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.Event;
import com.th1024.community.bean.Message;
import com.th1024.community.service.DiscussPostService;
import com.th1024.community.service.ElasticsearchService;
import com.th1024.community.service.MessageService;
import com.th1024.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author izumisakai
 * @create 2022-08-15 11:21
 */
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 消费事件消息
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        // 对参数进行判空处理
        if (record == null || record.value() == null) {
            LOGGER.error("消息内容为空");
            return;
        }
        // 将事件消息还原为事件对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        // 补充内容信息
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        // 转换为JSON字符串存储
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖和给帖子评论事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("消息内容为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            LOGGER.error("消息格式错误！");
            return;
        }

        // 将帖子存入es服务器中
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }
}
