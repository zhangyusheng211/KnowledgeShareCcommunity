package com.moxi.mogublog.sms.event;

import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.feign.SmsFeignClient;
import com.moxi.mogublog.commons.schema.ChatResponse.ChatGPTSetting;
import com.moxi.mogublog.sms.global.RedisConf;
import com.moxi.mogublog.utils.*;
import com.moxi.mogublog.xo.service.CommentService;
import com.moxi.mogublog.xo.service.NoticeService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.enums.EAuditStatus;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.enums.ENoticeType;
import com.moxi.mougblog.base.enums.EResourceType;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AT事件处理器
 */

@Slf4j
@Component
public class AtEventHandler {


    @Resource
    private UserService userService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private NoticeService noticeService;
    @Resource
    private CommentService commentService;
    @Resource
    private SmsFeignClient smsFeignClient;

    /**
     * at事件通用处理方法
     *
     * @param message
     */
    public void atEventHandler(String userUid, String toUserUid, String bizUid, String bizType, String message) {
        this.atEventHandler(userUid, toUserUid, null, bizUid, bizType, message, true);
    }

    /**
     * at事件通用处理方法
     *
     * @param message
     */
    public void atEventHandler(String userUid, String toUserUid, String toUid, String bizUid, String bizType, String message, boolean isAt) {
        // 判断消息中，是否有AT请求
        log.info("消息处理: {}", message);

        // 获取发送的用户信息
        User user = userService.getById(userUid);

        // 判断被AT的是否是机器人
        Map<String, User> robotUserMap = userService.getRobotUserList();
        User toUser = robotUserMap.get(toUserUid);
        String chatGPTResult = "";
        // 判断是否是机器人回复
        if (toUser != null) {
            // 开始进行机器人回复
            // 获取ChatGPT配置
            ChatGPTSetting chatGPTSetting = userService.checkRobotReply(user.getUid(), user.getUserTag());

            String regex = "<a[^>]*>[^<]*<\\/a>";
            String result = message.replaceAll(regex, "");
            Document doc = Jsoup.parse(result);
            String question = doc.text();

            // 是否开启
            if (chatGPTSetting == null || !chatGPTSetting.isOpenRobotReply()) {
                chatGPTResult = "抱歉，后台暂未开启智能问答，请联系管理员";
            } else {
                // 开启了问答，校验是否还有提问次数
                if (chatGPTSetting.isBlock()) {
                    chatGPTResult = "抱歉，今日机器人回复您的次数已达到上限，暂时无法提供问答服务";
                } else {
//                    chatGPTResult = HttpRequestUtil.askOpenAi(question, chatGPTSetting.getApiUrl(), chatGPTSetting.getApiKey(), userUid);
                    chatGPTResult = smsFeignClient.getTextAnswer(question);
                    // 没有回复内容，可能内部系统存在问题
                    if (StringUtils.isEmpty(chatGPTResult)) {
                        chatGPTResult = "抱歉，网络出现问题，请稍后再试";
                    } else {
                        chatGPTResult = chatGPTResult.replaceFirst("\\n", "").replaceFirst("\\n", "");

                        // redis计数器增加
                        redisUtil.setEx(RedisConf.CHATGPT_REPLY_COUNT + Constants.SYMBOL_COLON + user.getUid(), JsonUtils.objectToJson(chatGPTSetting.getLimitCount() + 1), 12, TimeUnit.HOURS);
                    }
                }
            }

            // 将markdown替换
            chatGPTResult = FileUtils.markdownToHtml(chatGPTResult);
            // 构造评论，进行回复
            Comment comment = new Comment();
            comment.setToUid(toUid);
            comment.setSource(bizType);
            comment.setBlogUid(bizUid);
            comment.setUserUid(toUser.getUid());
            // 当该评论不是一级评论时，需要设置一级评论UID字段
            // 非一级评论，才需要设置toUserUid
            if (StringUtils.isNotEmpty(toUid)) {
                Comment toComment = commentService.getById(toUid);
                assert toComment != null;
                // 表示 toComment 是非一级评论
                if (StringUtils.isNotEmpty(toComment.getFirstCommentUid())) {
                    comment.setFirstCommentUid(toComment.getFirstCommentUid());
                } else {
                    // 表示父评论是一级评论，直接获取UID
                    comment.setFirstCommentUid(toComment.getUid());
                }
                comment.setToUserUid(user.getUid());
            }
            comment.setContent(chatGPTResult);
            comment.setAuditStatus(EAuditStatus.AGREE);
            comment.setAuditName("系统");
            comment.setAuditTime(new Date());
            comment.insert();

            // 发送评论对应的通知
            noticeService.sendNoticeByComment(comment);
            return;
        }

        // 不是AT请求，不记录AT通知
        if (!isAt) {
            return;
        }

        // 不是机器人，可能是AT的普通用户，需要给用户发送通知
        // 记录被AT的通知
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setNoticeType(ENoticeType.SYSTEM.getCode());
        EResourceType resourceType = EResourceType.getResourceByComment(bizType);
        EBusinessType businessType = EResourceType.resourceType2BusinessType(resourceType, 3);
        noticeVO.setBusinessType(businessType.getCode());
        noticeVO.setBusinessUid(bizUid);
        noticeVO.setUserUid(toUserUid);
        noticeVO.setContent(message);
        noticeVO.setIsBlack(0);
        noticeService.addNotice(noticeVO);

        // 记录小红点
        String redisKey = RedisConf.USER_RECEIVE_SYSTEM_NOTICE_COUNT + Constants.SYMBOL_COLON + toUserUid;
        String count = redisUtil.get(redisKey);
        if (StringUtils.isNotEmpty(count)) {
            redisUtil.incrBy(redisKey, Constants.NUM_ONE);
        } else {
            redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
        }
    }
}
