package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Comment;
import com.moxi.mogublog.commons.entity.Notice;
import com.moxi.mogublog.commons.vo.NoticeVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 通知表 服务类
 *
 * @author 陌溪
 * @date 2021年8月5日23:42:52
 */
public interface NoticeService extends SuperService<Notice> {

    /**
     * 获取通知列表
     *
     * @param noticeVO
     * @return
     */
    IPage<Notice> getPageList(NoticeVO noticeVO);

    /**
     * 新增通知
     *
     * @param noticeVO
     */
    String addNotice(NoticeVO noticeVO);

    /**
     * 批量向关注该用户【管理员】的人发送通知
     *
     * @param noticeVO
     * @return
     */
    String batchAddNoticeByWatch(NoticeVO noticeVO);

    /**
     * 编辑通知
     *
     * @param noticeVO
     */
    String editNotice(NoticeVO noticeVO);

    /**
     * 删除通知
     *
     * @param noticeVO
     */
    String deleteNotice(NoticeVO noticeVO);

    /**
     * 批量删除通知
     *
     * @param NoticeVOList
     */
    String deleteBatchNotice(List<NoticeVO> NoticeVOList);


    /**
     * 获取用户收到的通知数
     *
     * @return
     */
    String getUserReceiveNoticeCount();

    /**
     * 阅读用户的通知数
     *
     * @param
     * @return
     */
    String readUserReceiveNoticeCount();

    /**
     * 获取后台站内信列表
     *
     * @return
     */
    IPage<Notice> getBlackNoticeList(NoticeVO noticeVO);

    /**
     * 删除站内信小红点提示
     *
     * @return
     */
    void deleteRedisBatchNotice(String adminUid);

    /**
     * 获取后台站内信消息数量
     *
     * @return
     */
    Integer searchAllNoticeCount(String adminUid);

    /**
     * 通过评论发送通知
     *
     * @param comment
     */
    boolean sendNoticeByComment(Comment comment);
}
