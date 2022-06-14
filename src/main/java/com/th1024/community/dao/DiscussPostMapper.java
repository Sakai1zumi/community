package com.th1024.community.dao;

import com.th1024.community.bean.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author izumisakai
 * @create 2022-04-20 20:55
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param("userId") 给参数取别名
    //如果方法只有一个参数，并且需要在<if>中使用（即动态sql），则必须要取别名
    int selectDiscussPostRows(@Param("userId") int userId); //

    int insertDiscussPost(DiscussPost discussPost);
}
