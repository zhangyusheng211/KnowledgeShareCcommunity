package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * @author 遇见
 */
@TableName("t_user_massage")
@Data
public class UserMessage extends SuperEntity<UserMessage> {
    private String uid;
    /**
     * 发送人
     */
    private String sendUserUid;
    /**
     * 发送人昵称
     */
    private String sendUserName;
    /**
     * 发送人头像
     */
    private String avatar;
    /**
     * 接收人
     */
    private String receiveUserUid;
    /**
     * 组ID
     */
    private String groupUid;
    /**
     * 是否已读 0 未读  1 已读
     */
    private Integer isRead;
    /**
     * 类型 0 单聊消息  1 群消息
     */
    private Integer type=0;
    /**
     * 消息内容
     */
    private String content;
}
