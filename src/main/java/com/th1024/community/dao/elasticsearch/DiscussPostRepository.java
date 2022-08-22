package com.th1024.community.dao.elasticsearch;

import com.th1024.community.bean.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author izumisakai
 * @create 2022-08-20 17:16
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
