package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxi.mogublog.commons.entity.ImMessage;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.ImMessageVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.ImMapper;
import com.moxi.mogublog.xo.service.ImService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.utils.FileFeignUtil;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.Constants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service(value = "imService")
public class ImServiceImpl extends ServiceImpl<ImMapper, ImMessage> implements ImService {
    @Resource
    private FileFeignUtil fileFeignUtil;
    @Resource
    private ImService imService;
    @Resource
    private UserService userService;

    @Override
    public IPage<ImMessage> pageHistory(ImMessage imMessage) {
        LambdaQueryWrapper<ImMessage> queryWrapper = new LambdaQueryWrapper<>();
        if (imMessage.getRoomId() == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 通过roomId查询历史聊天记录
        queryWrapper.eq(ImMessage::getRoomId, imMessage.getRoomId());
        queryWrapper.orderByDesc(ImMessage::getSendTime);
        queryWrapper.eq(ImMessage::getStatus, EStatus.ENABLE);
        Page<ImMessage> page = new Page<>();
        page.setCurrent(imMessage.getPage());
        page.setSize(imMessage.getPageSize());
        IPage<ImMessage> pageResult = page(page, queryWrapper);
        Collections.reverse(pageResult.getRecords());
        // 获取聊天记录中，用户的头像
        List<String> userUidList = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();
        for (ImMessage message : pageResult.getRecords()) {
            if (StringUtils.isNotEmpty(message.getFromUserId())) {
                userUidList.add(message.getFromUserId());
            }
            if (userUidList.size() > 0) {
                List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
                for (User user : userList) {
                    userMap.put(user.getUid(), user);
                }
            }
        }

        for (ImMessage message : pageResult.getRecords()) {
            // 判断消息是否撤回
            if (message.getIsWithdraw() == Constants.NUM_ONE) {
                message.setMessage("消息已撤回");
            }

            if (StringUtils.isNotEmpty(message.getFromUserId())) {
                User user = userMap.get(message.getFromUserId());
                if (user != null) {
                    message.setFormUserAvatar(user.getPhotoUrl());
                    message.setUserTag(user.getUserTag());
                    message.setUserLevel(user.getUserLevel());
                    message.setGender(user.getGender());
                    message.setCredits(user.getCredits());
                    message.setUserIpPossession(user.getUserIpPossession());
                }
            }
        }
        return pageResult;
    }

    @Override
    public IPage<ImMessage> getPageList(ImMessageVO imMessageVO) {
        QueryWrapper<ImMessage> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(imMessageVO.getMessage())) {
            queryWrapper.like(SQLConf.MESSAGE, imMessageVO.getMessage());
        }
        if (StringUtils.isNotEmpty(imMessageVO.getFromUserNickName())) {
            queryWrapper.like(SQLConf.FROM_USER_NICK_NAME, imMessageVO.getFromUserNickName());
        }
        if (StringUtils.isNotEmpty(imMessageVO.getFromUserId())) {
            queryWrapper.eq(SQLConf.FROM_USER_ID, imMessageVO.getFromUserId());
        }
        if (StringUtils.isNotEmpty(imMessageVO.getToUserId())) {
            queryWrapper.eq(SQLConf.TO_USER_ID, imMessageVO.getToUserId());
        }
        if (StringUtils.isNotEmpty(imMessageVO.getRoomId())) {
            queryWrapper.eq(SQLConf.ROOM_ID, imMessageVO.getRoomId());
        }
        if (imMessageVO.getMessageLevel() != null) {
            queryWrapper.eq(SQLConf.MESSAGE_LEVEL, imMessageVO.getMessageLevel());
        }
        if (imMessageVO.getCategory() != null) {
            queryWrapper.eq(SQLConf.CATEGORY, imMessageVO.getCategory());
        }
        if (imMessageVO.getMessageType() != null) {
            queryWrapper.eq(SQLConf.MESSAGE_TYPE, imMessageVO.getMessageType());
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(imMessageVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(imMessageVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(imMessageVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(imMessageVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        Page<ImMessage> page = new Page<>();
        page.setCurrent(imMessageVO.getCurrentPage());
        page.setSize(imMessageVO.getPageSize());
        IPage<ImMessage> imMessageIPage = imService.page(page, queryWrapper);
        // 获取聊天记录中，用户的头像
        List<String> userUidList = new ArrayList<>();
        for (ImMessage message : imMessageIPage.getRecords()) {
            // 消息发送人
            if (StringUtils.isNotEmpty(message.getFromUserId())) {
                userUidList.add(message.getFromUserId());
            }
            // 消息接收人
            if (StringUtils.isNotEmpty(message.getToUserId())) {
                userUidList.add(message.getToUserId());
            }
        }
        // 获取用户uid
        Map<String, User> userMap = new HashMap<>();
        if (userUidList.size() > 0) {
            List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
            for (User user : userList) {
                userMap.put(user.getUid(), user);
            }
        }

        for (ImMessage message : imMessageIPage.getRecords()) {
            // 消息发送人
            if (StringUtils.isNotEmpty(message.getFromUserId())) {
                message.setFromUser(userMap.get(message.getFromUserId()));
            }
            // 消息接收人
            if (StringUtils.isNotEmpty(message.getToUserId())) {
                message.setToUser(userMap.get(message.getToUserId()));
            }
        }
        return imMessageIPage;
    }

    @Override
    public String deleteBatchImMessage(List<ImMessageVO> imMessageVOList) {
        if (imMessageVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        imMessageVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<ImMessage> imMessageCollection = imService.listByIds(uids);
        imMessageCollection.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = imService.updateBatchById(imMessageCollection);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public Integer getMessageCount(ImMessageVO imMessageVO) {
        QueryWrapper<ImMessage> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(imMessageVO.getFromUserId())) {
            queryWrapper.eq("from_user_id", imMessageVO.getFromUserId());
        }
        if (StringUtils.isNotEmpty(imMessageVO.getToUserId())) {
            queryWrapper.eq("to_user_id", imMessageVO.getToUserId());
        }

        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        return imService.count(queryWrapper);
    }
}
