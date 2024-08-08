package com.moxi.mogublog.xo.utils;

import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.SysParamsService;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.CharUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词工具类
 *
 * @author: 陌溪
 * @create: 2021-05-04-17:37
 */
@Slf4j
@Component
public class SensitiveUtils implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * 将敏感词替换成 ***
     */
    public static final String REPLACEMENT = "***";

    /**
     * 根节点
     */
    private TrieNode rootNode;
    /**
     * 广告根节点
     */
    private TrieNode slogenRootNode;

    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 是否首次开启
     */
    private Boolean isFirstOpen = true;
    /**
     * 是否首次开启广告过滤
     */
    private Boolean isSlogenFirstOpen = true;

    /**
     * 启动时删除redis缓存， 重新添加TrieNode()
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        redisUtil.delete(RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + SysConf.SYS_SENSITIVE_WORD);
        redisUtil.delete(RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + SysConf.SYS_SENSITIVE_SLOGAN);
    }

    /**
     * 将一个敏感词添加进前缀树中
     *
     * @param keyword
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            // 首先判断是否存在相同子节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                // 添加子节点
                tempNode.addSubNode(c, subNode);
            }
            // 指向子节点，进入下一层循环
            tempNode = subNode;

            // 设置结束标识（叶子节点），表示这个字符是该敏感词的最后一个字符
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 将一个敏感词添加进广告前缀树中
     *
     * @param keyword
     */
    private void addSlogenKeyword(String keyword) {
        TrieNode tempNode = slogenRootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            // 首先判断是否存在相同子节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 初始化子节点
                subNode = new TrieNode();
                // 添加子节点
                tempNode.addSubNode(c, subNode);
            }
            // 指向子节点，进入下一层循环
            tempNode = subNode;

            // 设置结束标识（叶子节点），表示这个字符是该敏感词的最后一个字符
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }


    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本（即用 *** 替代敏感词） 和敏感词出现次数
     */
    public Map<String, String> filter(String text, Boolean flag, String redisKey) {


        // 如何敏感词修改，那么重新构造
        if (!this.checkKeyExist(flag, redisKey)) {
            // 如果 redis中不存在， 那么就 初始化 缓存的节点
            isFirstOpen = true;
            isSlogenFirstOpen = true;
            if (isFirstOpen && !flag) {
                rootNode = new TrieNode();
                String sysSensitiveWords = "";
                sysSensitiveWords = sysParamsService.getSysParamsValueByKey(redisKey);
                String[] keywords = sysSensitiveWords.split(";");
                for (int i = 0; i < keywords.length; i++) {
                    // 添加到前缀树
                    this.addKeyword(keywords[i]);
                }
                isFirstOpen = false;
            }
            if (isSlogenFirstOpen && flag) {
                slogenRootNode = new TrieNode();
                String sysSlogenWords = "";
                sysSlogenWords = sysParamsService.getSysParamsValueByKey(redisKey);
                String[] keywords = sysSlogenWords.split(";");
                for (int i = 0; i < keywords.length; i++) {
                    // 添加到前缀树
                    this.addSlogenKeyword(keywords[i]);
                }
                isSlogenFirstOpen = false;
            }
        }

        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针 1：前缀树的工作指针
        TrieNode tempNode;
        if (flag) {
            tempNode = slogenRootNode;
        } else {
            tempNode = rootNode;
        }
        // 指针 2：指向文本中某个敏感词的第一位
        int begin = 0;
        // 指针 3；指向文本中某个敏感词的最后一位
        int end = 0;
        int count = 0;

        // 记录过滤后的文本（结果）
        StringBuilder sb = new StringBuilder();

        while (end < text.length()) {
            char c = text.charAt(end);
            // 跳过符号（防止敏感词混合符号，比如 ☆垃☆圾）
            if (isSymbol(c)) {
                // 若指针 1 处于根节点，则将此符号计入结果（直接忽略），让指针 2 向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头还是在中间，指针 3 都会向下走一步
                end++;
                continue;
            }

            // 检查子节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以指针 begin 开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一位的判断
                begin++;
                end = begin;
                // 指针 1 重新指向根节点
                if (flag) {
                    tempNode = slogenRootNode;
                } else {
                    tempNode = rootNode;
                }
            } else if (tempNode.isKeywordEnd()) {
                if (!flag) {
                    // 发现敏感词，将 begin~end 的字符串替换掉
                    sb.append(REPLACEMENT);
                }
                // 计算加1
                count++;
                // 进入下一位的判断
                end++;
                if (!flag) {
                    begin = end;
                    // 指针 1 重新指向根节点
                    tempNode = rootNode;
                }
            } else {
                // 检查下一个字符
                end++;
            }
        }

        // 将最后一批字符计入结果（如果最后一次循环的字符串不是敏感词，上述的循环逻辑不会将其加入最终结果）
        sb.append(text.substring(begin));
        Map<String, String> result = new HashMap<>(Constants.NUM_TWO);
        result.put(SysConf.CONTENT, sb.toString());
        result.put(SysConf.COUNT, Integer.toString(count));
        return result;
    }

    /**
     * 检查 敏感词/ 广告词 redis的 key 是否存在
     *
     * @param flag
     * @return true 存在  ；false  不存在
     */
    private boolean checkKeyExist(Boolean flag, String redisKey) {
        if (!flag) {
            redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + redisKey;
        } else {
            //广告敏感词过滤
            redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + redisKey;
        }
        String paramsValue = redisUtil.get(redisKey);
        if (StringUtils.isEmpty(paramsValue)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 判断某个字符是否是符号
     *
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 定义前缀树
     */
    private class TrieNode {
        // 关键词结束标识（叶子节点）
        private boolean isKeywordEnd = false;
        // 子节点(key:子节点字符, value:子节点类型)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

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

}
