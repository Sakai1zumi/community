package com.th1024.community.dao;

import com.th1024.community.bean.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author izumisakai
 * @create 2022-07-27 10:57
 */
@Mapper
public interface MessageMapper {
    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信，支持分页
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的所有私信
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信数量
    /*
    若conversationId不为空则表示查询某个会话的未读消息数量
    若为空则表示查询所有的未读消息数量
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    // 发送私信
    int insertMessage(Message message);

    // 修改状态
    int updateStatus(List<Integer> ids, int status);
}
