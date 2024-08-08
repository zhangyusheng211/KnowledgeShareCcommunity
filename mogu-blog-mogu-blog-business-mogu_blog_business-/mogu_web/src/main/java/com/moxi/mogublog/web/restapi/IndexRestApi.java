package com.moxi.mogublog.web.restapi;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.annotion.FeignSecurity.FeignSecurity;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.BlogSort;
import com.moxi.mogublog.commons.entity.Link;
import com.moxi.mogublog.commons.entity.Tag;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.annotion.log.SysLogHandle;
import com.moxi.mogublog.web.chat.holder.SessionManager;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.manager.TaskManager;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.commons.vo.BlogVO;
import com.moxi.mogublog.commons.vo.ImMessageVO;
import com.moxi.mogublog.commons.vo.UserVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.MessageConstant;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.vo.BehaviorVO;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 首页 RestApi
 *
 * @author 陌溪
 * @since 2018-09-04
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页相关接口", tags = {"首页相关接口"})
@Slf4j
public class IndexRestApi {

    @Resource
    private TagService tagService;
    @Resource
    private LinkService linkService;
    @Resource
    private BlogSortService blogSortService;
    @Resource
    private WebConfigService webConfigService;
    @Resource
    private SysParamsService sysParamsService;
    @Resource
    private BlogService blogService;
    @Resource
    private WebNavbarService webNavbarService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private CollectService collectService;
    @Resource
    private UserPraiseRecordService userPraiseRecordService;
    @Resource
    private TaskManager taskManager;
    @Resource
    private SmsFeignClient smsFeignClient;

    @ApiOperation(value = "通过推荐等级获取博客列表", notes = "通过推荐等级获取博客列表")
    @GetMapping("/getBlogByLevel")
    public String getBlogByLevel(@ApiParam(name = "level", value = "推荐等级", required = false) @RequestParam(name = "level", required = false, defaultValue = "0") Integer level,
                                 @ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                 @ApiParam(name = "useSort", value = "使用排序", required = false) @RequestParam(name = "useSort", required = false, defaultValue = "0") Integer useSort) {

        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogPageByLevel(level, currentPage, useSort));
    }

    @ApiOperation(value = "获取首页排行博客", notes = "获取首页排行博客")
    @GetMapping("/getHotBlog")
    public String getHotBlog() {

        log.info("获取首页排行博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getHotBlog());
    }

    @ApiOperation(value = "获取首页最新的博客", notes = "获取首页最新的博客")
    @PostMapping("/getNewBlog")
    public String getNewBlog(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取首页最新的博客");
        blogVO.setIsOnlySubjectShow(Constants.NUM_ZERO);
        IPage<Blog> blogPage = blogService.getNewBlog(blogVO);
        return ResultUtil.result(SysConf.SUCCESS, blogPage);
    }

    @ApiOperation(value = "mogu-search调用获取博客的接口[包含内容]", notes = "mogu-search调用获取博客的接口")
    @GetMapping("/getBlogBySearch")
    public String getBlogBySearch(@ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogBySearch(currentPage, null));
    }

    @ApiOperation(value = "按时间戳获取博客", notes = "按时间戳获取博客")
    @GetMapping("/getBlogByTime")
    public String getBlogByTime(@ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogByTime(currentPage, Long.valueOf(blogNewCount)));
    }

    @ApiOperation(value = "获取最热标签", notes = "获取最热标签")
    @GetMapping("/getHotTag")
    public String getHotTag() {
        String hotTagCount = sysParamsService.getSysParamsValueByKey(SysConf.HOT_TAG_COUNT);
        // 从Redis中获取最热标签
        String jsonResult = redisUtil.get(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConf.SUCCESS, jsonResult2List);
        }
        List<Tag> tagList = tagService.getHotTag(Integer.valueOf(hotTagCount));
        if (tagList.size() > 0) {
            redisUtil.setEx(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount, JsonUtils.objectToJson(tagList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, tagList);
    }

    @ApiOperation(value = "获取最热分类", notes = "获取最热分类")
    @GetMapping("/getHotBlogSort")
    public String getHotBlogSort() {
        String hotBlogSortCount = sysParamsService.getSysParamsValueByKey(SysConf.HOT_BLOG_SORT_COUNT);
        // 从Redis中获取最热分类
        String jsonResult = redisUtil.get(RedisConf.BLOG_SORT + Constants.SYMBOL_COLON + hotBlogSortCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConf.SUCCESS, jsonResult2List);
        }
        List<BlogSort> blogSortList = blogSortService.getHotBlogSort(Integer.valueOf(hotBlogSortCount));
        if (blogSortList.size() > 0) {
            redisUtil.setEx(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotBlogSortCount, JsonUtils.objectToJson(blogSortList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, blogSortList);
    }

    @ApiOperation(value = "获取友情链接", notes = "获取友情链接")
    @GetMapping("/getLink")
    public String getLink() {
        String friendlyLinkCount = sysParamsService.getSysParamsValueByKey(SysConf.FRIENDLY_LINK_COUNT);
        // 从Redis中获取友情链接
        String jsonResult = redisUtil.get(RedisConf.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConf.SUCCESS, jsonResult2List);
        }
        List<Link> linkList = linkService.getListByPageSize(Integer.valueOf(friendlyLinkCount));
        if (linkList.size() > 0) {
            redisUtil.setEx(RedisConf.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount, JsonUtils.objectToJson(linkList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, linkList);
    }

    @BussinessLog(value = "点击友情链接", behavior = EBehavior.FRIENDSHIP_LINK)
    @ApiOperation(value = "增加友情链接点击数", notes = "增加友情链接点击数")
    @GetMapping("/addLinkCount")
    public String addLinkCount(@ApiParam(name = "uid", value = "友情链接UID", required = false) @RequestParam(name = "uid", required = false) String uid) {
        log.info("点击友链");
        return linkService.addLinkCount(uid);
    }

    @ApiOperation(value = "获取网站配置", notes = "获取友情链接")
    @GetMapping("/getWebConfig")
    public String getWebConfig() {
        log.info("获取网站配置");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @ApiOperation(value = "获取网站导航栏", notes = "获取网站导航栏")
    @GetMapping("/getWebNavbar")
    public String getWebNavbar() {
        log.info("获取网站导航栏");
        return ResultUtil.result(SysConf.SUCCESS, webNavbarService.getAllList());
    }

    @BussinessLog(value = "记录访问页面", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation(value = "记录访问页面", notes = "记录访问页面")
    @GetMapping("/recorderVisitPage")
    public String recorderVisitPage(@ApiParam(name = "pageName", value = "页面名称", required = false) @RequestParam(name = "pageName", required = false) String pageName) {

        if (StringUtils.isEmpty(pageName)) {
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @ApiOperation(value = "用户行为上报", notes = "用户行为上报")
    @PostMapping("/recorderBehavior")
    public String recorderBehavior(HttpServletRequest request, @RequestBody BehaviorVO behaviorVO) {
        String userUid = RequestHolder.getUserUid();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        String browser = map.get(SysConf.BROWSER);
        String ip = IpUtils.getIpAddr(request);
        String bizType = "";
        if (behaviorVO.getBizType() != null) {
            bizType = behaviorVO.getBizType().getType();
        }
        // 异步存储日志
        threadPoolTaskExecutor.execute(
                new SysLogHandle(userUid, ip, os, browser,
                        behaviorVO.getBehavior().getBehavior(),
                        behaviorVO.getBizUid(),
                        behaviorVO.getOtherData(), redisUtil, bizType));

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }


    @ApiOperation(value = "获取首页积分排行榜", notes = "获取首页积分排行榜")
    @PostMapping("/getUserTopN")
    public String getUserTopN(@Validated({GetList.class}) @RequestBody UserVO userVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取首页积分排行榜");
        return ResultUtil.successWithData(userService.getUserTopN(userVO));
    }

    /**
     * 收藏/点赞刷数
     * @return
     */
    @ApiOperation(value = "收藏刷数", notes = "收藏刷数")
    @GetMapping("/refreshCollect")
    public String refreshCollect() {
        log.info("获取首页积分排行榜");
        return ResultUtil.successWithData(collectService.refreshCollect());
    }

    @ApiOperation(value = "收藏/点赞刷数", notes = "收藏/点赞刷数")
    @GetMapping("/refreshPraise")
    public String refreshPraise() {
        log.info("获取首页积分排行榜");
        return ResultUtil.successWithData(userPraiseRecordService.refreshPraise());
    }

    /**
     * 发送领域事件
     * @param message
     * @param userUid
     * @return
     */
    @FeignSecurity
    @PostMapping("/sendDomainEventMessage")
    public String sendDomainEventMessage(String message, String userUid) {
        log.info("[sendDomainEventMessage] 发送消息到前端");
        if (StringUtils.isNotEmpty(userUid)) {
            // 推动给指定用户
            if (SessionManager.channelMap.containsKey(userUid)) {
                Channel channel = SessionManager.channelMap.get(userUid);
                ImMessageVO imMessageVO = new ImMessageVO();
                imMessageVO.setFromUserId(userUid);
                imMessageVO.setMessage(message);
                imMessageVO.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DOMAIN_EVENT);
                // 发送消息
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessageVO)));
            }
        } else {
            // 推送给所有人
            Map<String, Channel> channelMap = SessionManager.channelMap;
            for (String item: channelMap.keySet()) {
                Channel channel = SessionManager.channelMap.get(item);
                ImMessageVO imMessageVO = new ImMessageVO();
                imMessageVO.setFromUserId(item);
                imMessageVO.setMessage(message);
                imMessageVO.setMessageLevel(MessageConstant.MESSAGE_LEVEL_DOMAIN_EVENT);
                // 发送消息
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(imMessageVO)));
            }
        }

        return ResultUtil.successWithMessage("发送成功");
    }


    /**
     * chatGPT自动回复
     * @param openid
     * @param message
     * @return
     */
    @FeignSecurity
    @PostMapping("/chatGPTReply")
    String chatGPTReplayText(@RequestParam(value = "openid") String openid, @RequestParam(value = "message")String message) {
        return userService.chatGPTReply(openid, message);
    }


    /**
     * 更新博客信息【点赞、点踩、评论、收藏数】
     * @return
     */
    @PostMapping("/updateBlogInfoAll")
    String updateBlogInfoAll() {
        taskManager.refreshPraiseCountTask(EBusinessType.PRAISE, true);
        taskManager.refreshPraiseCountTask(EBusinessType.TREAD, true);
        taskManager.refreshCommentCountTask(true);
        taskManager.refreshCollectCountTask(true);
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    /**
     * 微信自动回复
     * @param question
     * @param apiUrl
     * @param apiKey
     * @return
     */
    @GetMapping("/autoChat")
    public String autoChat(@RequestParam(value = "question") String question,
                           @RequestParam(value = "apiUrl") String apiUrl,
                           @RequestParam(value = "apiKey") String apiKey) {
//        String result = HttpRequestUtil.askOpenAi(question, apiUrl, apiKey, SysConf.DEFAULT_UID);
        String result = smsFeignClient.getTextAnswer(question);
        return ResultUtil.successWithData(result);
    }

    /**
     * 获取版本参数
     * @return
     */
    @GetMapping("/getVersionParams")
    public String getVersionParams() {
        String versionParams = sysParamsService.getSysParamsValueByKey(SysConf.SYS_VERSION_PARAMS);
        return ResultUtil.successWithData(versionParams);
    }
}

