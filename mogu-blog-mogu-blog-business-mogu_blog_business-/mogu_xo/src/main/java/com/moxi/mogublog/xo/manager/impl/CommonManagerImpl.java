package com.moxi.mogublog.xo.manager.impl;

import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.manager.CommonManager;
import com.moxi.mogublog.xo.mapper.*;
import com.moxi.mogublog.xo.service.SecretConfigService;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.enums.ESecretType;
import com.moxi.mougblog.base.enums.EThirdSecretType;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用模块业务处理
 *
 * @author 陌溪
 * @date 2022年3月27日23:24:06
 */
@Service
@Slf4j
public class CommonManagerImpl implements CommonManager {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserMomentMapper userMomentMapper;
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private ProblemMapper problemMapper;
    @Resource
    private ResourceMapper resourceMapper;
    @Resource
    private SecretConfigService secretConfigService;

    // 网站门户页面
    @Value(value = "${data.webSite.url}")
    private String WEB_SITE_URL;


    @Override
    public String getUserUidByResource(EResourceType resourceType, String resourceUid) {
        String userUid = "";
        switch (resourceType) {
            case BLOG: {
                Blog blog = blogMapper.selectById(resourceUid);
                if (blog != null) {
                    userUid = blog.getUserUid();
                }
            }
            break;
            case Question: {
                Question question = questionMapper.selectById(resourceUid);
                if (question != null) {
                    userUid = question.getUserUid();
                }
            }
            break;
            case MEDIA: {

            }
            break;
            case MOMENT: {
                UserMoment userMoment = userMomentMapper.selectById(resourceUid);
                if (userMoment != null) {
                    userUid = userMoment.getUserUid();
                }
            }
            break;
            case COMMENT: {
                Comment comment = commentMapper.selectById(resourceUid);
                if (comment != null) {
                    userUid = comment.getUserUid();
                }
            }
            break;
            case PROBLEM: {
                Problem problem = problemMapper.selectById(resourceUid);
                if (problem != null) {
                    userUid = problem.getUserUid();
                }
            }
            break;
            case RESOURCE: {
                com.moxi.mogublog.commons.entity.Resource resource = resourceMapper.selectById(resourceUid);
                if (resource != null) {
                    userUid = resource.getUserUid();
                }
            }
            break;
            default:
                throw new InsertException("类型转换异常: resourceType:" + resourceType);
        }
        return userUid;
    }

    @Override
    public String pushSEOByResource(EResourceType resourceType, List<String> resourceUidList) {

        // 获取百度SEO密钥
        SecretConfig secretConfig = secretConfigService.getSecretConfig(ESecretType.THIRD_SECRET.getType(), EThirdSecretType.BAIDU_SEO.getType());
        String baiduUrl = secretConfig.getRequestUrl();
        String token = secretConfig.getBizSecret();
        // 获取需要推送的URL列表
        List<String> pushUrlList = formatResourceRequestUrlList(resourceType, resourceUidList);
        // 开始进行推送
        String tempResult = "";
        for (int i = pushUrlList.size() - 1; i >= 0; i--) {
            if (i == pushUrlList.size() - 1) {
                tempResult += pushUrlList.get(i).trim();
            } else {
                tempResult += (pushUrlList.get(i).trim() + "\n");
            }
        }
        return pushBaidu(baiduUrl, WEB_SITE_URL, token, tempResult);
    }

    private List<String> formatResourceRequestUrlList(EResourceType resourceType, List<String> resourceUidList) {
        List<String> pushUrlList = new ArrayList<>();
        switch (resourceType) {
            case BLOG: {
                List<Blog> blogList = blogMapper.selectBatchIds(resourceUidList);
                for (Blog blog : blogList) {
                    pushUrlList.add(WEB_SITE_URL + "info/" + blog.getOid());
                }

            }
            break;
            case Question: {
                List<Question> questionList = questionMapper.selectBatchIds(resourceUidList);
                for (Question question : questionList) {
                    pushUrlList.add(WEB_SITE_URL + "qInfo/" + question.getOid());
                }
            }
            break;
            case PROBLEM: {
                List<Problem> problemList = problemMapper.selectBatchIds(resourceUidList);
                for (Problem problem : problemList) {
                    pushUrlList.add(WEB_SITE_URL + "cInfo/" + problem.getOid());
                }
            }
            break;
            case RESOURCE: {


                List<com.moxi.mogublog.commons.entity.Resource> resourceList = resourceMapper.selectBatchIds(resourceUidList);
                for (com.moxi.mogublog.commons.entity.Resource resource : resourceList) {
                    pushUrlList.add(WEB_SITE_URL + "resource/" + resource.getUid());
                }
            }
            break;
        }
        return pushUrlList;
    }

    public String pushBaidu(String baiduSite, String site, String token, String urlsStr) {
        StringBuilder result = new StringBuilder();
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //建立URL之间的连接
            URLConnection conn = new URL(baiduSite + "?site=" + site + "&token=" + token).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host", "data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            out.print(urlsStr);
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.warn("推送到百度SEO异常", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
