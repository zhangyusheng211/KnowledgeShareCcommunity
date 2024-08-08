package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.vo.ImMessageVO;

import java.util.List;

public interface ImService extends IService<ImMessage> {

    /**
     * 页面历史
     *
     * @param imMessage
     * @return
     */
    IPage<ImMessage> pageHistory(ImMessage imMessage);

    /**
     * 后台查看所有聊天记录
     *
     * @param imMessageVO
     * @return
     */
    IPage<ImMessage> getPageList(ImMessageVO imMessageVO);

    /**
     * 批量删除聊天记录
     *
     * @param imMessageVOList
     * @return
     */
    String deleteBatchImMessage(List<ImMessageVO> imMessageVOList);

    /**
     * 获取聊天记录数
     *
     * @param imMessageVO
     * @return
     */
    Integer getMessageCount(ImMessageVO imMessageVO);
}
