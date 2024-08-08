package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 消息推送表
 *
 * @author 陌溪
 * @date 2023年4月2日10:52:51
 */
@Data
@TableName("t_message_push")
public class MessagePush extends SuperEntity<MessagePush> {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员
     */
    private String adminUid;


    /**
     * 触达状态：1：未执行，2：已触达，3：已失败
     */
    private Integer pushStatus;

    /**
     * 触达方式【列表】: 1: 公告,   2: 站内信  3: 邮件，4: 私信
     */
    private String pushMethod;

    /**
     * 触达范围: 1: 全体人员，2: 指定人员，3: 指定标签
     */
    private Integer pushArea;

    /**
     * 圈选的用户UID列表
     */
    private String pushUserUidList;

    /**
     * 指定用户标签
     */
    private Integer userTag;

    /**
     * 推送标题
     */
    private String title;

    /**
     * 推送内容
     */
    private String content;

    /**
     * 是否发布(1:是，0:否)
     */
    private Integer isPublish;
}
