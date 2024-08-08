package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * <p>
 * 收藏表
 * </p>
 *
 * @author 陌溪
 * @since 2018-09-08
 */
@Data
@TableName("t_collect")
public class Collect extends SuperEntity<Collect> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户的uid
     */
    private String userUid;

    /**
     * 被收藏用户uid
     */
    private String toUserUid;

    /**
     * 业务uid
     */
    private String bizUid;
    /**
     * 收藏的类型
     */
    private String collectType;

    /**
     * 所属的用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 收藏的博客
     */
    @TableField(exist = false)
    private Blog blog;

    /**
     * 收藏的问答
     */
    @TableField(exist = false)
    private Question question;

    /**
     * 收藏的动态
     */
    @TableField(exist = false)
    private UserMoment userMoment;

    /**
     * 收藏的动态
     */
    @TableField(exist = false)
    private Problem problem;

    /**
     * 收藏的资源
     */
    @TableField(exist = false)
    private Resource resource;
}
