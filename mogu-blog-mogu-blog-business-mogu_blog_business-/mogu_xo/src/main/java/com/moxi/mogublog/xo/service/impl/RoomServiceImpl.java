package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxi.mogublog.commons.entity.Room;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.commons.vo.RoomVO;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.mapper.RoomMapper;
import com.moxi.mogublog.xo.service.RoomService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.DeleteException;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.UpdateException;
import com.moxi.mougblog.base.global.MessageConstant;
import com.moxi.mougblog.base.holder.RequestHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Resource
    private RoomService roomService;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private UserService userService;

    @Override
    public List<Room> queryList(String receiveId, LambdaQueryWrapper<Room> queryWrapper) {
        return roomMapper.queryList(receiveId, queryWrapper);
    }

    @Override
    public IPage<Room> getPageList(RoomVO roomVO) {
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(roomVO.getTitle())) {
            queryWrapper.like(SQLConf.TITLE, roomVO.getTitle());
        }
        if (StringUtils.isNotEmpty(roomVO.getBelongUserId())) {
            queryWrapper.eq(SQLConf.BELONG_USER_ID, roomVO.getBelongUserId());
        }
        if (StringUtils.isNotEmpty(roomVO.getReceiveId())) {
            queryWrapper.eq(SQLConf.RECEIVE_ID, roomVO.getReceiveId());
        }
        if (roomVO.getRoomType() > 0) {
            queryWrapper.eq(SQLConf.ROOM_TYPE, roomVO.getRoomType());
        }

        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if (StringUtils.isNotEmpty(roomVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(roomVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if (StringUtils.isNotEmpty(roomVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(roomVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        Page<Room> page = new Page<>();
        page.setCurrent(roomVO.getCurrentPage());
        page.setSize(roomVO.getPageSize());
        IPage<Room> iPage = roomService.page(page, queryWrapper);
        // 获取聊天记录中，用户的头像
        List<String> userUidList = new ArrayList<>();
        for (Room room : iPage.getRecords()) {
            // 消息发送人
            if (StringUtils.isNotEmpty(room.getBelongUserId())) {
                userUidList.add(room.getBelongUserId());
            }
            // 消息接收人
            if (StringUtils.isNotEmpty(room.getReceiveId())) {
                userUidList.add(room.getReceiveId());
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

        for (Room room : iPage.getRecords()) {
            // 消息发送人
            if (StringUtils.isNotEmpty(room.getBelongUserId())) {
                room.setUser(userMap.get(room.getBelongUserId()));
            }
            // 消息接收人
            if (StringUtils.isNotEmpty(room.getReceiveId())) {
                room.setToUser(userMap.get(room.getReceiveId()));
            }
        }
        return iPage;
    }

    @Override
    public String addRoom(RoomVO roomVO) {
        if (StringUtils.isEmpty(roomVO.getAvatar()) || StringUtils.isEmpty(roomVO.getTitle())) {
            throw new InsertException(MessageConf.PARAM_INCORRECT);
        }
        // 新增聊天室只能创建群聊
        Room room = new Room();
        // 创建群组
        String uid = StringUtils.getUUID();
        room.setUid(uid);
        room.setRoomType(MessageConstant.MESSAGE_TYPE_GROUP);
        room.setBelongUserId(String.valueOf(MessageConstant.MESSAGE_BELONG_PUBLIC));
        room.setReceiveId(uid);
        room.setSessionId(uid);
        room.setTitle(roomVO.getTitle());
        room.setAvatar(roomVO.getAvatar());
        room.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editRoom(RoomVO roomVO) {
        Room room = roomService.getById(roomVO.getUid());
        if (room == null) {
            throw new UpdateException(MessageConf.PARAM_INCORRECT);
        }
        room.setTitle(roomVO.getTitle());
        room.setAvatar(roomVO.getAvatar());
        room.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteRoom(RoomVO roomVO) {
        String userUid = RequestHolder.getUserUid();
        Room room = roomService.getById(roomVO.getUid());
        if (room == null) {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_DEFAULT_ERROR);
        }
        if (room.getUid().equals("10001")) {
            return ResultUtil.errorWithMessage("无法删除官方群");
        }
        if (room.getRoomType() == MessageConstant.MESSAGE_TYPE_GROUP) {
            return ResultUtil.errorWithMessage("无法删除群组");
        }
        if (!userUid.equals(room.getBelongUserId())) {
            return ResultUtil.errorWithMessage("无法删除他人的房间");
        }
        room.setStatus(EStatus.DISABLED);
        room.updateById();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String deleteBatchRoom(List<RoomVO> roomVOList) {
        if (roomVOList.size() == 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        roomVOList.forEach(item -> {
            uids.add(item.getUid());
        });

        Collection<Room> roomCollection = roomService.listByIds(uids);
        roomCollection.forEach(item -> {
            if (item.getUid().equals("10001")) {
                throw new DeleteException("无法删除官方群");
            }
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        boolean save = roomService.updateBatchById(roomCollection);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
