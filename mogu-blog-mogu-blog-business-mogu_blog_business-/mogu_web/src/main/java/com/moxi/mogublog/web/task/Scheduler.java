package com.moxi.mogublog.web.task;

import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.manager.TaskManager;
import com.moxi.mogublog.xo.service.HotSerchService;
import com.moxi.mogublog.xo.service.UserEquityRecordService;
import com.moxi.mogublog.xo.service.UserPraiseRecordService;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.global.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 凌晨定时任务
 *
 * @author: 陌溪
 * @create: 2021-11-28-8:59
 */
@Slf4j
@Component
@EnableScheduling
public class Scheduler {

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserEquityRecordService userEquityRecordService;
    @Resource
    TaskManager taskManager;


    //每天0：00执行
    @Scheduled(cron = "0 00 00 ? * *")
    public void clearTasks() {
        log.info("##########凌晨定时任务开始执行【更新单日数据】##########");
        // 获取用户点击博客
        Set<String> blogClickKeys = redisUtil.keys(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 获取用户点击问答
        Set<String> questionClickKeys = redisUtil.keys(RedisConf.QUESTION_CLICK + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 获取用户点击标签
        Set<String> tagClickKeys = redisUtil.keys(RedisConf.TAG_CLICK + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 获取用户发布博客
        Set<String> publishBlogClickKeys = redisUtil.keys(RedisConf.USER_PUBLISH_BLOG_COUNT + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 获取用户发布问答
        Set<String> publishQuestionClickKeys = redisUtil.keys(RedisConf.USER_PUBLISH_QUESTION_COUNT + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 获取用户增加积分限制
        Set<String> creditsClickKeys = redisUtil.keys(RedisConf.USER_CREDITS_ADD_LIMIT + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 仪表盘
        Set<String> dashBoardKeys = redisUtil.keys(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // TopN用户
        Set<String> usertopNKeys = redisUtil.keys(RedisConf.INDEX_TOP_N_USER + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);
        // 用户签到月份信息
        Set<String> userSignInRecordMonth = redisUtil.keys(RedisConf.USER_SIGN_IN_RECORD_MONTH + Constants.SYMBOL_COLON + Constants.SYMBOL_STAR);

        List<String> allKeys = new ArrayList<>();
        allKeys.addAll(blogClickKeys);
        allKeys.addAll(questionClickKeys);
        allKeys.addAll(tagClickKeys);
        allKeys.addAll(publishBlogClickKeys);
        allKeys.addAll(publishQuestionClickKeys);
        allKeys.addAll(creditsClickKeys);
        allKeys.addAll(dashBoardKeys);
        allKeys.addAll(usertopNKeys);
        allKeys.addAll(userSignInRecordMonth);
        redisUtil.delete(allKeys);
        log.info("##########凌晨定时任务执行完毕##########");
    }

    /**
     * 每天0点清除用户VIP
     */
//    @Scheduled(cron = "0 00 00 ? * *")
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void dropUserVip() {
        taskManager.dropUserVip();
        log.info("##########会员掉落执行完毕##########");
    }

    /**
     * 每周发签到卡【按排行榜TopN进行发放】
     * 每周一的0点开始执行
     * 参考cron在线表达式：https://cron.qqe2.com/
     * 定时发放任务关闭
     */
    @Scheduled(cron = "0 0 0 ? * 1")
//    @Scheduled(cron = "0 30 9 ? * *")
    public void sendSignInCardTask() {
        log.info("##########开始执行发放签到卡任务##########");
//        userEquityRecordService.batchSendSignInCard();
        log.info("##########发放签到卡任务结束##########");
    }

    /**
     *  每年1月1日零时 删除万历年节假日信息缓存
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void deleteCalendarList() {
        redisUtil.delete(RedisConf.CALENDAR_LIST);
    }

    /**
     * 更新博客点赞、收藏、评论数据【每小时更新一次】
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updateBlogInfo() {
        log.info("##########开始执行数据更新任务##########");
        taskManager.refreshPraiseCountTask(EBusinessType.PRAISE, false);
        taskManager.refreshPraiseCountTask(EBusinessType.TREAD, false);
        taskManager.refreshCommentCountTask(false);
        taskManager.refreshCollectCountTask(false);
        taskManager.refreshResourceDownloadCountTask(false);
        log.info("##########数据更新任务##########");
    }

    /**
     * 更新全量博客点赞、收藏、评论数据【每隔两天凌晨一点同步更新一次全量数据】
     * 该数据将用户首页排序展示
     */
    @Scheduled(cron = "0 0 1 1/2 * ? ")
    public void updateBlogInfoAll() {
        log.info("##########开始执行数据更新任务##########");
        taskManager.refreshPraiseCountTask(EBusinessType.PRAISE, true);
        taskManager.refreshPraiseCountTask(EBusinessType.TREAD, true);
        taskManager.refreshCommentCountTask(true);
        taskManager.refreshCollectCountTask(true);
        taskManager.refreshResourceDownloadCountTask(true);
        log.info("##########数据更新任务##########");
    }

    /**
     * 刷新用户缓存
     */
    @Scheduled(cron = "0 0 0 ? * 1")
    public void refreshUserCache() {
        // 首页会存在用户卡片，加载比较慢容易影响用户体验，因此将前置将用户的卡片信息缓存起来
        taskManager.refreshUserCardTask();
    }

}
