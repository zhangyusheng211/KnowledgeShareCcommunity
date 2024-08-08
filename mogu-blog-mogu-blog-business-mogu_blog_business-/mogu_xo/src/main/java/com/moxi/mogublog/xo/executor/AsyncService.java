package com.moxi.mogublog.xo.executor;

import com.moxi.mogublog.commons.schema.AggEsDoc;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.enums.EBusinessType;
import com.moxi.mougblog.base.enums.ENoticeType;
import com.moxi.mougblog.base.enums.EOperate;

/**
 * 异步任务服务
 *
 * @author 遇见
 */
public interface AsyncService {

    /**
     * 异步执行
     * 发送通知
     */
    void executeAsyncBusinessNotice(NoticeVO noticeVO);

    /**
     * 向用户发送系统通知
     *
     * @param userUid
     * @param content
     */
    void executeAsyncBusinessNotice(String userUid, String content);

    /**
     * 异步执行通知
     * 向所有人发送博客发布站内信
     *
     * @param isAdmin     是否是管理员
     * @param uid         用户Uid
     * @param businessUid 业务Uid
     */
    void executeAsyncBusinessNotice(Boolean isAdmin, String uid, String businessUid, Integer businessTypeCode);

    /**
     * 异步执行
     * 向指定人发送通知站内信
     *
     * @param userUid
     * @param noticeType
     * @param businessType
     * @param resourceUid
     */
    void executeAsyncNotice(String userUid, ENoticeType noticeType, EBusinessType businessType, String resourceUid);

    /**
     * 异步更新更新ES索引和缓存
     *
     * @param aggEsDoc
     */
    void executeAsyncUpdateEsAndRedis(AggEsDoc aggEsDoc, EOperate operate);

    /**
     * 发送后台站内信通知
     *
     * @param isAdmin
     * @param uid
     * @param businessUid
     * @param businessTypeCode
     */
    void executeAsyncBusinessBlackNotice(Boolean isAdmin, String uid, String businessUid, Integer businessTypeCode, String content);
}
