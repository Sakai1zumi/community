package com.th1024.community.service;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.Page;
import com.th1024.community.dao.elasticsearch.DiscussPostRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author izumisakai
 * @create 2022-08-24 21:19
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    // 存储
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    // 删除
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    // 搜索
    public SearchHits<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {

        // 查询的字段，构建查询条件
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current, limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        return elasticsearchRestTemplate.search(nativeSearchQuery, DiscussPost.class);
    }
}
