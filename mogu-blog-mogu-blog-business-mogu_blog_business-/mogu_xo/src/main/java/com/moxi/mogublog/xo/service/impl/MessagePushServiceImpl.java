package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.domainEvent.EventAction;
import com.moxi.mogublog.commons.entity.MessagePush;
import com.moxi.mogublog.commons.vo.MessagePushVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.MessagePushMapper;
import com.moxi.mogublog.xo.service.MessagePushService;
import com.moxi.mogublog.xo.utils.DomainEventUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 博客分类表 服务实现类
 *
 * @author 陌溪
 * @since 2018-09-08
 */
@Service
public class MessagePushServiceImpl extends SuperServiceImpl<MessagePushMapper, MessagePush> implements MessagePushService {

    @Resource
    private MessagePushService messagePushService;
    @Resource
    private DomainEventUtil domainEventUtil;

    @Override
    public IPage<MessagePush> getPageList(MessagePushVO messagePushVO) {
        QueryWrapper<MessagePush> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(messagePushVO.getKeyword()) && !StringUtils.isEmpty(messagePushVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, messagePushVO.getKeyword().trim());
        }
        if (messagePushVO.getPushMethod() != null) {
            queryWrapper.like(SQLConf.PUSH_METHOD, messagePushVO.getPushMethod());
        }
        if (StringUtils.isNotEmpty(messagePushVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(messagePushVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(messagePushVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(messagePushVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }
        Page<MessagePush> page = new Page<>();
        page.setCurrent(messagePushVO.getCurrentPage());
        page.setSize(messagePushVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        IPage<MessagePush> pageList = messagePushService.page(page, queryWrapper);
        return pageList;
    }

    @Override
    public String addMessagePush(MessagePushVO messagePushVO) {
        MessagePush messagePush = new MessagePush();
        // 使用Spring工具类提供的深拷贝进行实体类赋值
        BeanUtils.copyProperties(messagePushVO, messagePush, SysConf.STATUS);
        messagePush.setAdminUid(RequestHolder.getAdminUid());
        messagePush.insert();
        // 发送触达变更领域事件
        domainEventUtil.publishEvent(EventAction.MESSAGE_PUSH_ADD, messagePush);
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String messagePush(MessagePushVO messagePushVO) {
        // 发送触达变更领域事件
        MessagePush messagePush = messagePushService.getById(messagePushVO.getUid());
        if (messagePush == null) {
            throw new QueryException(MessageConf.ENTITY_NOT_EXIST);
        }
        domainEventUtil.publishEvent(EventAction.MESSAGE_PUSH, messagePush);
        return ResultUtil.successWithMessage("推送成功");
    }

    @Override
    public String editMessagePush(MessagePushVO messagePushVO) {
        MessagePush messagePush = messagePushService.getById(messagePushVO.getUid());
        // 使用Spring工具类提供的深拷贝进行实体类赋值
        BeanUtils.copyProperties(messagePushVO, messagePush, SysConf.STATUS);
        messagePush.setUpdateTime(new Date());
        messagePush.setAdminUid(RequestHolder.getAdminUid());
        messagePush.updateById();
        // 发送触达变更领域事件
        domainEventUtil.publishEvent(EventAction.MESSAGE_PUSH_EDIT, messagePush);
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchMessagePush(List<MessagePushVO> messagePushVoList) {
        if (messagePushVoList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();

        messagePushVoList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<MessagePush> messagePushList = messagePushService.listByIds(uids);
        messagePushList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
            domainEventUtil.publishEvent(EventAction.MESSAGE_PUSH_DELETE, item);
        });
        Boolean save = messagePushService.updateBatchById(messagePushList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }


}
