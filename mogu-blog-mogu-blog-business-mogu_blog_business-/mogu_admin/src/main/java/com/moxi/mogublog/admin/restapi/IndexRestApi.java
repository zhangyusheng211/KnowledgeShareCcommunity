package com.moxi.mogublog.admin.restapi;

import com.moxi.mogublog.utils.JsonUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.CommentService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.service.WebVisitService;
import com.moxi.mogublog.commons.vo.CommentVO;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 首页RestApi
 *
 * @author 陌溪
 * @date 2018年10月22日下午3:27:24
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页相关接口", tags = {"首页相关接口"})
@Slf4j
public class IndexRestApi {

    @Resource
    private BlogService blogService;
    @Resource
    private CommentService commentService;
    @Resource
    private WebVisitService webVisitService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;

    @ApiOperation(value = "首页初始化数据", notes = "首页初始化数据", response = String.class)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {

        Map<String, Object> map = new HashMap<>(Constants.NUM_FOUR);

        String blogCount = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT);
        if (StringUtils.isNotEmpty(blogCount)) {
            map.put(SysConf.BLOG_COUNT, Integer.valueOf(blogCount));
        } else {
            Integer count = blogService.getBlogCount(EStatus.ENABLE);
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT, JsonUtils.objectToJson(count), 10, TimeUnit.MINUTES);
            map.put(SysConf.BLOG_COUNT, count);
        }

        String commentCount = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.COMMENT_COUNT);
        if (StringUtils.isNotEmpty(commentCount)) {
            map.put(SysConf.COMMENT_COUNT, Integer.valueOf(commentCount));
        } else {
            CommentVO commentVO = new CommentVO();
            Integer count = commentService.getCommentCount(commentVO);
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.COMMENT_COUNT, JsonUtils.objectToJson(count), 10, TimeUnit.MINUTES);
            map.put(SysConf.COMMENT_COUNT, count);
        }

        String userCount = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.USER_COUNT);
        if (StringUtils.isNotEmpty(userCount)) {
            map.put(SysConf.USER_COUNT, Integer.valueOf(userCount));
        } else {
            Integer count = userService.getUserCount(EStatus.ENABLE);
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.USER_COUNT, JsonUtils.objectToJson(count), 10, TimeUnit.MINUTES);
            map.put(SysConf.USER_COUNT, count);
        }

        String visitCount = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.VISIT_COUNT);
        if (StringUtils.isNotEmpty(userCount)) {
            map.put(SysConf.VISIT_COUNT, Integer.valueOf(visitCount));
        } else {
            Integer count = webVisitService.getWebVisitCount();
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.VISIT_COUNT, JsonUtils.objectToJson(count), 10, TimeUnit.MINUTES);
            map.put(SysConf.VISIT_COUNT, count);
        }

        return ResultUtil.result(SysConf.SUCCESS, map);
    }

    @ApiOperation(value = "获取最近一周用户独立IP数和访问量", notes = "获取最近一周用户独立IP数和访问量", response = String.class)
    @RequestMapping(value = "/getVisitByWeek", method = RequestMethod.GET)
    public String getVisitByWeek() {
        Map<String, Object> visitByWeek = webVisitService.getVisitByWeek();
        return ResultUtil.result(SysConf.SUCCESS, visitByWeek);
    }

    @ApiOperation(value = "获取每个标签下文章数目", notes = "获取每个标签下文章数目", response = String.class)
    @RequestMapping(value = "/getBlogCountByTag", method = RequestMethod.GET)
    public String getBlogCountByTag() {
        List<Map<String, Object>> blogCountByTag = blogService.getBlogCountByTag();
        return ResultUtil.result(SysConf.SUCCESS, blogCountByTag);
    }

    @ApiOperation(value = "获取每个分类下文章数目", notes = "获取每个分类下文章数目", response = String.class)
    @RequestMapping(value = "/getBlogCountByBlogSort", method = RequestMethod.GET)
    public String getBlogCountByBlogSort() {

        List<Map<String, Object>> blogCountByTag = blogService.getBlogCountByBlogSort();
        return ResultUtil.result(SysConf.SUCCESS, blogCountByTag);
    }

    @ApiOperation(value = "获取一年内的文章贡献数", notes = "获取一年内的文章贡献度", response = String.class)
    @RequestMapping(value = "/getBlogContributeCount", method = RequestMethod.GET)
    public String getBlogContributeCount() {

        Map<String, Object> resultMap = blogService.getBlogContributeCount();
        return ResultUtil.result(SysConf.SUCCESS, resultMap);
    }
}
