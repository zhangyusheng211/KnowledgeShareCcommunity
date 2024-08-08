package com.moxi.mogublog.xo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.moxi.mogublog.commons.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    @Select("select " +
            " room.uid, " +
            " room_type, " +
            " room.receive_id, " +
            " room.belong_user_id, " +
            " room.avatar, " +
            " room.title, " +
            " room.session_id, " +
            " msg.send_time as time, " +
            " concat(msg.from_user_nick_name, ': ', (case when msg.is_withdraw = 1 then '消息已撤回' else msg.message end)) as subtitle, " +

            " readable.unread, " +
            " room.status " +
            "from " +
            " t_room room " +
            "left join ( " +
            " select msg.room_id, msg.from_user_nick_name, msg.message, msg.is_withdraw, msg.send_time FROM t_im_message msg inner join (select max(send_time) as send_time, room_id from t_im_message group by room_id) r on r.room_id = msg.room_id and r.send_time = msg.send_time " +
            ") msg on msg.room_id = room.session_id " +
            "left join ( " +
            " select room_id, to_user_id, count(is_read) as unread from t_im_message where is_read = 0 group by room_id, to_user_id, is_read  " +
            ") readable on readable.room_id = room.session_id and readable.to_user_id = #{receiveId} and status = 1 " +
            " ${ew.customSqlSegment}" +
            " order by room_type desc, send_time desc")
    List<Room> queryList(@Param("receiveId") String receiveId, @Param(Constants.WRAPPER) LambdaQueryWrapper<Room> queryWrapper);
}
