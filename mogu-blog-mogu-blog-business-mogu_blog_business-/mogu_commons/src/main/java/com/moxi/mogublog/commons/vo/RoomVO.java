package com.moxi.mogublog.commons.vo;

import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

@Data
public class RoomVO extends BaseVO<RoomVO> {

    // 类型 群组/个人
    private int roomType;

    // 会话所属用户
    private String belongUserId;

    // 接收消息方
    private String receiveId;

    // 会话头像
    private String avatar;

    // 会话标题
    private String title;

    // 会话消息记录id
    private String sessionId;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;
}
