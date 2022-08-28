package com.th1024.community.util;

/**
 * @author izumisakai
 * @create 2022-04-25 16:59
 */
public interface CommunityConstant {

    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 重复激活
    int ACTIVATION_REPEAT = 1;

    //激活失败
    int ACTIVATION_FAILURE = 2;

    // 设置记住状态失效时间
    int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24 * 100;

    // 设置默认状态失效时间
    int DEFAULT_EXPIRED_SECONDS = 60 * 60 * 24;

    // 实体类型：帖子
    int ENTITY_TYPE_POST = 1;

    // 实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    // 实体类型：用户
    int ENTITY_TYPE_USER = 3;

    // 主题类型常量
    // 评论
    String TOPIC_COMMENT = "comment";

    // 点赞
    String TOPIC_LIKE = "like";

    // 关注
    String TOPIC_FOLLOW = "follow";

    // 发帖
    String TOPIC_PUBLISH = "publish";

    // 删帖
    String TOPIC_DELETE = "delete";

    // 系统用户id
    int SYSTEM_USER_ID = 1;

    // 权限：普通用户
    String AUTHORITY_USER = "user";

    // 权限：管理员
    String AUTHORITY_ADMIN = "admin";

    // 权限：版主
    String AUTHORITY_MODERATOR = "moderator";

}
