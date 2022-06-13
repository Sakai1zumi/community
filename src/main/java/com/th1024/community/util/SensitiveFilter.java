package com.th1024.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

/**
 * 过滤敏感词的工具类
 *
 * @author izumisakai
 * @create 2022-06-13 10:46
 */
@Component
public class SensitiveFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    // 根结点
    private TrieNode root = new TrieNode();

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        // 指针3可能会先到达队尾，因此利用指针3做循环效率会更高一点
        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根结点，将此结果计入结果，让指针2向下走一步
                if (tempNode == root) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 指针2和指针3进入下一个位置
                position = ++begin;
                // 指针1重新指向根结点
                tempNode = root;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词，将begin～position位置的字符串替换掉
                sb.append(REPLACEMENT);
                // 指针2和指针3进入下一个位置
                begin = ++position;
                // 指针1重新指向根结点
                tempNode = root;
            } else {
                // 检查下一个字符
                position++;
            }
        }

        // 将最后一批结果计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断字符是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80 ~ 0x9FFF：东亚文字范围（中文，日文等）
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点（key是下级字符，value是下级节点）
        private Map<Character, TrieNode> subNodes = new TreeMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

    // 前缀树的初始化
    @PostConstruct
    public void init() {
        try (
                InputStream is = SensitiveFilter.class.getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
                // 将敏感词添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            LOGGER.error("加载敏感词信息失败：" + e.getMessage());
        }

    }

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        // 指针先指向根结点
        TrieNode tempNode = root;
        char[] c = keyword.toCharArray();
        for (int i = 0; i < c.length; i++) {
            // 先判断前缀树中有无该敏感词
            TrieNode subNode = tempNode.getSubNode(c[i]);

            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c[i], subNode);
            }
            // 指针指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标志
            if (i == c.length - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }
}
