package com.th1024.community.dao;

import com.th1024.community.bean.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author izumisakai
 * @create 2022-07-10 16:32
 */
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);
}
