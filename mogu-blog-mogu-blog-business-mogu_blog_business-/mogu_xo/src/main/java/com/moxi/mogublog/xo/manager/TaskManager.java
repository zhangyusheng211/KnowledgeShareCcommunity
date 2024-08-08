package com.moxi.mogublog.xo.manager;

import com.moxi.mougblog.base.enums.EBusinessType;

/**
 * 任务调度接口
 *
 * @author 遇见
 * @date 2023年4月1日18:18:04
 */
public interface TaskManager {

    /**
     * 刷新点赞和点踩的任务
     *
     * @param businessType 点赞 or 点踩
     * @param isAll        是否全部
     * @return
     */
    void refreshPraiseCountTask(EBusinessType businessType, boolean isAll);

    /**
     * 刷新收藏数量的任务
     *
     * @param isAll
     */
    void refreshCollectCountTask(boolean isAll);

    /**
     * 刷新评论数量的任务
     *
     * @param isAll
     */
    void refreshCommentCountTask(boolean isAll);

    /**
     * 刷新资源下载数量任务
     * @param isAll
     */
    void refreshResourceDownloadCountTask(boolean isAll);

    /**
     * 刷新用戶卡片信息
     * @param
     */
    void refreshUserCardTask();


    /**
     * 删除用户VIP
     */
    void dropUserVip();
}
