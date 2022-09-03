package com.th1024.community;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.dao.DiscussPostMapper;
import com.th1024.community.dao.elasticsearch.DiscussPostRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author izumisakai
 * @create 2022-08-20 17:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(280));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(281));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(285));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(149, 0, 100, 0));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(152, 0, 100, 0));
    }

    @Test
    public void testUpdate() {
        // 修改id为231的数据
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(231);
        discussPost.setContent("滴滴滴123新人灌水");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDelete() {
        // 删除一条数据
        // discussPostRepository.deleteById(231);
        // 删除全部数据
        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "contene"))/*multiMatchQuery()多个字段同时匹配*/
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC)) /*排序：按type倒序*/
                .withSorts(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSorts(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10)) /*分页操作*/
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em").postTags("/em>"), /*高亮*/
                        new HighlightBuilder.Field("content").preTags("<em").postTags("/em>")
                ).build();

        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(nativeSearchQuery, DiscussPost.class);

        System.out.println("totalHits = " + search.getTotalHits());

        search.getSearchHits().stream().map(SearchHit::getContent).forEach(System.out::println);
    }

}
