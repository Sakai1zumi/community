package com.th1024.community.controller;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.bean.Page;
import com.th1024.community.service.ElasticsearchService;
import com.th1024.community.service.LikeService;
import com.th1024.community.service.UserService;
import com.th1024.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author izumisakai
 * @create 2022-08-25 01:37
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        SearchHits<DiscussPost> searchHits = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        // 得到查询返回的内容
        List<SearchHit<DiscussPost>> hits = searchHits.getSearchHits();
        // 设置一个最后需要返回的实体类的集合
        List<DiscussPost> postList = new ArrayList<>();

        // 遍历返回的内容进行处理
        for (SearchHit<DiscussPost> hit : hits) {
            // 高亮的内容
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            // 将高亮的内容替换到原来的实体
            hit.getContent().setTitle(highlightFields.get("title") == null ?
                    hit.getContent().getTitle() : highlightFields.get("title").get(0));
            hit.getContent().setContent(highlightFields.get("content") == null ?
                    hit.getContent().getContent() : highlightFields.get("content").get(0));

            // 放入实体类中
            postList.add(hit.getContent());
        }


        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (!postList.isEmpty()) {
            for (DiscussPost discussPost : postList) {
                Map<String, Object> ma1 = new HashMap<>();
                // 帖子
                ma1.put("post", discussPost);
                // 作者
                ma1.put("user", userService.findUserById(discussPost.getUserId()));
                // 点赞数量
                ma1.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
                discussPosts.add(ma1);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        // 传输给页面，在地址栏中回显
        model.addAttribute("keyword", keyword);

        // 设置分页信息
        long totalCount = searchHits.getTotalHits();
        page.setUrl("/search?keyword=" + keyword);
        page.setRows((int) totalCount);

        return "/site/search";
    }
}
