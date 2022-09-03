package com.th1024.community.quartz;

import com.th1024.community.bean.DiscussPost;
import com.th1024.community.service.DiscussPostService;
import com.th1024.community.service.ElasticsearchService;
import com.th1024.community.service.LikeService;
import com.th1024.community.util.CommunityConstant;
import com.th1024.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author izumisakai
 * @create 2022-09-02 12:27
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 论坛纪元
    private static final Date EPOCH;

    static {
        try {
            EPOCH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2001-01-31 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元失败");
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            LOGGER.info("没有需要刷新的帖子，任务取消");
            return;
        }

        LOGGER.info("【刷新帖子】任务开始：" + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer)operations.pop());
        }

        LOGGER.info("【刷新帖子】任务结束");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null) {
            LOGGER.error("无法找到id为：[" + postId + "]的帖子");
            return;
        }

        // 是否是精华帖
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1)) + (post.getCreateTime().getTime() - EPOCH.getTime()) / (1000 * 3600 * 24);// 防止出现分数为负数的情况

        // 更新帖子的分数
        discussPostService.updateScore(postId, score);
        // 同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);

    }
}
