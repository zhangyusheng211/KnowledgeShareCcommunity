package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.moxi.mogublog.commons.entity.Room;
import com.moxi.mogublog.commons.vo.RoomVO;

import java.util.List;

public interface RoomService extends IService<Room> {

    /**
     * 查询房间
     *
     * @param receiveId
     * @param queryWrapper
     * @return
     */
    List<Room> queryList(String receiveId, LambdaQueryWrapper<Room> queryWrapper);

    /**
     * 后台获取房间列表
     *
     * @param roomVO
     * @return
     */
    IPage<Room> getPageList(RoomVO roomVO);

    /**
     * 新增房间
     *
     * @param roomVO
     * @return
     */
    String addRoom(RoomVO roomVO);

    /**
     * 编辑房间
     *
     * @param roomVO
     * @return
     */
    String editRoom(RoomVO roomVO);


    /**
     * 删除房间
     *
     * @param roomVO
     * @return
     */
    String deleteRoom(RoomVO roomVO);

    /**
     * 批量删除房间
     *
     * @param roomVOList
     * @return
     */
    String deleteBatchRoom(List<RoomVO> roomVOList);
}
