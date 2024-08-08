package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 热搜表
 * @author 陌溪
 * @date 2022年6月17日21:42:26
 */
@Data
@TableName("t_hot_search")
public class HotSearch extends SuperEntity<HotSearch> {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索内容
     */
    private String content;

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 用户UuserIp
     */
    private String userIp;

    /**
     * 搜索频次合计
     */
    @TableField(exist = false)
    private Integer hotCount;

}
