package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.vo.MessagePushVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 消息推送服务类
 *
 * @author 陌溪
 * @since 2023年4月2日11:09:46
 */
public interface MessagePushService extends SuperService<MessagePush> {

    /**
     * 获取消息推送列表
     *
     * @param messagePushVO
     * @return
     */
    public IPage<MessagePush> getPageList(MessagePushVO messagePushVO);

    /**
     * 新增消息推送
     *
     * @param messagePushVO
     */
    public String addMessagePush(MessagePushVO messagePushVO);

    /**
     * 消息推送
     *
     * @param messagePushVO
     * @return
     */
    public String messagePush(MessagePushVO messagePushVO);

    /**
     * 编辑消息推送
     *
     * @param messagePushVO
     */
    String editMessagePush(MessagePushVO messagePushVO);

    /**
     * 批量删除消息推送
     *
     * @param blogSortVoList
     */
    String deleteBatchMessagePush(List<MessagePushVO> blogSortVoList);

}
